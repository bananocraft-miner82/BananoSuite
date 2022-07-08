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

public class StartDeathInsuranceCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;
    private Economy econ;

    public StartDeathInsuranceCommand(ConfigEngine configEngine, Economy econ) {
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

            SendMessage(player, "Death Insurance is not enabled on this server.", ChatColor.GOLD);

            return false;

        }

        if(args.length != 1) {

            SendMessage(player, "Incorrect argument. Expected 1, received " + args.length, ChatColor.RED);

            return false;

        }

        if(!Arrays.stream(DeathInsuranceOption.values()).anyMatch(v -> v.toString().equalsIgnoreCase(args[0]))) {

            SendMessage(player, "Invalid argument. Valid values include '" + DeathInsuranceOption.None.toString() + "', '"
                                    + DeathInsuranceOption.Inventory + "' and '" + DeathInsuranceOption.Full + "'.", ChatColor.RED);

            return false;

        }

        // Get the provided option
        DeathInsuranceOption current = DB.getPlayerDeathInsurance(player);
        DeathInsuranceOption option = DeathInsuranceOption.valueOf(args[0]);

        // Record the provided option
        if(current != option) {

            DB.setPlayerDeathInsuranceOption(player, option);

            if(current == DeathInsuranceOption.None) {

                SendMessage(player, "Congratulations on your new Death Insurance policy! You are now covered for [" + option.toString() + "].", ChatColor.GREEN);
                SendMessage(player, "Your first premium will cost " + econ.format(DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, option)) + " and the cost will increase based on the number of deaths in a 24 hour period.", ChatColor.GREEN);

            }
            else {

                if(option == DeathInsuranceOption.None) {

                    SendMessage(player, "Your Death Insurance policy has been terminated.", ChatColor.GREEN);

                }
                else {

                    SendMessage(player, "Your Death Insurance policy has been changed from [" + current.toString() + "] to [" + option.toString() + "].", ChatColor.GREEN);
                    SendMessage(player, "Your first premium will cost " + econ.format(DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, option)) + " and the cost will increase based on the number of deaths in a 24 hour period..", ChatColor.GREEN);

                }

            }

        }
        else {

            SendMessage(player, "You already have that level of Death Insurance [" + option.toString() + "]!", ChatColor.RED);

        }

        return true;

    }

}
