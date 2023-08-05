package miner82.bananosuite.commands;

import miner82.bananosuite.classes.DistanceCalculator;
import miner82.bananosuite.classes.Math;
import miner82.bananosuite.classes.PlayerRecord;
import miner82.bananosuite.classes.TeleportLocationMaker;
import miner82.bananosuite.classes.TeleportPremiumCalculator;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class TeleportCommand extends BaseTeleportCommand implements CommandExecutor {

    private final String KEY_DESTINATION = "destination";

    private final IDBConnection db;
    private final Economy econ;
    private final ConfigEngine configEngine;

    public TeleportCommand(Plugin plugin, IDBConnection db, ConfigEngine configEngine, Economy econ) {

        super(plugin);

        this.db = db;
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;
        boolean quote = false;

        if(sender instanceof Player) {

            player = (Player) sender;

        }
        else {

            return false;

        }

        if(!this.configEngine.getIsEnabled()
                || !this.configEngine.getTeleportEnabled()) {

            SendMessage(player, "That command is not enabled on this server.", ChatColor.GOLD);

            return false;

        }

        if(args.length > 1
                && args[1].equalsIgnoreCase("quote")) {

            quote = true;

        }

        // Process the teleport fee
        try {

            PlayerRecord playerRecord = db.getPlayerRecord(player);

            // Calculate the distance
            Optional<World> world = Bukkit.getWorlds().stream().filter(x -> x.getEnvironment().equals(World.Environment.NORMAL)).findFirst();

            if(!world.isPresent()) {
                return false;
            }

            Location destination = null;

            if(args[0].equalsIgnoreCase("SPAWN")) {

                destination = world.get().getSpawnLocation();

            }
            else if(args[0].equalsIgnoreCase("HOME")) {

                destination = playerRecord.getHomeLocation();

            }

            if(destination != null) {

                destination = TeleportLocationMaker.checkSafeLocation(destination);

                if(destination == null) {

                    SendMessage(player, "Your teleport destination does not appear to be safe! Please contact an OP.", ChatColor.RED);

                    return false;

                }

                if(this.configEngine.getIsSpawnTeleportFree()) {

                    teleportPlayer(player, destination);
                    SendMessage(player, "You have not been charged for this teleport. Spawn teleports are currently free!", ChatColor.GOLD);

                    return true;

                }

                Location currentLocation = player.getLocation();

                int minimumTeleportDistance = this.configEngine.getMinimumTeleportDistance();
                double distance = DistanceCalculator.calculateDistance(currentLocation, destination);

                if(distance > minimumTeleportDistance) {

                    double teleportCost;

                    // OPs are putting in the hard work without any remuneration. Let's reward them.
                    if(player.isOp()) {

                        teleportCost = 0;

                    }
                    else {

                        teleportCost = TeleportPremiumCalculator.calculateTeleportCost(this.configEngine, currentLocation, destination);

                        // Give a citizen/special discount for players with a rank with perks.
                        if(playerRecord != null
                             && playerRecord.getPlayerRank().getPerks()) {

                            teleportCost = Math.Round(teleportCost * 0.75, 2);

                        }

                    }

                    if(quote) {

                        SendMessage(player, "A teleport to spawn will cost " + econ.format(teleportCost), ChatColor.GOLD);

                        return true;

                    }

                    try {

                        // We can't teleport a player from another thread, so the payment must be handled here.
                        if (econ.has(player, teleportCost)) {

                            EconomyResponse response = econ.withdrawPlayer(player, teleportCost);

                            if (response.transactionSuccess()) {

                                teleportPlayer(player, destination);

                                SendMessage(player, "Your teleport request has been completed and " + econ.format(teleportCost) + " has been deducted from your balance.", ChatColor.GREEN);

                            }

                        }
                        else {

                            SendMessage(player, "You don't have enough to cover the cost of that teleport! It will cost " + econ.format(teleportCost) + " and you only have " + econ.format(econ.getBalance(player)) + ".", ChatColor.RED);

                        }

                    }
                    catch (Exception e) {

                        e.printStackTrace();

                        SendMessage( player, "Oops, something went wrong! Please contact an Op if you have been charged for this transaction but not teleported to home.", ChatColor.RED);

                    }

                }
                else {

                    SendMessage( player, "Teleporting over such a small distance is not allowed. Why not walk?", ChatColor.RED);

                }

            }
            else {

                SendMessage( player, "The destination '" + args[0] + "' could not be identified!", ChatColor.RED);

            }

        }
        catch (NumberFormatException ex) {

            SendMessage( player, "A properly formatted decimal value must be provided!", ChatColor.RED);

            return false;

        }

        return true;

    }

}