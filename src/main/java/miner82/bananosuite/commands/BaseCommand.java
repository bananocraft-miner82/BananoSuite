package miner82.bananosuite.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BaseCommand {

    protected void SendMessage(Player player, String message, ChatColor messageColour) {
        if(player != null) {
            player.sendMessage(messageColour + message);
        }
        else {
            System.out.println(message);
        }
    }

    protected boolean CanCommand(CommandSender requester) {

        if(requester != null
             && requester instanceof Player) {

            Player player = (Player) requester;

            if (!player.hasPermission("bananominer.setconfig")
                    && !player.isOp()) {

                SendMessage(player,"You do not have permission to use this command!", ChatColor.RED);

                return false;

            }
        }

        return true;

    }
}
