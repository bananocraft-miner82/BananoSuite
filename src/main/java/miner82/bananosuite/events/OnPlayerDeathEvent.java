package miner82.bananosuite.events;

import miner82.bananosuite.DB;
import miner82.bananosuite.classes.DeathInsuranceOption;
import miner82.bananosuite.classes.DeathInsurancePremiumCalculator;
import miner82.bananosuite.configuration.ConfigEngine;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnPlayerDeathEvent implements Listener {

    private final ConfigEngine configEngine;
    private final Economy econ;

    public OnPlayerDeathEvent(ConfigEngine configEngine, Economy econ) {
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent args) {

        if(!this.configEngine.getDeathInsuranceEnabled()) {
            return;
        }

        Player player = args.getEntity();

        DeathInsuranceOption insuranceOption = DB.getPlayerDeathInsurance(player);

        if(insuranceOption != DeathInsuranceOption.None) {

            // Calculate the insurance amount
            double basePremium = this.configEngine.getBaseDeathInsurancePremium();
            double insuranceFee = DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, insuranceOption);
            double feePaid = 0;
            String errorMessage = "";
            boolean activateDeathInsurance = false;

            if(basePremium > 0
                  && insuranceFee == 0) {

                player.sendMessage(ChatColor.GOLD + "Your first death insurance premium each week is free. Thanks for being a citizen!");
                activateDeathInsurance = true;

            }

            if(insuranceFee > 0) {

                if (econ.has(player, insuranceFee)) {

                    try {

                        EconomyResponse r = econ.withdrawPlayer(player, insuranceFee);
                        boolean success = r.transactionSuccess();
                        feePaid = r.amount;

                        if (!success) {

                            feePaid = 0;
                            player.sendMessage(ChatColor.RED + "Your insurance premium of " + econ.format(insuranceFee) + " could not be collected because the transaction failed! " + r.errorMessage);
                            player.sendMessage(ChatColor.RED + "Your policy will be honoured, but we hope you will send the fee to the server donation address in due course :-)");

                            System.out.println("[" + player.getName() + "] Insurance premium of " + econ.format(insuranceFee) + " could not be paid: " + r.errorMessage);

                            errorMessage = r.errorMessage;

                        }

                        activateDeathInsurance = true;

                    } catch (Exception e) {
                        System.out.println("[" + player.getName() + "] Insurance premium of " + econ.format(insuranceFee) + " could not be paid: " + e.getMessage());
                        errorMessage = e.getMessage();
                    }

                } else {

                    player.sendMessage(ChatColor.RED + "Your balance is insufficient to cover your Death Insurance Premium!");

                    return;

                }

            }

            if(activateDeathInsurance) {

                DB.recordPlayerInsuredDeath(player, insuranceOption, feePaid, insuranceFee, args.getDeathMessage(), errorMessage);

                args.setKeepInventory(true);
                args.getDrops().clear();

                if (insuranceOption == DeathInsuranceOption.Full) {

                    args.setKeepLevel(true);
                    args.setDroppedExp(0);

                }

                args.setDeathMessage(player.getDisplayName() + " was saved from an excruciating death by Death Insurance!");

                player.sendMessage(ChatColor.AQUA + "Your next insurance premium will cost " + econ.format(DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, insuranceOption)) + " and the cost will increase based on the number of deaths in a 24 hour period.");

                if (econ.getBalance(player) < DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, insuranceOption)) {

                    player.sendMessage(ChatColor.GOLD + "Your balance is low and may not cover your next Death Insurance premium!");

                }

            }
        }

    }

}
