package miner82.bananosuite.commands;

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

public class QuoteDeathInsuranceCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;
    private Economy econ;

    public QuoteDeathInsuranceCommand(ConfigEngine configEngine, Economy econ) {
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(!Arrays.stream(DeathInsuranceOption.values()).anyMatch(v -> v.toString().equalsIgnoreCase(args[0]))) {

            SendMessage(player, "Invalid argument. Valid values include '" + DeathInsuranceOption.None.toString() + "', '"
                    + DeathInsuranceOption.Inventory + "' and '" + DeathInsuranceOption.Full + "'.", ChatColor.RED);

            return false;

        }

        DeathInsuranceOption option = DeathInsuranceOption.valueOf(args[0]);

        SendMessage(player, "Your next premium for " + option.toString() + " death insurance will cost " + econ.format(DeathInsurancePremiumCalculator.CalculateNextPremium(this.configEngine, player, option)) + " and the cost will increase based on the number of deaths in a 24 hour period.", ChatColor.GREEN);

        return true;

    }

}
