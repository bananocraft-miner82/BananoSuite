package miner82.bananosuite.classes;

import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlayerRecord {

    private UUID uuid;
    private String playerName;
    private LocalDateTime joined;

    private DeathInsuranceOption deathInsuranceOption;
    private LocalDateTime lastDIPolicyUsage;
    private boolean pvpOptedIn;
    private PlayerRank playerRank;
    private boolean shieldsUp;
    private Location homeLocation;

    public PlayerRecord(UUID playerUUID, String playerName, LocalDateTime joined,
                        DeathInsuranceOption insuranceOption, LocalDateTime lastDIPolicyUsage,
                        boolean pvpOptedIn, PlayerRank playerRank,
                        boolean shieldsUp, Location homeLocation) {

        this.uuid = playerUUID;
        this.playerName = playerName;
        this.joined = joined;
        this.deathInsuranceOption = insuranceOption;
        this.lastDIPolicyUsage = lastDIPolicyUsage;
        this.pvpOptedIn = pvpOptedIn;
        this.playerRank = playerRank;
        this.shieldsUp = shieldsUp;
        this.homeLocation = homeLocation;

    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getPlayerName() { return this.playerName; }

    public LocalDateTime getJoined() {
        return this.joined;
    }

    public DeathInsuranceOption getDeathInsuranceOption() {
        return this.deathInsuranceOption;
    }

    public void setDeathInsuranceOption(DeathInsuranceOption deathInsuranceOption) {
        this.deathInsuranceOption = deathInsuranceOption;
    }

    public LocalDateTime getLastDIPolicyUsage() {
        return this.lastDIPolicyUsage;
    }

    public void setLastDIPolicyUsage(LocalDateTime value) {
        this.lastDIPolicyUsage = value;
    }

    public boolean isPvpOptedIn() {
        return this.pvpOptedIn;
    }

    public void setPvpOptedIn(boolean value) {
        this.pvpOptedIn = value;
    }

    public PlayerRank getPlayerRank() {
        return this.playerRank;
    }

    public void setPlayerRank(PlayerRank value) {
        this.playerRank = value;
    }

    public boolean isShieldsUp() {
        return this.shieldsUp;
    }

    public void setShieldsUp(boolean value) {
        this.shieldsUp = value;
    }

    public Location getHomeLocation() {
        return this.homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }

}
