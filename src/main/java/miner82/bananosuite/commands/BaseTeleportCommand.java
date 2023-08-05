package miner82.bananosuite.commands;

import miner82.bananosuite.classes.TeleportRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BaseTeleportCommand extends BaseCommand {

    private final List<TeleportRequest> pendingTeleportRequests = new ArrayList<>();
    private int managerTaskId;
    private final Plugin plugin;

    public BaseTeleportCommand(Plugin plugin) {

        this.plugin = plugin;

    }

    protected void startPendingTeleportManager() {

       this.managerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                //methods
                if(pendingTeleportRequests.removeIf(x -> LocalDateTime.now().isAfter(x.getExpiry()))) {
                    System.out.println("Pending teleports were cleaned up from the collection.");
                }
            }
        }, 600, 600);

    }

    protected TeleportRequest takeTeleportRequest(String requestId) {

        Optional<TeleportRequest> requestOptional = pendingTeleportRequests.stream().filter(x -> x.getRequestId().toString().equalsIgnoreCase(requestId)).findFirst();

        if(requestOptional.isPresent()) {

            TeleportRequest request = requestOptional.get();

            pendingTeleportRequests.remove(request);

            return request;

        }

        return null;

    }

    protected UUID createTeleportRequest(Player forPlayer, Location destination) {

        TeleportRequest request = new TeleportRequest(forPlayer.getUniqueId(), destination);

        pendingTeleportRequests.add(request);

        return request.getRequestId();

    }

    protected UUID createTeleportRequest(Player forPlayer) {

        return createTeleportRequest(forPlayer, null);

    }

    protected void teleportPlayer(Player player, Location destination) {

        if (!destination.getChunk().isLoaded()) {
            destination.getChunk().load();
        }

        player.setGravity(false);

        if(player.isFlying()) {

            player.setFlySpeed(0);
            player.setGliding(false);

        }

        player.setVelocity(new Vector(0,0,0));
        player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);

        player.setGravity(true);

    }

}
