package miner82.bananosuite;

import com.mongodb.client.*;
import miner82.bananosuite.classes.*;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.mongodb.client.model.Filters.*;


public class DB {


    static Plugin plugin = Main.getPlugin(Main.class);
    private static MongoClient mongoClient = MongoClients.create(getMongoURI());
    private static MongoDatabase db = mongoClient.getDatabase("BananoSuite");

    public static String getMongoURI() {
        return plugin.getConfig().getString("mongoURI");
    }

    private static HashMap<String, PlayerSuiteOption> playerOptions = new HashMap<String, PlayerSuiteOption>(); // player uuid, insurance level

    public static MonKeyMaps maps = new MonKeyMaps();

    private static URI getURI() throws Exception {
        return new URI(plugin.getConfig().getString("mongoURI"));
    }

    public static Date getLastPlayerDonation(Player player) {

        String playerUUID = player.getUniqueId().toString();

        try {
            Document user = getUserDBEntry(player);

            return new Date(user.getLong("lastdonation"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void recordPlayerDonation(Player player, double donation) {

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

    public static DeathInsuranceOption getPlayerDeathInsurance(Player player) {

        String playerUUID = player.getUniqueId().toString();

        if(!playerOptions.containsKey(playerUUID)) {
            initialisePlayerOnJoin(player);
        }

        return playerOptions.get(playerUUID).getDeathInsuranceOption();

    }

    public static boolean setPlayerDeathInsuranceOption(Player player, DeathInsuranceOption option) {

        String playerUUID = player.getUniqueId().toString();

        try {
            db.getCollection("users")
                    .updateOne(eq("_id", playerUUID),
                            new Document("$set", new Document("deathinsurance", option.toString())));
        }
        catch (Exception ex) {
            return false;
        }

        if(!playerOptions.containsKey(playerUUID)) {
            initialisePlayerOnJoin(player);
        }

        playerOptions.get(playerUUID).setDeathInsuranceOption(option);

        return true;

    }

    public static boolean setPlayerHomeLocation(Player player, Location homeLocation) {

        String playerUUID = player.getUniqueId().toString();

        try {

            db.getCollection("users")
              .updateOne(eq("_id", playerUUID),
                    new Document("$set", new Document("homeX", homeLocation.getBlockX())
                                                    .append("homeY", homeLocation.getBlockY())
                                                    .append("homeZ", homeLocation.getBlockZ())
                                                    .append("homeWorld", homeLocation.getWorld().getName())));

            if(!playerOptions.containsKey(playerUUID)) {
                initialisePlayerOnJoin(player);
            }

            playerOptions.get(playerUUID).setHomeLocation(homeLocation);

            return true;

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public static Location getPlayerHomeLocation(Player player) {

        String playerUUID = player.getUniqueId().toString();

        if(!playerOptions.containsKey(playerUUID)) {
            initialisePlayerOnJoin(player);
        }

        return playerOptions.get(playerUUID).getHomeLocation();

    }

    public static boolean getPlayerPvPOptIn(Player player) {

        String playerUUID = player.getUniqueId().toString();

        if(!playerOptions.containsKey(playerUUID)) {
            initialisePlayerOnJoin(player);
        }

        return playerOptions.get(playerUUID).isPvpOptedIn();

    }

    public static boolean setPlayerPvPOptIn(Player player, boolean optedIn) {

        String playerUUID = player.getUniqueId().toString();

        try {
            db.getCollection("users")
                    .updateOne(eq("_id", playerUUID),
                            new Document("$set", new Document("pvpoptedin", optedIn)));
        }
        catch (Exception ex) {
            return false;
        }

        if(!playerOptions.containsKey(playerUUID)) {
            initialisePlayerOnJoin(player);
        }

        playerOptions.get(playerUUID).setPvpOptedIn(optedIn);

        return true;

    }

    public static boolean getPlayerShieldsUp(Player player) {

        if(!player.isOp()) {
            return false;
        }

        String playerUUID = player.getUniqueId().toString();

        if(!playerOptions.containsKey(playerUUID)) {
            initialisePlayerOnJoin(player);
        }

        return playerOptions.get(playerUUID).isShieldsUp();

    }

    public static boolean setPlayerShieldsUp(Player player, boolean shieldsUp) {

        if(!player.isOp()) {
            return false;
        }

        String playerUUID = player.getUniqueId().toString();

        try {
            db.getCollection("users")
                    .updateOne(eq("_id", playerUUID),
                            new Document("$set", new Document("shieldsup", shieldsUp)));
        }
        catch (Exception ex) {
            return false;
        }

        if(!playerOptions.containsKey(playerUUID)) {
            initialisePlayerOnJoin(player);
        }

        playerOptions.get(playerUUID).setShieldsUp(shieldsUp);

        return true;

    }

    public static void recordPlayerInsuredDeath(Player player, DeathInsuranceOption deathInsuranceOption, double feePaid, double feeDue, String originalDeathMessage, String errorMessage) {

        String playerUUID = player.getUniqueId().toString();

        try {
            Document document = new Document("_id", new ObjectId())
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

    public static int getPlayerPremiumUseCountInLast24Hours(Player player) {

        String playerUUID = player.getUniqueId().toString();

        int totalUses = 0;

        FindIterable<Document> documents = db.getCollection("deathpolicyuses").find(and(eq("playerid", playerUUID), gte("occurred", System.currentTimeMillis() - 86400000)));

        for (Document document: documents) {
            totalUses += 1;
        }

        return totalUses;

    }

    public static int getPlayerPremiumUseCountSinceDate(Player player, LocalDateTime since) {

        String playerUUID = player.getUniqueId().toString();

        int totalUses = 0;

        FindIterable<Document> documents = db.getCollection("deathpolicyuses").find(and(eq("playerid", playerUUID), gte("occurred", since.atZone(ZoneId.systemDefault()).toInstant())));

        for (Document document: documents) {
            totalUses += 1;
        }

        return totalUses;

    }

    public static boolean getPlayerIsCitizen(Player player) {

        String playerUUID = player.getUniqueId().toString();

        if(!playerOptions.containsKey(playerUUID)) {
            initialisePlayerOnJoin(player);
        }

        return playerOptions.get(playerUUID).isCitizen();

    }

    public static boolean isBananoMap(MapView map) {

        if(map != null) {

            FindIterable<Document> documents = db.getCollection("maps")
                                                 .find(eq("_id", map.getId()));

            return documents.first() != null;

        }

        return false;
    }

    public static boolean createMapRecord(MapView map, Player owner, MonKeyType mapType, String imageFileName) {

        if(map != null) {

            if (maps.containsKey(map.getId())) {
                return true;
            }

            Document document1 = new Document("_id", map.getId())
                    .append("owneruuid", owner.getUniqueId().toString())
                    .append("ownername", owner.getName())
                    .append("created", System.currentTimeMillis())
                    .append("type", mapType.toString())
                    .append("filename", imageFileName);

            db.getCollection("maps").insertOne(document1);

            return true;

        }

        return false;

    }

    public static MonkeyMap getMapRecord(ConfigEngine configEngine, MapView map) {

        if(map != null) {

            if(maps.containsKey(map.getId())) {
                return maps.get(map.getId());
            }

            FindIterable<Document> documents = db.getCollection("maps").find(eq("_id", map.getId()));

            Document document = documents.first();

            if(document != null) {

                UUID id = UUID.fromString(document.getString("owneruuid"));

                Player player = Bukkit.getPlayer(id);

                if(player == null) {

                    player = Bukkit.getOfflinePlayer(id).getPlayer();

                }

                MonKeyType mapType = MonKeyType.valueOf(document.getString("type"));
                String fileName = document.getString("filename");

                MonkeyMap monkeyMap = new MonkeyMap(map.getId(), player, mapType, fileName, configEngine.getAbsoluteDataDirectoryPath(mapType) + fileName);

                maps.put(map.getId(), monkeyMap);

                return monkeyMap;

            }

        }

        return null;

    }

    public static Document createPlayerRecord(Player player) {

        String playerUUID = player.getUniqueId().toString();
        Location spawnLocation = player.getBedSpawnLocation();

        if(spawnLocation == null) {
            spawnLocation = plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }

        Document document1 = new Document("_id", playerUUID)
                .append("name", player.getName())
                .append("joined", System.currentTimeMillis())
                .append("iscitizen", player.isOp())
                .append("isthefuzz", player.isOp())
                .append("shieldsup", false)
                .append("pvpoptedin", false)
                .append("lastdonation", 0)
                .append("deathinsurance", DeathInsuranceOption.None.toString())
                .append("homeWorld", spawnLocation.getWorld().getName())
                .append("homeX", spawnLocation.getBlockX())
                .append("homeY", spawnLocation.getBlockY())
                .append("homeZ", spawnLocation.getBlockZ());

        db.getCollection("users").insertOne(document1);

        return document1;

    }

    /**
     * @returns: a new record if player doesn't exist.
     */
    public static Document getUserDBEntry(Player player) {

        String playerUUID = player.getUniqueId().toString();
        Document query = new Document("_id", playerUUID);
        Document user = db.getCollection("users").find(query).first();

        if(user == null) {

            createPlayerRecord(player);

        }

        return user;
    }

    public static Document getUserDBEntry(OfflinePlayer player) {

        return getUserDBEntry(player.getPlayer());
    }

    public static List<OfflinePlayer> FindPlayerByName(String playerName) {

        List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

        if(playerName.length() > 0) {

            Document query = new Document("name", playerName);
            FindIterable<Document> users = db.getCollection("users").find(query);

            for (Document user : users) {

                UUID id = UUID.fromString(user.getString("_id"));

                OfflinePlayer player = Bukkit.getOfflinePlayer(id);

                if(player != null) {

                    players.add(player);

                }

            }

        }

        return players;
    }

    public static void initialisePlayerOnJoin(Player player) {

        try {

            Document playerRecord = getUserDBEntry(player);

            if(playerRecord != null) {

                DeathInsuranceOption deathInsuranceOption = DeathInsuranceOption.valueOf(playerRecord.getString("deathinsurance"));
                boolean pvpOptedIn = playerRecord.getBoolean("pvpoptedin");
                boolean isCitizen = playerRecord.getBoolean("iscitizen");
                Location homeLocation = player.getBedSpawnLocation();
                boolean isTheFuzz = playerRecord.getBoolean("isthefuzz");
                boolean shieldsUp = playerRecord.getBoolean("shieldsup");

                Optional<World> world = Bukkit.getWorlds().stream().filter(w -> w.getName().equalsIgnoreCase(playerRecord.getString("homeWorld"))).findFirst();

                if (world.isPresent()) {

                    // Getting a cast exception with negative values...
                    homeLocation = new Location(world.get(),
                                                (double)playerRecord.getInteger("homeX"),
                                                (double)playerRecord.getInteger("homeY"),
                                                (double)playerRecord.getInteger("homeZ"));

                }

                PlayerSuiteOption option = new PlayerSuiteOption(player,
                                                                 deathInsuranceOption,
                                                                 pvpOptedIn,
                                                                 isCitizen,
                                                                 isTheFuzz,
                                                                 shieldsUp,
                                                                 homeLocation);

                playerOptions.put(player.getUniqueId().toString(), option);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deinitialisePlayerOnLeave(Player player) {

        try {

            String playerId = player.getUniqueId().toString();

            if(playerOptions.containsKey(playerId)) {

                playerOptions.remove(playerId);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
