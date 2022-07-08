package miner82.bananosuite.classes;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerSuiteOption {

    private Player player;

    private DeathInsuranceOption deathInsuranceOption;
    private boolean pvpOptedIn;
    private boolean isCitizen;
    private boolean isTheFuzz;
    private boolean shieldsUp;
    private Location homeLocation;

    public PlayerSuiteOption(Player player, DeathInsuranceOption insuranceOption,
                               boolean pvpOptedIn, boolean isCitizen, boolean isTheFuzz,
                               boolean shieldsUp, Location homeLocation) {

        this.player = player;
        this.deathInsuranceOption = insuranceOption;
        this.pvpOptedIn = pvpOptedIn;
        this.isCitizen = isCitizen;
        this.isTheFuzz = isTheFuzz;
        this.shieldsUp = shieldsUp;
        this.homeLocation = homeLocation;

    }

    public Player getPlayer() {
        return player;
    }

    public DeathInsuranceOption getDeathInsuranceOption() {
        return this.deathInsuranceOption;
    }

    public void setDeathInsuranceOption(DeathInsuranceOption deathInsuranceOption) {
        this.deathInsuranceOption = deathInsuranceOption;
    }

    public boolean isPvpOptedIn() {
        return this.pvpOptedIn;
    }

    public void setPvpOptedIn(boolean value) {
        this.pvpOptedIn = value;
    }

    public boolean isCitizen() {
        return this.isCitizen;
    }

    public void setCitizen(boolean value) {
        this.isCitizen = value;
    }

    public boolean isTheFuzz() {
        return this.isTheFuzz;
    }

    public void setTheFuzz(boolean value) {
        this.isTheFuzz = value;
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
