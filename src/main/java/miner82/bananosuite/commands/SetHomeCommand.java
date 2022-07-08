package miner82.bananosuite.commands;

import miner82.bananosuite.DB;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;

    public SetHomeCommand(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(this.configEngine.getRestrictHomeToOverworld()
             && !player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {

            SendMessage(player, "You can only set your home location in the Overworld.", ChatColor.RED);

            return false;
        }

        Location newHomeLocation = player.getLocation();

        if(DB.setPlayerHomeLocation(player, player.getLocation())) {

            SendMessage(player, "Your home location has been set to " + newHomeLocation.getBlockX() + " / " + newHomeLocation.getBlockY() + " / " + newHomeLocation.getBlockZ() + ".", ChatColor.GREEN);

        }
        else {

            SendMessage(player, "There was an issue setting your home location. Please try again later!", ChatColor.GOLD);

        }

        return true;

    }

}
