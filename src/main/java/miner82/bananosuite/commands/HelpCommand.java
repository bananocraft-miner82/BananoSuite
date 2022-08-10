package miner82.bananosuite.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand extends BaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        SendMessage( player, ChatColor.BOLD + "BananoSuite Help Commands", ChatColor.GOLD);

        if(player != null
             && player.isOp()) {

            // List the OP-only commands

        }

        SendMessage(player, "Donate: Donate some currency to the server.", ChatColor.YELLOW);
        SendMessage(player, "/donate [amount]", ChatColor.GRAY);

        SendMessage(player, "Rain: Feeling generous? Drop some currency to the other online players.", ChatColor.YELLOW);
        SendMessage(player, "/rain [amount]", ChatColor.GRAY);

        if(player != null && player.isOp()) {

            SendMessage(player, "/rain [amount] [optional:testmode]", ChatColor.GRAY);
            SendMessage(player, "OR /makeitrain [amount] [optional:testmode]", ChatColor.GRAY);

        }
        else {

            SendMessage(player, "/rain [amount]", ChatColor.GRAY);
            SendMessage(player, "OR /makeitrain [amount]", ChatColor.GRAY);

        }

        SendMessage(player, "PvP Toggle: Don't want to PvP? You can opt out! QUERY tells you your current PvP status.", ChatColor.YELLOW);
        SendMessage(player, "/pvptoggle [PVPOFF|PVPON|QUERY]", ChatColor.GRAY);

        SendMessage(player, "TELEPORT COMMANDS", ChatColor.GOLD);
        SendMessage(player, "Set Home: Set your teleport home location for use with the home teleport command.", ChatColor.YELLOW);
        SendMessage(player, "/sethome", ChatColor.GRAY);

        SendMessage(player, "Teleport Home: Teleport to your home location [chargeable service]. The greater the distance, the higher the cost. Use QUERY for a quote.", ChatColor.YELLOW);
        SendMessage(player, "/tphome [optional:QUERY]", ChatColor.GRAY);
        SendMessage(player, "OR /tph [optional:QUERY]", ChatColor.GRAY);

        SendMessage(player, "Teleport to Spawn: Teleport to the world spawn location [chargeable service]. The greater the distance, the higher the cost. Use QUERY for a quote.", ChatColor.YELLOW);
        SendMessage(player, "/tpspawn [optional:QUERY]", ChatColor.GRAY);
        SendMessage(player, "OR /tps [optional:QUERY]", ChatColor.GRAY);

        SendMessage(player, "Teleport Quote: Get the cost of the desired teleport. The greater the distance, the higher the cost.", ChatColor.YELLOW);
        SendMessage(player, "/tpquote [HOME|SPAWN]", ChatColor.GRAY);

        SendMessage(player, "DEATH INSURANCE COMMANDS", ChatColor.GOLD);

        SendMessage(player, "Teleport Quote: Get the cost of the desired teleport. The greater the distance, the higher the cost.", ChatColor.YELLOW);
        SendMessage(player, "/tpquote [HOME|SPAWN]", ChatColor.GRAY);

        if(player != null && player.isOp()) {

            SendMessage(player, "Enable/Disable Death Insurance: Admin tool to toggle DI on or off.", ChatColor.YELLOW);
            SendMessage(player, "/enabledeathinsurance [enable|disable]", ChatColor.GRAY);
            SendMessage(player, "OR /toggledeathpolicy [enable|disable]", ChatColor.GRAY);

        }

        SendMessage(player, "Start Death Insurance: Take out a Death Insurance Policy. You are only charged when you die!", ChatColor.YELLOW);
        SendMessage(player, "/startdeathinsurance [None|Inventory|Full]", ChatColor.GRAY);
        SendMessage(player, "OR /startdeathpolicy [None|Inventory|Full]", ChatColor.GRAY);

        SendMessage(player, "Stop Death Insurance: End a Death Insurance Policy.", ChatColor.YELLOW);
        SendMessage(player, "/stopdeathinsurance", ChatColor.GRAY);
        SendMessage(player, "OR /stopdeathpolicy", ChatColor.GRAY);

        SendMessage(player, "Death Insurance Quote: Find out how much the first Death Insurance Policy premium will cost. More deaths in 24 hours = higher premiums!", ChatColor.YELLOW);
        SendMessage(player, "/quotedeathinsurance [Inventory|Full]", ChatColor.GRAY);
        SendMessage(player, "OR /diquote [Inventory|Full]", ChatColor.GRAY);

        SendMessage(player, "MAP COMMANDS", ChatColor.GOLD);

        SendMessage(player, "MonKey/QR Map: Buy a MonKey or QR Code Map. Ask your server OP for the costs!", ChatColor.YELLOW);
        SendMessage(player, "/buymonkeymap [MonKey|QRCode] [Wallet Address] [Optional: Frame Type (MonKey Only, Server Configurable)]", ChatColor.GRAY);

        return true;

    }
}
