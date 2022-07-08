package miner82.bananosuite.commands;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableDeathInsuranceCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;

    public EnableDeathInsuranceCommand(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(!CanCommand(sender)) {

            return false;

        }

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(args.length != 1) {

            SendMessage(player, "Incorrect arguments. Expected 1, received " + args.length, ChatColor.RED);

            return false;

        }
        else if(!args[0].equalsIgnoreCase("enable") && !args[0].equalsIgnoreCase("disable")) {

            SendMessage(player, "Invalid argument. Valid values are 'enable' and 'disable'.", ChatColor.RED);

            return false;

        }

        // Apply the setting
        try {

            this.configEngine.setDeathInsuranceEnabled(args[0].equalsIgnoreCase("enable"));
            this.configEngine.save();

            SendMessage(player, "Death insurance is now " + args[0] + "d.", ChatColor.GREEN);

        }
        catch (Exception e) {

            System.out.println("An error occurred while attempting to enable/disable Death Insurance: ");
            e.printStackTrace();

        }

        return true;

    }

}
