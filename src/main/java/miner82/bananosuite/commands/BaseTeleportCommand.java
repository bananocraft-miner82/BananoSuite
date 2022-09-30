package miner82.bananosuite.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class BaseTeleportCommand extends BaseCommand {

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
