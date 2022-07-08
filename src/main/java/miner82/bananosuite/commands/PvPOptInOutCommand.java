package miner82.bananosuite.commands;

import miner82.bananosuite.DB;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPOptInOutCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;

    public PvPOptInOutCommand(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }
        else {

            return false;

        }

        if(args.length != 1) {

            SendMessage(player, "Incorrect arguments. Expected 1, received " + args.length, ChatColor.RED);

            return false;

        }
        else if(!args[0].equalsIgnoreCase("PVPON")
                  && !args[0].equalsIgnoreCase("PVPOFF")
                  && !args[0].equalsIgnoreCase("QUERY")) {

            SendMessage(player, "Incorrect arguments. Expected 'PVPON', 'PVPOFF' or 'QUERY', received " + args[0], ChatColor.RED);

            return false;

        }

        if(args[0].equalsIgnoreCase("QUERY")) {

            if(DB.getPlayerPvPOptIn(player)) {

                SendMessage(player, "You are currently OPTED IN for PvP.", ChatColor.RED);

            }
            else {

                SendMessage(player, "You are currently OPTED OUT for PvP.", ChatColor.GREEN);

            }

        }
        else {

            DB.setPlayerPvPOptIn(player, args[0].equalsIgnoreCase("PVPON"));

            if(DB.getPlayerPvPOptIn(player)) {

                SendMessage(player, "You are now OPTED IN for PvP.", ChatColor.RED);

            }
            else {

                SendMessage(player, "You are now OPTED OUT for PvP.", ChatColor.GREEN);

            }

        }

        return true;

    }

}