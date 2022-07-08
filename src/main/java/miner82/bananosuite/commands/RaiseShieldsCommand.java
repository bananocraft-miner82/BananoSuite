package miner82.bananosuite.commands;

import miner82.bananosuite.DB;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RaiseShieldsCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;

    public RaiseShieldsCommand(ConfigEngine configEngine) {
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

        if(player == null) {
            SendMessage(player, "You cannot run this command from console.", ChatColor.RED);
        }

        boolean shieldsUp = DB.getPlayerShieldsUp(player);

        DB.setPlayerShieldsUp(player, !DB.getPlayerShieldsUp(player));

        if(DB.getPlayerShieldsUp(player)) {

            SendMessage(player, "Your shields are UP.", ChatColor.GREEN);

        }
        else {

            SendMessage(player, "Your shields are DOWN.", ChatColor.RED);

        }

        return true;

    }

}
