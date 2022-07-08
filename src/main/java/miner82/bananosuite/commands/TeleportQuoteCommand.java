package miner82.bananosuite.commands;

import miner82.bananosuite.DB;
import miner82.bananosuite.classes.DistanceCalculator;
import miner82.bananosuite.classes.TeleportPremiumCalculator;
import miner82.bananosuite.configuration.ConfigEngine;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Optional;

public class TeleportQuoteCommand extends BaseCommand implements CommandExecutor {

    private Economy econ;
    private ConfigEngine configEngine;

    public TeleportQuoteCommand(ConfigEngine configEngine, Economy econ) {
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(player == null) {

            SendMessage(player, "You cannot run this command from console!", ChatColor.RED);

        }

        if(args.length != 1) {

            SendMessage(player, "Incorrect arguments. Expected 1, received " + args.length, ChatColor.RED);

            return false;

        }

        if(!args[0].equalsIgnoreCase("HOME")
             && !args[0].equalsIgnoreCase("SPAWN")) {

            SendMessage(player, "Incorrect arguments. Expected 'HOME' or 'SPAWN', received " + args[0], ChatColor.RED);

            return false;

        }

        Location destination = null;

        if(args[0].equalsIgnoreCase("HOME")) {

            destination = DB.getPlayerHomeLocation(player);

        }
        else if (args[0].equalsIgnoreCase("SPAWN")) {

            if(this.configEngine.getIsSpawnTeleportFree()) {

                SendMessage(player, "Spawn teleport is currently free!", ChatColor.GREEN);

                return true;

            }

            Optional<World> world = Bukkit.getWorlds().stream().filter(x -> x.getEnvironment().equals(World.Environment.NORMAL)).findFirst();

            if(!world.isPresent()) {
                return false;
            }

            destination = world.get().getSpawnLocation();

        }

        if(destination != null) {

            Location currentLocation = player.getLocation();
            double distance = DistanceCalculator.calculateDistance(currentLocation, destination);

            if(distance > 0) {

                double teleportCost = TeleportPremiumCalculator.calculateTeleportCost(this.configEngine, currentLocation, destination);

                SendMessage(player, "A teleport to " + args[0] + " will currently cost " + econ.format(teleportCost) + ".", ChatColor.GOLD);

            }
            else {

                SendMessage(player, "You appear to be at that location already!", ChatColor.RED);

            }

        }
        else {

            SendMessage(player, "That destination could not be found!", ChatColor.RED);

        }

        return true;

    }

}
