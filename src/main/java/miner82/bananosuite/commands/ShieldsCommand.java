package miner82.bananosuite.commands;

import miner82.bananosuite.classes.PlayerRecord;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShieldsCommand extends BaseCommand implements CommandExecutor {

    private final IDBConnection db;
    private final ConfigEngine configEngine;

    public ShieldsCommand(IDBConnection db, ConfigEngine configEngine) {
        this.db = db;
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

        if(args.length > 0) {

            PlayerRecord playerRecord = db.getPlayerRecord(player);

            if(playerRecord == null) {

                SendMessage(player, "Your BananoSuite profile could not be loaded! Please contact an admin.", ChatColor.RED);

                return false;

            }

            if(args[0].equalsIgnoreCase("status")) {

                if (playerRecord.isShieldsUp()) {

                    SendMessage(player, "Your shields are UP.", ChatColor.GREEN);

                } else {

                    SendMessage(player, "Your shields are DOWN.", ChatColor.RED);

                }

            }
            else if(args[0].equalsIgnoreCase("raise")) {

                playerRecord.setShieldsUp(true);

                if(this.db.save(playerRecord)) {

                    SendMessage(player, "Your shields are UP.", ChatColor.GREEN);

                }
                else {

                    SendMessage(player, "There was an issue saving your BananoSuite profile!", ChatColor.RED);

                }

            }
            else if(args[0].equalsIgnoreCase("lower")) {

                playerRecord.setShieldsUp(false);

                if(this.db.save(playerRecord)) {

                    SendMessage(player, "Your shields are DOWN.", ChatColor.RED);

                }
                else {

                    SendMessage(player, "There was an issue saving your BananoSuite profile!", ChatColor.RED);

                }

            }
            else {

                SendMessage(player, "An argument of 'raise', 'lower' or 'query' is expected with this command.", ChatColor.RED);

            }

        }
        else {

            SendMessage(player, "An argument of 'raise', 'lower' or 'query' is expected with this command.", ChatColor.RED);

        }

        return true;

    }

}
