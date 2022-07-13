package miner82.bananosuite.classes;

import org.bukkit.entity.Player;

public class MonKey {

    private int uniqueId;
    private Player owner;
    private String address;
    private MonKeyType type;

    public MonKey(int uniqueId, Player player, String address, MonKeyType type) {
        this.uniqueId = uniqueId;
        this.owner = player;
        this.address = address;
        this.type = type;
    }

    public int getUniqueId() {
        return this.uniqueId;
    }

    public Player getOwner() {
        return this.owner;
    }

    public String getAddress() {
        return this.address;
    }

    public MonKeyType getType() {
        return this.type;
    }

}

