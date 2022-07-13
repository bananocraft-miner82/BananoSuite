package miner82.bananosuite.classes;

import org.bukkit.entity.Player;

public class MonkeyMap {

    private int mapId = 0;
    private Player owner;
    public MonKeyType mapType = MonKeyType.MonKey;
    String imageFileName = "";
    String fullFilePath = "";

    public MonkeyMap(int mapId, Player owner, MonKeyType mapType, String fileName, String absoluteImagePath) {

        this.mapId = mapId;
        this.owner = owner;
        this.mapType = mapType;
        this.imageFileName = fileName;

        this.fullFilePath = absoluteImagePath;

    }

    public int getMapId() {
        return this.mapId;
    }

    public Player getOwner() {
        return this.owner;
    }

    public MonKeyType getMapType() {
        return this.mapType;
    }

    public String getImageFileName() {
        return this.imageFileName;
    }

    public String getImageFullFilePath() {
        return this.fullFilePath;
    }

}
