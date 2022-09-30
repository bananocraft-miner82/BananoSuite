package miner82.bananosuite.dbconnectors;

import com.google.gson.*;
import miner82.bananosuite.BananoSuitePlugin;
import miner82.bananosuite.classes.*;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class JsonDBConnector extends BaseDBConnector {

    private final String DATA_DIRECTORY = "data";
    private File dataLocation;

    private final String mapsFile = "maps.json";

    public JsonDBConnector(BananoSuitePlugin plugin, ConfigEngine configEngine) {

        super(plugin, configEngine);

        initialiseDataDirectory();

        tryCreateFile(mapsFile);

        loadMaps();

    }

    private void initialiseDataDirectory() {

        this.dataLocation = new File(plugin.getDataFolder(), DATA_DIRECTORY);

        if(!this.dataLocation.exists()) {

            this.dataLocation.mkdirs();

        }

    }

    private void tryCreateFile(String fileName) {

        try {

            File file = new File(this.dataLocation, fileName);

            if (!file.exists()) {
                file.createNewFile();
            }

        }
        catch (IOException ex) {

            System.out.println("Error accessing file '" + fileName + ": " + ex.getMessage());
            ex.printStackTrace();

        }

    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    protected PlayerRecord loadPlayerRecord(Player player) {

        String key = player.getUniqueId().toString();

        if(playerRecords.containsKey(key)) {

            return playerRecords.get(key);

        }

        // Retrieve the player record
        try {

            File file = new File(this.dataLocation, player.getUniqueId().toString() + ".json");
            System.out.println("Loading player file from " + file.getAbsolutePath());

            if(!file.exists()) {

                file.createNewFile();

            }

            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
                    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
                }
            })
            .registerTypeAdapter(PlayerRank.class, new JsonDeserializer<PlayerRank>() {
                @Override
                public PlayerRank deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    try {
                        return PlayerRank.valueOf(json.getAsJsonPrimitive().getAsString());
                    }
                    catch (Exception ex) {

                    }

                    return PlayerRank.None;
                }
            })
            .registerTypeAdapter(DeathInsuranceOption.class, new JsonDeserializer<DeathInsuranceOption>() {
                @Override
                public DeathInsuranceOption deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    try {
                        return DeathInsuranceOption.valueOf(json.getAsJsonPrimitive().getAsString());
                    }
                    catch (Exception ex) {

                    }

                    return DeathInsuranceOption.None;
                }
            })
            .registerTypeAdapter(Location.class, new JsonDeserializer<Location>() {
                @Override
                public Location deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    try {
                        JsonObject loc = json.getAsJsonObject();

                        UUID worldId = UUID.fromString(loc.getAsJsonPrimitive("worldid").getAsString());
                        int locX = loc.getAsJsonPrimitive("x").getAsInt();
                        int locY = loc.getAsJsonPrimitive("y").getAsInt();
                        int locZ = loc.getAsJsonPrimitive("z").getAsInt();

                        World world = Bukkit.getWorld(worldId);

                        if(world != null) {
                            return new Location(world, locX, locY, locZ);
                        }
                        else {
                            return player.getBedSpawnLocation();
                        }
                    }
                    catch (Exception ex) {

                    }

                    return player.getBedSpawnLocation();
                }
            }).create();

            Reader fileReader = new FileReader(file);

            PlayerRecord playerRecord = gson.fromJson(fileReader, PlayerRecord.class);

            fileReader.close();

            if(playerRecord != null) {

                if (!playerRecords.containsKey(playerRecord.getUUID())) {

                    playerRecords.put(playerRecord.getUUID(), playerRecord);
                    System.out.println("Player loaded: " + playerRecord.getPlayerName());

                    return playerRecord;

                }

            }
            else {

                return createPlayerRecord(player);

            }

        }
        catch (IOException ex) {

            System.out.println("Loading players file failed!");
            ex.printStackTrace();

        }

        return null;

    }

    @Override
    protected PlayerRecord createPlayerRecord(Player player) {

        try {

            UUID playerUUID = player.getUniqueId();

            PlayerRecord playerRecord = new PlayerRecord(playerUUID, player.getName(), LocalDateTime.now(),
                                                            DeathInsuranceOption.None, LocalDateTime.now(), false, PlayerRank.None,
                                                            false, player.getBedSpawnLocation());

            if(save(playerRecord)) {

                playerRecords.put(playerUUID, playerRecord);

                return playerRecord;

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public boolean save(PlayerRecord playerRecord) {

        try {

            File file = new File(this.dataLocation, playerRecord.getUUID() + ".json");

            System.out.println("Saving player file to " + file.getAbsolutePath());

            if(!file.exists()) {

                file.createNewFile();

            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                        @Override
                        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                            return new JsonPrimitive(src.toInstant(ZoneOffset.UTC).toEpochMilli());
                        }
                    })
                    .registerTypeAdapter(PlayerRank.class, new JsonSerializer<PlayerRank>() {
                        @Override
                        public JsonElement serialize(PlayerRank src, Type typeOfSrc, JsonSerializationContext context) {
                            return new JsonPrimitive(src.name());
                        }
                    })
                    .registerTypeAdapter(DeathInsuranceOption.class, new JsonSerializer<DeathInsuranceOption>() {
                        @Override
                        public JsonElement serialize(DeathInsuranceOption src, Type typeOfSrc, JsonSerializationContext context) {
                            return new JsonPrimitive(src.name());
                        }
                    })
                    .registerTypeAdapter(Location.class, new JsonSerializer<Location>() {
                        @Override
                        public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
                            JsonObject obj = new JsonObject();

                            obj.addProperty("worldid", src.getWorld().getUID().toString());
                            obj.addProperty("x", src.getBlockX());
                            obj.addProperty("y", src.getBlockY());
                            obj.addProperty("z", src.getBlockZ());

                            return obj;
                        }
                    }).create();

            Writer fileWriter = new FileWriter(file, false);

            gson.toJson(playerRecord, fileWriter);

            fileWriter.flush();
            fileWriter.close();

            System.out.println("Player file '" + file.getName() + "' has been saved successfully.");

            return true;

        }
        catch (IOException ex) {

            System.out.println("Player file save has failed.");
            ex.printStackTrace();

        }

        return false;

    }

    @Override
    public void recordPlayerDonation(Player player, double donation) {
        // Don't log it in this mode
    }

    @Override
    public void recordPlayerInsuredDeath(Player player, DeathInsuranceOption deathInsuranceOption, double feePaid, double feeDue, String originalDeathMessage, String errorMessage) {
        // Don't log it in this mode
    }

    @Override
    public int getPlayerDIUseCountInLast24Hours(UUID playerUUID) {

        PlayerRecord playerRecord = playerRecords.get(playerUUID);

        if(playerRecord != null
             && playerRecord.getLastDIPolicyUsage().isAfter(LocalDateTime.now().minusDays(1))) {

            return 1;

        }

        return 0;

    }

    @Override
    public int getPlayerDIUseCountSinceDate(UUID playerUUID, LocalDateTime since) {

        PlayerRecord playerRecord = playerRecords.get(playerUUID);

        if(playerRecord != null
                && playerRecord.getLastDIPolicyUsage().isAfter(since)) {

            return 1;

        }

        return 0;

    }

    @Override
    public void loadMaps() {

        try {

            File file = new File(this.dataLocation, mapsFile);

            System.out.println("Loading MonKeyMaps file from " + file.getAbsolutePath());

            if(!file.exists()) {

                file.createNewFile();

            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                        @Override
                        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                            Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
                            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
                        }
                    })
                    .registerTypeAdapter(MonKeyType.class, new JsonDeserializer<MonKeyType>() {
                        @Override
                        public MonKeyType deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                            try {
                                return MonKeyType.valueOf(json.getAsJsonPrimitive().getAsString());
                            }
                            catch (Exception ex) {

                            }

                            return MonKeyType.MonKey;
                        }
                    })
                    .registerTypeAdapter(MonKeyMapStatus.class, new JsonDeserializer<MonKeyMapStatus>() {
                        @Override
                        public MonKeyMapStatus deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                            try {
                                return MonKeyMapStatus.valueOf(json.getAsJsonPrimitive().getAsString());
                            }
                            catch (Exception ex) {

                            }

                            return MonKeyMapStatus.ReadyForGeneration;
                        }
                    }).create();

            Reader fileReader = new FileReader(file);

            MonkeyMap[] records = gson.fromJson(fileReader, MonkeyMap[].class);

            fileReader.close();

            if(records != null
                    && records.length > 0) {

                for (MonkeyMap record : records) {

                    maps.put(record.getId(), record);

                }

            }

            System.out.println(maps.size() + " MonKeyMaps loaded!");

        }
        catch (IOException ex) {

            System.out.println("Loading banned blocks file failed!");
            ex.printStackTrace();

        }

    }

    @Override
    public boolean createMapRecord(MonkeyMap map) {

        super.createMapRecord(map);

        save(map);

        return true;

    }

    @Override
    public boolean save(MonkeyMap map) {

        try {

            File file = new File(this.dataLocation, mapsFile);

            System.out.println("Saving MonKeyMaps file to " + file.getAbsolutePath());

            if(!file.exists()) {

                file.createNewFile();

            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                            @Override
                            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                                return new JsonPrimitive(src.toInstant(ZoneOffset.UTC).toEpochMilli());
                            }
                        }
                    )
                    .registerTypeAdapter(MonKeyType.class, new JsonSerializer<MonKeyType>() {
                            @Override
                            public JsonElement serialize(MonKeyType src, Type typeOfSrc, JsonSerializationContext context) {
                                return new JsonPrimitive(src.name());
                            }
                        }
                    )
                    .registerTypeAdapter(MonKeyMapStatus.class, new JsonSerializer<MonKeyMapStatus>() {
                            @Override
                            public JsonElement serialize(MonKeyMapStatus src, Type typeOfSrc, JsonSerializationContext context) {
                                return new JsonPrimitive(src.name());
                            }
                        }
                    ).create();

            Writer fileWriter = new FileWriter(file, false);

            gson.toJson(maps.values().toArray(), fileWriter);

            fileWriter.flush();
            fileWriter.close();

            System.out.println("MonKeyMaps file has been saved successfully.");

            return true;

        }
        catch (IOException ex) {

            System.out.println("MonKeyMaps file save has failed.");
            ex.printStackTrace();

        }

        return false;

    }

}
