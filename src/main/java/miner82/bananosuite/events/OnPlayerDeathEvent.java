package miner82.bananosuite.events;

import miner82.bananosuite.BananoSuitePlugin;
import miner82.bananosuite.classes.*;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import miner82.bananosuite.runnables.PaymentProcessor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OnPlayerDeathEvent implements Listener {

    private final IDBConnection db;
    private final ConfigEngine configEngine;
    private final Economy econ;

    public OnPlayerDeathEvent(IDBConnection db, ConfigEngine configEngine, Economy econ) {
        this.db = db;
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent args) {

        if(!this.configEngine.getDeathInsuranceEnabled()) {
            return;
        }

        Player player = args.getEntity();

        try {

            PlayerRecord playerRecord = this.db.getPlayerRecord(player);

            if (playerRecord == null) {

                return;

            }

            DeathInsuranceOption insuranceOption = playerRecord.getDeathInsuranceOption();

            if (insuranceOption != DeathInsuranceOption.None) {

                // Calculate the insurance amount
                double basePremium = this.configEngine.getBaseDeathInsurancePremium();
                double premium = DeathInsuranceManager.CalculateNextPremium(this.db, this.configEngine, playerRecord, insuranceOption);
                double feePaid = 0;
                String errorMessage = "";
                boolean activateDeathInsurance = false;

                if (basePremium > 0
                        && premium == 0) {

                    player.sendMessage(ChatColor.GOLD + "Your first death insurance premium each week is free. Thanks for being a citizen!");
                    activateDeathInsurance = true;

                }

                if (premium > 0) {

                    if (econ.has(player, premium)) {

                        new PaymentProcessor(econ,
                                player,
                                premium,
                                new HashMap<String,Object>(),
                                this::DeathInsuranceCallback)
                                .runTaskAsynchronously(BananoSuitePlugin.getPlugin(BananoSuitePlugin.class));

                        activateDeathInsurance = true;

                    } else {

                        player.sendMessage(ChatColor.RED + "Your balance is insufficient to cover your Death Insurance Premium!");

                        return;

                    }

                }

                if (activateDeathInsurance) {

                    playerRecord.setLastDIPolicyUsage(LocalDateTime.now());

                    db.save(playerRecord);
                    db.recordPlayerInsuredDeath(player, insuranceOption, feePaid, premium, args.getDeathMessage(), errorMessage);

                    args.setKeepInventory(true);
                    args.getDrops().clear();

                    if (insuranceOption == DeathInsuranceOption.Full) {

                        args.setKeepLevel(true);
                        args.setDroppedExp(0);

                    }

                    args.setDeathMessage(player.getDisplayName() + " was saved from an excruciating death by Death Insurance!");

                    player.sendMessage(ChatColor.AQUA + "Your next insurance premium will cost " + econ.format(DeathInsuranceManager.CalculateNextPremium(this.db, this.configEngine, playerRecord, insuranceOption)) + " and the cost will increase based on the number of deaths in a 24 hour period.");

                    if (econ.getBalance(player) < DeathInsuranceManager.CalculateNextPremium(this.db, this.configEngine, playerRecord, insuranceOption)) {

                        player.sendMessage(ChatColor.GOLD + "Your balance is low and may not cover your next Death Insurance premium!");

                    }

                }
            }

        }
        catch (Exception ex) {

            ex.printStackTrace();

        }

    }

    private void DeathInsuranceCallback(PaymentCallbackParameters parameters) {

        Player player = parameters.getSender();
        double premium = parameters.getAmount();

        if(parameters.getTransactionWasSuccessful()) {

            player.sendMessage(ChatColor.GREEN + "Your insurance premium of " + econ.format(premium) + " was collected successfully. Thank you!");

        }
        else {

            player.sendMessage(ChatColor.RED + "Your insurance premium of " + econ.format(premium) + " could not be collected because the transaction failed! " + parameters.getTransactionError());
            player.sendMessage(ChatColor.RED + "Your policy will be honoured, but we hope you will send the premium fee to the server donation address in due course :-)");

            System.out.println("[" + player.getName() + "] Insurance premium of " + econ.format(premium) + " could not be paid: " + parameters.getTransactionError());

        }

    }

}
