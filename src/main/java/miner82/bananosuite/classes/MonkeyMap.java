package miner82.bananosuite.classes;

import java.util.UUID;

public class MonkeyMap {

    private final UUID id;
    private int mapId = 0;
    private final UUID ownerUUID;
    private final MonKeyType mapType;
    private final String walletAddress;
    private final String additionalText;
    private final String frame;
    String imageFileName = "";
    String fullFilePath = "";
    private MonKeyMapStatus status = MonKeyMapStatus.ReadyForGeneration;

    public MonkeyMap(UUID id, UUID ownerUUID, MonKeyType mapType, String walletAddress, String frame, String additionalText) {

        this.id = id;
        this.ownerUUID = ownerUUID;
        this.mapType = mapType;
        this.walletAddress = walletAddress;
        this.frame = frame;
        this.additionalText = additionalText;

    }

    public UUID getId() {
        return this.id;
    }

    public int getMapId() {
        return this.mapId;
    }

    public void setMapId(int newMapId) {

        this.mapId = newMapId;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public MonKeyType getMapType() {
        return this.mapType;
    }

    public String getWalletAddress() {
        return this.walletAddress;
    }

    public String getAdditionalText() {
        return this.additionalText;
    }

    public boolean hasAdditionalText() {

        return this.additionalText != null
                 && this.additionalText.length() > 0;

    }

    public String getFrame() {
        return this.frame;
    }

    public boolean hasFrame() {

        return this.mapType == MonKeyType.MonKey
                && this.frame != null
                && this.frame.length() > 0;

    }

    public String getImageFileName() {
        return this.imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
    public String getFullFilePath() {
        return this.fullFilePath;
    }

    public void setFullFilePath(String imageFilePath) {
        this.fullFilePath = imageFilePath;
    }

    public MonKeyMapStatus getStatus() {
        return this.status;
    }

    public void setStatus(MonKeyMapStatus newStatus) {
        this.status = newStatus;
    }

}
