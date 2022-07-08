package miner82.bananosuite.commands;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadConfigurationCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;

    public ReloadConfigurationCommand(ConfigEngine configEngine) {
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

        SendMessage(player, "Configuration reload command acknowledged.", ChatColor.GREEN);
        this.configEngine.reload();

        return true;

    }
}