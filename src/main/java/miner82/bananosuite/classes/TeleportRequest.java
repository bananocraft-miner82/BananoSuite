package miner82.bananosuite.classes;

import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.UUID;

public class TeleportRequest {

    private final UUID requestId;
    private final UUID playerId;
    private final Location destination;
    private final LocalDateTime expiry;

    public TeleportRequest(UUID playerId, Location destination) {

        this.requestId = UUID.randomUUID();
        this.playerId = playerId;
        this.destination = destination;
        this.expiry = LocalDateTime.now().plusMinutes(5);

    }

    public TeleportRequest(UUID playerId) {

        this(playerId, null);

    }

    public UUID getRequestId() {
        return this.requestId;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public Location getDestination() {
        return this.destination;
    }

    public LocalDateTime getExpiry() {
        return this.expiry;
    }

}
