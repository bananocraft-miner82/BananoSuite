package miner82.bananosuite.commands;

import miner82.bananosuite.classes.PlayerRecord;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPOptInOutCommand extends BaseCommand implements CommandExecutor {

    private final IDBConnection db;
    private final ConfigEngine configEngine;

    public PvPOptInOutCommand(IDBConnection db, ConfigEngine configEngine) {
        this.db = db;
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

        if(!this.configEngine.getIsEnabled()
                || !this.configEngine.getPvpToggleEnabled()) {

            SendMessage(player, "That command is not enabled on this server.", ChatColor.GOLD);

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

        PlayerRecord playerRecord = db.getPlayerRecord(player);

        if(args[0].equalsIgnoreCase("QUERY")) {

            if(playerRecord.isPvpOptedIn()) {

                SendMessage(player, "You are currently OPTED IN for PvP.", ChatColor.RED);

            }
            else {

                SendMessage(player, "You are currently OPTED OUT for PvP.", ChatColor.GREEN);

            }

        }
        else {

            playerRecord.setPvpOptedIn(args[0].equalsIgnoreCase("PVPON"));
            db.save(playerRecord);

            if(playerRecord.isPvpOptedIn()) {

                SendMessage(player, "You are now OPTED IN for PvP.", ChatColor.RED);

            }
            else {

                SendMessage(player, "You are now OPTED OUT for PvP.", ChatColor.GREEN);

            }

        }

        return true;

    }

}