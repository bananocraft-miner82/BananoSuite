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
            SendMessage(player, "Command Enable/Disable: Enable/disable commands.", ChatColor.YELLOW);
            SendMessage(player, "/bananosuite [reloadconfig|deathinsurance|donate|monkeymaps|pvptoggle|rain|teleport] [enable|disable|status]", ChatColor.GRAY);

        }

        SendMessage(player, "Donate: Donate some currency to the server.", ChatColor.YELLOW);
        SendMessage(player, "/donate [amount]", ChatColor.GRAY);

        SendMessage(player, "Rain: Feeling generous? Drop some currency to the other online players.", ChatColor.YELLOW);
        SendMessage(player, "/rain [amount] [optional:everyone|nearby] [optional(nearby only):distance]", ChatColor.GRAY);

        //SendMessage(player, "PvP Toggle: Don't want to PvP? You can opt out! QUERY tells you your current PvP status.", ChatColor.YELLOW);
        //SendMessage(player, "/pvptoggle [PVPOFF|PVPON|QUERY]", ChatColor.GRAY);

        SendMessage(player, "TELEPORT COMMANDS", ChatColor.GOLD);
        SendMessage(player, "Set Home: Set your teleport home location for use with the home teleport command.", ChatColor.YELLOW);
        SendMessage(player, "/sethome", ChatColor.GRAY);

        SendMessage(player, "Teleport: Teleport to home/spawn location [chargeable service]. The greater the distance, the higher the cost. Use QUERY for a quote.", ChatColor.YELLOW);
        SendMessage(player, "/teleport [optional:QUOTE]", ChatColor.GRAY);
        SendMessage(player, "OR /tp [optional:QUOTE]", ChatColor.GRAY);

        SendMessage(player, "Teleport Home: Teleport to your home location [chargeable service]. The greater the distance, the higher the cost. Use QUERY for a quote.", ChatColor.YELLOW);
        SendMessage(player, "/home [optional:QUOTE]", ChatColor.GRAY);
        SendMessage(player, "OR /tph [optional:QUOTE]", ChatColor.GRAY);

        SendMessage(player, "Teleport to Spawn: Teleport to the world spawn location [chargeable service]. The greater the distance, the higher the cost. Use QUERY for a quote.", ChatColor.YELLOW);
        SendMessage(player, "/spawn [optional:QUOTE]", ChatColor.GRAY);
        SendMessage(player, "OR /tps [optional:QUOTE]", ChatColor.GRAY);

        SendMessage(player, "DEATH INSURANCE COMMANDS", ChatColor.GOLD);

        SendMessage(player, "Start/Stop/Query Death Insurance: Take out/cancel/query a Death Insurance Policy. You are only charged when you die!", ChatColor.YELLOW);
        SendMessage(player, "/deathinsurance [start|stop|quote|query] [None|Inventory|Full]", ChatColor.GRAY);
        SendMessage(player, "OR /di [start|stop|quote|query] [None|Inventory|Full]", ChatColor.GRAY);

        SendMessage(player, "MAP COMMANDS", ChatColor.GOLD);

        SendMessage(player, "MonKey/QR Map: Buy a MonKey or QR Code Map. Ask your server OP for the costs!", ChatColor.YELLOW);
        SendMessage(player, "/monkeymap [buy|query|retry|collect] [MonKey|QRCode] [Wallet Address] [Optional: Frame Type (MonKey Only, Server Configurable)]", ChatColor.GRAY);

        return true;

    }
}
