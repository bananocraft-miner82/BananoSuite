package miner82.bananosuite.dbconnectors;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import miner82.bananosuite.BananoSuitePlugin;
import miner82.bananosuite.classes.*;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.eq;

public class MongoDBConnector extends BaseDBConnector {

    private final MongoClient mongoClient;
    private final MongoDatabase db;

    private final String mongoURI;

    public MongoDBConnector(BananoSuitePlugin plugin, ConfigEngine configEngine) {

        super(plugin, configEngine);

        this.mongoURI = this.configEngine.getMongoDbConnectionString();
        mongoClient = MongoClients.create(this.mongoURI);
        this.db = mongoClient.getDatabase("BananoSuite");

        loadMaps();

    }

    @Override
    public void shutdown() {

        try {

            this.mongoClient.close();

        }
        catch (Exception ex ){

        }

    }

    protected PlayerRecord loadPlayerRecord(Player player) {

        UUID key = player.getUniqueId();

        if(playerRecords.containsKey(key)) {

            return playerRecords.get(key);

        }

        Document query = new Document("_id", key.toString());
        Document user = db.getCollection("users").find(query).first();
        PlayerRecord playerRecord = null;

        if (user == null) {

            playerRecord = createPlayerRecord(player);

        }
        else {

            try {

                DeathInsuranceOption deathInsuranceOption = DeathInsuranceOption.valueOf(user.getString("deathinsurance"));
                boolean pvpOptedIn = user.getBoolean("pvpoptedin");
                Location homeLocation = player.getBedSpawnLocation();
                boolean shieldsUp = user.getBoolean("shieldsup");
                PlayerRank playerRank = PlayerRank.None;
                LocalDateTime joined = LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getLong("joined")), ZoneOffset.UTC);
                int wildTeleportUseCount = 0;

                if(user.containsKey("wildusecount")) {

                    System.out.println("Pre-load wildTeleportUseCount: " + wildTeleportUseCount);
                    wildTeleportUseCount = user.getInteger("wildusecount");

                    System.out.println(user.getInteger("wildusecount"));
                    System.out.println("Post-load wildTeleportUseCount" + wildTeleportUseCount);
                }

                LocalDateTime lastDIUsage = LocalDateTime.now();

                if(user.containsKey("playerrank")) {

                    playerRank = PlayerRank.valueOf(user.get("playerrank", "None"));

                }

                if(user.containsKey("lastdiuse")) {

                    lastDIUsage = LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getLong("lastdiuse")), ZoneOffset.UTC);

                }

                World world = player.getWorld();

                if(user.containsKey("homeWorldId")) {

                    world = Bukkit.getWorld(UUID.fromString(user.getString("homeWorldId")));

                }
                else if(user.containsKey("homeWorld")
                         && Bukkit.getWorld(user.getString("homeWorld")) != null) {

                    // backwards compatibility
                    world = Bukkit.getWorld(user.getString("homeWorld"));

                }

                if (world != null) {

                    // Getting a cast exception with negative values...
                    homeLocation = new Location(world,
                            (double)user.getInteger("homeX"),
                            (double)user.getInteger("homeY"),
                            (double)user.getInteger("homeZ"));

                }
                else {

                    homeLocation = player.getBedSpawnLocation();

                }

                playerRecord = new PlayerRecord(key,
                                                player.getName(),
                                                joined,
                                                deathInsuranceOption,
                                                lastDIUsage,
                                                pvpOptedIn,
                                                playerRank,
                                                shieldsUp,
                                                homeLocation,
                                                wildTeleportUseCount);

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        if(playerRecord != null
                && !this.playerRecords.containsKey(key)) {

            this.playerRecords.put(key, playerRecord);

        }

        return playerRecord;

    }

    protected PlayerRecord createPlayerRecord(Player player) {

        UUID playerUUID = player.getUniqueId();

        if(playerRecords.containsKey(playerUUID)) {

            return playerRecords.get(playerUUID);

        }

        Location spawnLocation = player.getBedSpawnLocation();

        if(spawnLocation == null) {
            spawnLocation = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        }

        Document document1 = new Document("_id", playerUUID.toString())
                .append("name", player.getName())
                .append("joined", System.currentTimeMillis())
                .append("shieldsup", false)
                .append("pvpoptedin", false)
                .append("lastdonation", 0)
                .append("playerrank", PlayerRank.None.name())
                .append("deathinsurance", DeathInsuranceOption.None.toString())
                .append("lastdiuse", LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .append("homeWorldId", spawnLocation.getWorld().getUID().toString())
                .append("homeWorldName", spawnLocation.getWorld().getName())
                .append("homeX", spawnLocation.getBlockX())
                .append("homeY", spawnLocation.getBlockY())
                .append("homeZ", spawnLocation.getBlockZ())
                .append("wildusecount", 0);

        db.getCollection("users").insertOne(document1);

        PlayerRecord playerRecord = new PlayerRecord(playerUUID,
                                                    player.getName(),
                                                    LocalDateTime.now(),
                                                    DeathInsuranceOption.None,
                                                    LocalDateTime.now(),
                                                    player.isOp(),
                                                    PlayerRank.None,
                                                    false,
                                                    spawnLocation,
                                                    0);

        if(!this.playerRecords.containsKey(playerUUID)) {
            this.playerRecords.put(playerUUID, playerRecord);
        }

        return playerRecord;

    }

    @Override
    public boolean save(PlayerRecord playerRecord) {

        try {

            UUID playerUUID = playerRecord.getUUID();

            BasicDBObject searchQuery = new BasicDBObject("_id", playerUUID.toString());

            BasicDBObject updateFields = new BasicDBObject();

            updateFields.append("shieldsup", playerRecord.isShieldsUp());
            updateFields.append("pvpoptedin", playerRecord.isPvpOptedIn());
            updateFields.append("playerrank", playerRecord.getPlayerRank().name());
            updateFields.append("deathinsurance", playerRecord.getDeathInsuranceOption().name());
            updateFields.append("lastdiuse", playerRecord.getLastDIPolicyUsage().toInstant(ZoneOffset.UTC).toEpochMilli());
            updateFields.append("homeWorldId", playerRecord.getHomeLocation().getWorld().getUID().toString());
            updateFields.append("homeWorldName", playerRecord.getHomeLocation().getWorld().getName());
            updateFields.append("homeX", playerRecord.getHomeLocation().getBlockX());
            updateFields.append("homeY", playerRecord.getHomeLocation().getBlockY());
            updateFields.append("homeZ", playerRecord.getHomeLocation().getBlockZ());
            updateFields.append("wildusecount", playerRecord.getWildTeleportUseCount());

            BasicDBObject setQuery = new BasicDBObject();
            setQuery.append("$set", updateFields);

            UpdateResult updateResult = db.getCollection("users").updateMany(searchQuery, setQuery);

            return updateResult.getModifiedCount() > 0;

        }
        catch (Exception ex) {

        }

        return false;

    }

    @Override
    public void recordPlayerDonation(Player player, double donation) {

        String playerUUID = player.getUniqueId().toString();

        try {

            db.getCollection("users")
                    .updateOne(eq("_id", playerUUID),
                            new Document("$set", new Document("lastdonation", donation)));

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void recordPlayerInsuredDeath(Player player, DeathInsuranceOption deathInsuranceOption, double feePaid, double feeDue, String originalDeathMessage, String errorMessage) {

        String playerUUID = player.getUniqueId().toString();

        try {
            Document document = new Document("_id", UUID.randomUUID().toString())
                    .append("playerid", playerUUID)
                    .append("name", player.getName())
                    .append("occurred", System.currentTimeMillis())
                    .append("policy", deathInsuranceOption.toString())
                    .append("feedue", feeDue)
                    .append("feepaid", feePaid)
                    .append("savedfrom", originalDeathMessage)
                    .append("errormessage", errorMessage);

            db.getCollection("deathpolicyuses").insertOne(document);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getPlayerDIUseCountInLast24Hours(UUID playerUUID) {

        int totalUses = 0;

        FindIterable<Document> documents = db.getCollection("deathpolicyuses").find(and(eq("playerid", playerUUID.toString()), gte("occurred", System.currentTimeMillis() - 86400000)));

        for (Document document: documents) {
            totalUses += 1;
        }

        return totalUses;

    }

    @Override
    public int getPlayerDIUseCountSinceDate(UUID playerUUID, LocalDateTime since) {

        int totalUses = 0;

        FindIterable<Document> documents = db.getCollection("deathpolicyuses").find(and(eq("playerid", playerUUID.toString()), gte("occurred", since.atZone(ZoneId.systemDefault()).toInstant())));

        for (Document document: documents) {
            totalUses += 1;
        }

        return totalUses;

    }

    @Override
    public void loadMaps() {

        for(Document mapDocument : db.getCollection("maps").find()) {

            MonkeyMap map = new MonkeyMap(UUID.fromString(mapDocument.getString("id")),
                                          UUID.fromString(mapDocument.getString("owneruuid")),
                                          MonKeyType.valueOf(mapDocument.getString("mapType")),
                                          mapDocument.getString("walletAddress"),
                                          mapDocument.getString("frame"),
                                          mapDocument.getString("additionalText"));

            map.setMapId(mapDocument.getInteger("mapId"));
            map.setStatus(MonKeyMapStatus.valueOf(mapDocument.getString("status")));
            map.setFullFilePath(mapDocument.getString("fullFilePath"));
            map.setImageFileName(mapDocument.getString("imageFileName"));

            this.maps.put(map.getId(), map);

        }

    }

    @Override
    public boolean createMapRecord(MonkeyMap map) {

        if(map != null) {

            super.createMapRecord(map);

            Document document = new Document("id", map.getId().toString())
                    .append("mapId", map.getMapId())
                    .append("owneruuid", map.getOwnerUUID().toString())
                    .append("created", System.currentTimeMillis())
                    .append("mapType", map.getMapType().name())
                    .append("walletAddress", map.getWalletAddress())
                    .append("additionalText", map.getAdditionalText())
                    .append("frame", map.getFrame())
                    .append("imageFileName", map.getImageFileName())
                    .append("fullFilePath", map.getFullFilePath())
                    .append("status", map.getStatus().toString());


            db.getCollection("maps").insertOne(document);

            return true;

        }

        return false;

    }

    @Override
    public boolean save(MonkeyMap map) {

        if(map != null) {

            BasicDBObject searchQuery = new BasicDBObject("id", map.getId().toString());

            BasicDBObject updateFields = new BasicDBObject();
            updateFields.append("mapId", map.getMapId());
            updateFields.append("owneruuid", map.getOwnerUUID().toString());
            updateFields.append("created", System.currentTimeMillis());
            updateFields.append("mapType", map.getMapType().name());
            updateFields.append("walletAddress", map.getWalletAddress());
            updateFields.append("additionalText", map.getAdditionalText());
            updateFields.append("frame", map.getFrame());
            updateFields.append("imageFileName", map.getImageFileName());
            updateFields.append("fullFilePath", map.getFullFilePath());
            updateFields.append("status", map.getStatus().toString());

            BasicDBObject setQuery = new BasicDBObject();
            setQuery.append("$set", updateFields);

            UpdateResult updateResult = this.db.getCollection("maps").updateMany(searchQuery, setQuery);

            return updateResult.getModifiedCount() > 0;

        }

        return false;

    }

}
