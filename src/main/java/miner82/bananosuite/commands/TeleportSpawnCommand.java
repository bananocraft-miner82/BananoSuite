package miner82.bananosuite.commands;

import miner82.bananosuite.classes.DistanceCalculator;
import miner82.bananosuite.classes.TeleportLocationMaker;
import miner82.bananosuite.classes.TeleportPremiumCalculator;
import miner82.bananosuite.classes.TeleportRequest;
import miner82.bananosuite.configuration.ConfigEngine;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
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

public class TeleportSpawnCommand extends BaseTeleportCommand implements CommandExecutor {

    private final String KEY_DESTINATION = "destination";

    private final Economy econ;
    private final ConfigEngine configEngine;

    public TeleportSpawnCommand(Plugin plugin, ConfigEngine configEngine, Economy econ) {

        super(plugin);

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

        if(args.length > 0
             && args[0].equalsIgnoreCase("quote")) {

            quote = true;

        }

        // Process the teleport fee
        try {
            // Calculate the distance
            Optional<World> world = Bukkit.getWorlds().stream().filter(x -> x.getEnvironment().equals(World.Environment.NORMAL)).findFirst();

            if(!world.isPresent()) {
                return false;
            }

            Location destination = world.get().getSpawnLocation();

            if(destination != null) {

                destination = TeleportLocationMaker.checkSafeLocation(destination);

                if(destination == null) {

                    SendMessage(player, "Your teleport destination does not appear to be safe! Please contact an OP.", ChatColor.RED);

                    return false;

                }

                if(this.configEngine.getIsSpawnTeleportFree()) {

                    teleportPlayer(player, destination);

                    return true;

                }

                Location currentLocation = player.getLocation();

                int minimumTeleportDistance = this.configEngine.getMinimumTeleportDistance();
                double distance = DistanceCalculator.calculateDistance(currentLocation, destination);

                if(distance > minimumTeleportDistance) {

                    double teleportCost = TeleportPremiumCalculator.calculateTeleportCost(this.configEngine, currentLocation, destination);

                    if(quote) {

                        SendMessage(player, "A teleport to spawn will cost " + econ.format(teleportCost), ChatColor.GOLD);

                        return true;

                    }

                    if(args.length > 0) {

                        TeleportRequest teleportRequest = takeTeleportRequest(args[0]);

                        if (teleportRequest != null) {

                            try {

                                // We can't teleport a player from another thread, so the payment must be handled here.
                                if (econ.has(player, teleportCost)) {

                                    EconomyResponse response = econ.withdrawPlayer(player, teleportCost);

                                    if (response.transactionSuccess()) {

                                        teleportPlayer(player, destination);

                                        SendMessage(player, "Your teleport request has been completed and " + econ.format(teleportCost) + " has been deducted from your balance.", ChatColor.GREEN);

                                    }

                                } else {

                                    SendMessage(player, "You don't have enough to cover the cost of that teleport! It will cost " + econ.format(teleportCost) + " and you only have " + econ.format(econ.getBalance(player)) + ".", ChatColor.RED);

                                }

                            } catch (Exception e) {

                                e.printStackTrace();

                                SendMessage(player, "Oops, something went wrong! Please contact an Op if you have been charged for this transaction but not teleported to home.", ChatColor.RED);

                            }

                        }
                        else {

                            SendMessage(player, "Your teleport request has expired, already been used or could not be found!", ChatColor.RED);

                        }

                    }
                    else {

                        ComponentBuilder confirmationRequest = new ComponentBuilder(ChatColor.AQUA + "This teleport will cost " + econ.format(teleportCost) + ". ");

                        TextComponent clickHere = new TextComponent( ChatColor.WHITE + "Click here" );
                        clickHere.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/bananosuite:spawn ACCEPTCOST" ) );
                        clickHere.setUnderlined(true);

                        confirmationRequest.append(clickHere);

                        player.spigot().sendMessage(confirmationRequest.create());

                    }

                }
                else {

                    SendMessage( player, "Teleporting over such a small distance is not allowed. Why not walk?", ChatColor.RED);

                }

            }

        }
        catch (NumberFormatException ex) {

            SendMessage( player, "A properly formatted decimal value must be provided!", ChatColor.RED);

            return false;

        }

        return true;

    }

}
