package miner82.bananosuite.commands;

import miner82.bananosuite.classes.PlayerRecord;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand extends BaseCommand implements CommandExecutor {

    private final IDBConnection db;
    private final ConfigEngine configEngine;

    public SetHomeCommand(IDBConnection db, ConfigEngine configEngine) {
        this.db = db;
        this.configEngine = configEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(!this.configEngine.getIsEnabled()
                || !this.configEngine.getTeleportEnabled()) {

            SendMessage(player, "That command is not enabled on this server.", ChatColor.GOLD);

            return false;

        }

        if(this.configEngine.getRestrictHomeToOverworld()
             && !player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {

            SendMessage(player, "You can only set your home location in the Overworld.", ChatColor.RED);

            return false;
        }

        Location newHomeLocation = player.getLocation();

        PlayerRecord playerRecord = db.getPlayerRecord(player);

        if(playerRecord == null) {

            SendMessage(player, "Your BananoSuite profile could not be loaded! Please contact an admin.", ChatColor.RED);

            return false;

        }

        playerRecord.setHomeLocation(newHomeLocation);

        if(db.save(playerRecord)) {

            SendMessage(player, "Your home location has been set to " + newHomeLocation.getBlockX() + " / " + newHomeLocation.getBlockY() + " / " + newHomeLocation.getBlockZ() + ".", ChatColor.GREEN);

        }
        else {

            SendMessage(player, "There was an issue setting your home location. Please try again later!", ChatColor.GOLD);

        }

        return true;

    }

}
