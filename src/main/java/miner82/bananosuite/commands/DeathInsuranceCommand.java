package miner82.bananosuite.commands;

import miner82.bananosuite.DB;
import miner82.bananosuite.classes.DeathInsuranceOption;
import miner82.bananosuite.classes.DeathInsurancePremiumCalculator;
import miner82.bananosuite.configuration.ConfigEngine;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DeathInsuranceCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;
    private Economy econ;

    public DeathInsuranceCommand(ConfigEngine configEngine, Economy econ) {
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

        if(args.length == 0) {

            SendMessage(player, "Incorrect argument(s). Between 1 and 2, received " + args.length, ChatColor.RED);

            return false;

        }

        // START A NEW DEATH INSURANCE POLICY
        if(args[0].equalsIgnoreCase("start")) {

            if(args.length < 2
                || args[1].equalsIgnoreCase(DeathInsuranceOption.None.toString())
                || !Arrays.stream(DeathInsuranceOption.values()).anyMatch(v -> v.toString().equalsIgnoreCase(args[1]))) {

                SendMessage(player, "A policy level is required in the second argument. Valid values include '"
                        + DeathInsuranceOption.Inventory + "' and '" + DeathInsuranceOption.Full + "'.", ChatColor.RED);

                return false;

            }

            // Get the provided option
            DeathInsuranceOption current = DB.getPlayerDeathInsurance(player);
            DeathInsuranceOption option = DeathInsuranceOption.valueOf(args[1]);

            // Record the provided option
            if(current != option) {

                DB.setPlayerDeathInsuranceOption(player, option);

                if(current == DeathInsuranceOption.None) {

                    SendMessage(player, "Congratulations on your new Death Insurance policy! You are now covered for [" + option.toString() + "].", ChatColor.GREEN);
                    SendMessage(player, "Your first premium will cost " + econ.format(DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, option)) + " and the cost will increase based on the number of deaths in a 24 hour period.", ChatColor.GREEN);

                }
                else {

                    SendMessage(player, "Your Death Insurance policy has been changed from [" + current.toString() + "] to [" + option.toString() + "].", ChatColor.GREEN);
                    SendMessage(player, "Your first premium will cost " + econ.format(DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, option)) + " and the cost will increase based on the number of deaths in a 24 hour period..", ChatColor.GREEN);

                }

                return true;

            }
            else {

                SendMessage(player, "You already have that level of Death Insurance [" + option.toString() + "]!", ChatColor.RED);

            }

        }
        else if(args[0].equalsIgnoreCase("stop")) {

            if(DB.getPlayerDeathInsurance(player) != DeathInsuranceOption.None) {

                DB.setPlayerDeathInsuranceOption(player, DeathInsuranceOption.None);

                SendMessage(player, "Your Death Insurance policy has been terminated.", ChatColor.GREEN);

            }
            else {

                SendMessage(player, "You do not have an active Death Insurance policy to terminate.", ChatColor.GOLD);

            }

        }
        else if(args[0].equalsIgnoreCase("quote")) {

            DeathInsuranceOption option = DeathInsuranceOption.valueOf(args[1]);

            SendMessage(player, "Your next premium for " + option.toString() + " death insurance will cost " + econ.format(DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, option)) + " and the cost will increase based on the number of deaths in a 24 hour period.", ChatColor.GREEN);

            return true;

        }
        else if(args[0].equalsIgnoreCase("query")) {

            DeathInsuranceOption current = DB.getPlayerDeathInsurance(player);

            SendMessage(player, "Your current Death Insurance policy is: " + current.toString(), ChatColor.GOLD);

            return true;

        }
        else {

            SendMessage(player, "Incorrect argument(s).", ChatColor.RED);

        }

        return true;

    }

}
