package miner82.bananosuite.commands;

import miner82.bananosuite.classes.LocationRandomiser;
import miner82.bananosuite.classes.PlayerRecord;
import miner82.bananosuite.classes.TeleportRequest;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.UUID;

public class WildTeleportCommand extends BaseTeleportCommand implements CommandExecutor {

    private final IDBConnection db;
    private final Economy econ;
    private final ConfigEngine configEngine;
    private final LocationRandomiser locationRandomiser;

    public WildTeleportCommand(Plugin plugin, IDBConnection db, ConfigEngine configEngine, Economy econ) {

        super(plugin);

        this.db = db;
        this.configEngine = configEngine;
        this.econ = econ;
        this.locationRandomiser = new LocationRandomiser(this.configEngine);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            PlayerRecord playerRecord = this.db.getPlayerRecord(player);

            if(playerRecord != null) {

                if(playerRecord.getWildTeleportUseCount() < this.configEngine.getMaximumWildTeleportUses()) {

                    double cost = this.configEngine.getWildTeleportCost(playerRecord.getWildTeleportUseCount());
                    boolean canTeleport = (cost == 0);

                    if(args.length > 0
                            && args[0].equalsIgnoreCase("help")) {

                        SendMessage(player, "A wild teleport will teleport you to a random location up to "
                                                + this.configEngine.getMaximumTeleportRadius() + " blocks from the world spawn location. "
                                                + "You can use the /wild command up to " + this.configEngine.getMaximumWildTeleportUses() + " times "
                                                + " and have already used the command " + playerRecord.getWildTeleportUseCount() + " times.", ChatColor.AQUA);

                        String message = "The cost for each use will be: ";

                        for(int index = 0; index < this.configEngine.getMaximumWildTeleportUses(); index++) {

                            message += "Attempt " + (index + 1) + ": ";

                            double attemptCost = this.configEngine.getWildTeleportCost(index);

                            if(attemptCost > 0) {
                                message += econ.format(attemptCost) + "; ";
                            }
                            else {
                                message += "FREE; ";
                            }

                        }

                        SendMessage(player, message, ChatColor.AQUA);

                        return true;

                    }

                    if(cost > 0) {

                        if(args.length > 0) {

                            TeleportRequest teleportRequest = takeTeleportRequest(args[0]);

                            if (teleportRequest != null) {

                                if(econ.has(player, cost)) {

                                    EconomyResponse response = econ.withdrawPlayer(player, cost);

                                    if (response.transactionSuccess()) {

                                        canTeleport = true;

                                    }

                                }

                            }
                            else {

                                SendMessage(player, "Your teleport request has expired, already been used or could not be found!", ChatColor.RED);

                            }

                        }
                        else if(args.length == 0) {

                            UUID requestId = createTeleportRequest(player);

                            ComponentBuilder confirmationRequest = new ComponentBuilder(ChatColor.AQUA + "This teleport will cost " + econ.format(cost) + ". ");

                            TextComponent clickHere = new TextComponent( ChatColor.WHITE + "Click here" );
                            clickHere.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/bananosuite:wild " + requestId.toString() ) );
                            clickHere.setUnderlined(true);

                            confirmationRequest.append(clickHere);

                            confirmationRequest.append(ChatColor.AQUA + " to accept the fee and continue with the teleport. This will expire in 5 minutes or when used.");

                            player.spigot().sendMessage(confirmationRequest.create());

                        }

                    }

                    if(canTeleport) {

                        TeleportPlayerToRandomisedLocation(player);
                        SendMessage(player, "Your wild teleport request has been completed!", ChatColor.GOLD);

                        incrementPlayerWildUseCount(player);

                    }

                }
                else {

                    SendMessage(player, "You have already used up all of your wild teleport chances!", ChatColor.RED);

                }

            }


        }

        return true;

    }

    private void incrementPlayerWildUseCount(Player player) {

        PlayerRecord playerRecord = this.db.getPlayerRecord(player);

        playerRecord.incrementWildTeleportUseCount();

        this.db.save(playerRecord);

    }

    private void TeleportPlayerToRandomisedLocation(Player player) {

        Location newSpawnLocation = locationRandomiser.calculateRandomLocation(player);

        // Enforce the player's spawn location
        player.setBedSpawnLocation(newSpawnLocation, true);

        // Now teleport them to the new spawn location
        if (!newSpawnLocation.getChunk().isLoaded()) {
            newSpawnLocation.getChunk().load();
        }

        player.setGravity(false);

        if(player.isFlying()) {

            player.setFlySpeed(0);
            player.setGliding(false);

        }

        player.setVelocity(new Vector(0,0,0));
        player.teleport(newSpawnLocation, PlayerTeleportEvent.TeleportCause.COMMAND);

        player.setGravity(true);

        if (locationRandomiser.setPlayerInBoatIfWaterLocation(player, newSpawnLocation)
                && this.configEngine.getIsDebugMode()) {

            System.out.println("Player spawned in water: assigned a boat.");

        }

        locationRandomiser.givePlayerLocationAir(player, newSpawnLocation);

    }

}
