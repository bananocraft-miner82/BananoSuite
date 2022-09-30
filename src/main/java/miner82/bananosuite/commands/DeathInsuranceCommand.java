package miner82.bananosuite.commands;

import miner82.bananosuite.classes.DeathInsuranceOption;
import miner82.bananosuite.classes.DeathInsuranceManager;
import miner82.bananosuite.classes.PlayerRecord;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DeathInsuranceCommand extends BaseCommand implements CommandExecutor {

    private final IDBConnection db;
    private final ConfigEngine configEngine;
    private final Economy econ;

    public DeathInsuranceCommand(ConfigEngine configEngine, Economy econ, IDBConnection db) {
        this.db  = db;
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(!this.configEngine.getIsEnabled()
                || !this.configEngine.getDeathInsuranceEnabled()) {

            SendMessage(player, "That command is not enabled on this server.", ChatColor.GOLD);

            return false;

        }

        PlayerRecord playerRecord = db.getPlayerRecord(player);

        if(playerRecord == null) {

            SendMessage(player, "Your BananoSuite profile could not be loaded! Please contact an admin.", ChatColor.RED);

            return false;

        }

        if(args.length == 0) {

            SendMessage(player, "Incorrect argument(s). Between 1 and 2, received " + args.length, ChatColor.RED);

            return false;

        }

        DeathInsuranceOption current = playerRecord.getDeathInsuranceOption();

        // START A NEW DEATH INSURANCE POLICY
        if(args[0].equalsIgnoreCase("start")) {

            if(args.length < 2
                || args[1].equalsIgnoreCase(DeathInsuranceOption.None.toString())
                || Arrays.stream(DeathInsuranceOption.values()).noneMatch(v -> v.toString().equalsIgnoreCase(args[1]))) {

                SendMessage(player, "A policy level is required in the second argument. Valid values include '"
                        + DeathInsuranceOption.Inventory + "' and '" + DeathInsuranceOption.Full + "'.", ChatColor.RED);

                return false;

            }

            // Get the provided option
            DeathInsuranceOption option = DeathInsuranceOption.valueOf(args[1]);

            // Record the provided option
            if(current != option) {

                playerRecord.setDeathInsuranceOption(option);
                if(db.save(playerRecord)) {

                    if (current == DeathInsuranceOption.None) {

                        SendMessage(player, "Congratulations on your new Death Insurance policy! You are now covered for [" + option.toString() + "].", ChatColor.GREEN);
                        SendMessage(player, "Your first premium will cost " + econ.format(DeathInsuranceManager.CalculateNextPremium(this.db, this.configEngine, playerRecord, option)) + " and the cost will increase based on the number of deaths in a 24 hour period.", ChatColor.GREEN);

                    } else {

                        SendMessage(player, "Your Death Insurance policy has been changed from [" + current.toString() + "] to [" + option.toString() + "].", ChatColor.GREEN);
                        SendMessage(player, "Your first premium will cost " + econ.format(DeathInsuranceManager.CalculateNextPremium(this.db, this.configEngine, playerRecord, option)) + " and the cost will increase based on the number of deaths in a 24 hour period..", ChatColor.GREEN);

                    }

                }
                else {

                    SendMessage(player, "An error occurred while saving your policy details. Please check with an admin!", ChatColor.RED);

                }

                return true;

            }
            else {

                SendMessage(player, "You already have that level of Death Insurance [" + option.toString() + "]!", ChatColor.RED);

            }

        }
        else if(args[0].equalsIgnoreCase("stop")) {

            //if(db.getPlayerDeathInsurance(player) != DeathInsuranceOption.None) {
            if(current != DeathInsuranceOption.None) {

                playerRecord.setDeathInsuranceOption(DeathInsuranceOption.None);
                db.save(playerRecord);

                SendMessage(player, "Your Death Insurance policy has been terminated.", ChatColor.GREEN);

            }
            else {

                SendMessage(player, "You do not have an active Death Insurance policy to terminate.", ChatColor.GOLD);

            }

        }
        else if(args[0].equalsIgnoreCase("quote")) {

            DeathInsuranceOption option = current; // db.getPlayerDeathInsurance(player);

            if(args.length == 2) {

                try {
                    option = DeathInsuranceOption.valueOf(args[1]);
                }
                catch (IllegalArgumentException ex) {
                    option = current; //db.getPlayerDeathInsurance(player);
                }
            }

            SendMessage(player, "Your next premium for " + option.toString() + " death insurance will cost " + econ.format(DeathInsuranceManager.CalculateNextPremium(this.db, this.configEngine, playerRecord, option)) + " and the cost will increase based on the number of deaths in a 24 hour period.", ChatColor.GREEN);

            return true;

        }
        else if(args[0].equalsIgnoreCase("query")) {

            //DeathInsuranceOption current = db.getPlayerDeathInsurance(player);

            SendMessage(player, "Your current Death Insurance policy is: " + current.toString(), ChatColor.GOLD);

            return true;

        }
        else {

            SendMessage(player, "Incorrect argument(s).", ChatColor.RED);

        }

        return true;

    }

}
