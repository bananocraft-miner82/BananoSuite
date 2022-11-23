package miner82.bananosuite.dbconnectors;

import com.zaxxer.hikari.HikariDataSource;
import miner82.bananosuite.BananoSuitePlugin;
import miner82.bananosuite.classes.*;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class MySQLDBConnector extends BaseDBConnector {

    private final HikariDataSource dataSource;

    public MySQLDBConnector(BananoSuitePlugin plugin, ConfigEngine configEngine) {

        super(plugin, configEngine);

        this.dataSource = new HikariDataSource();
        this.dataSource.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        this.dataSource.addDataSourceProperty("serverName", configEngine.getMysqlServerName());
        this.dataSource.addDataSourceProperty("port", configEngine.getMysqlPort());
        this.dataSource.addDataSourceProperty("databaseName", configEngine.getMysqlDatabaseName());
        this.dataSource.addDataSourceProperty("user", configEngine.getMysqlUsername());
        this.dataSource.addDataSourceProperty("password", configEngine.getMysqlPassword());

        this.dataSource.setIdleTimeout(45000);
        this.dataSource.setMaxLifetime(60000);
        this.dataSource.setMinimumIdle(5);

        if(Bukkit.getMaxPlayers() > 50) {
            this.dataSource.setMaximumPoolSize(50);
        }
        else if(Bukkit.getMaxPlayers() < 5) {
            this.dataSource.setMaximumPoolSize(5);
        }
        else {
            this.dataSource.setMaximumPoolSize(Bukkit.getMaxPlayers());
        }

        setupDatabase();

        loadMaps();

    }

    private Connection getConnection() {

        try {

            if (this.dataSource != null) {

                return this.dataSource.getConnection();

            }

        }
        catch (Exception ex) {

            ex.printStackTrace();

        }

        return null;

    }

    private void setupDatabase() {

        // If it's the first time connecting, let's set up the tables etc.
        Connection connection = getConnection();

        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                            "    playerUUID     VARCHAR(75) NOT NULL UNIQUE," +
                            "    name           VARCHAR(50) NOT NULL," +
                            "    joined         TIMESTAMP NOT NULL," +
                            "    deathinsurance VARCHAR(20) NOT NULL," +
                            "    lastpolicyuse  TIMESTAMP NOT NULL," +
                            "    pvpoptin       BOOLEAN NOT NULL DEFAULT FALSE," +
                            "    playerrank     VARCHAR(20) NOT NULL," +
                            "    shieldsup      BOOLEAN NOT NULL DEFAULT FALSE," +
                            "    homeworldId    VARCHAR(50) NOT NULL, " +
                            "    homeworldName  VARCHAR(50) NOT NULL, " +
                            "    homex          INT NOT NULL, " +
                            "    homey          INT NOT NULL, " +
                            "    homez          INT NOT NULL" +
                            ")  ENGINE=INNODB";

        try {

            PreparedStatement userTableCreator = connection.prepareStatement(usersTable);

            userTableCreator.execute();
            userTableCreator.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        String mapsTable = "CREATE TABLE IF NOT EXISTS maps ( " +
                           "    id             VARCHAR(75) NOT NULL UNIQUE, " +
                           "    mapid          INT NOT NULL, " +
                           "    playeruuid     VARCHAR(75) NOT NULL," +
                           "    created        TIMESTAMP NOT NULL, " +
                           "    maptype        VARCHAR(50) NOT NULL, " +
                           "    walletaddress  VARCHAR(128) NOT NULL, " +
                           "    additionaltext VARCHAR(50) NOT NULL, " +
                           "    frame          VARCHAR(25), " +
                           "    imagefilename  VARCHAR(250) NOT NULL, " +
                           "    imagefilepath  VARCHAR(250) NOT NULL," +
                           "    status         VARCHAR(25) NOT NULL" +
                           ")  ENGINE=INNODB";

        try {

            PreparedStatement mapsTableCreator = connection.prepareStatement(mapsTable);

            mapsTableCreator.execute();
            mapsTableCreator.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        String insuredDeathsTable = "CREATE TABLE IF NOT EXISTS insureddeaths ( " +
                                    "    playerUUID  VARCHAR(75) NOT NULL, " +
                                    "    created     TIMESTAMP NOT NULL, " +
                                    "    policytype  VARCHAR(50) NOT NULL, " +
                                    "    feepaid     DECIMAL(9,2) NOT NULL, " +
                                    "    feedue      DECIMAL(9,2) NOT NULL, " +
                                    "    original    VARCHAR(100) NOT NULL, " +
                                    "    errormsg    VARCHAR(100) NOT NULL " +
                                    ")  ENGINE=INNODB";

        try {

            PreparedStatement insuredDeathsTableCreator = connection.prepareStatement(insuredDeathsTable);

            insuredDeathsTableCreator.execute();
            insuredDeathsTableCreator.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            connection.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void shutdown() {

        super.shutdown();

        try {
            this.dataSource.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected PlayerRecord loadPlayerRecord(Player player) {

        UUID key = player.getUniqueId();

        if(playerRecords.containsKey(key)) {

            return playerRecords.get(key);

        }

        // Retrieve the player record
        PlayerRecord playerRecord = null;
        Connection connection = getConnection();

        try {

            // Query the database

            PreparedStatement query = connection.prepareStatement("SELECT playerUUID, name, joined, deathinsurance, lastpolicyuse, pvpoptin, playerrank, shieldsup, homeworldid, homex, homey, homez " +
                                                                      "FROM users " +
                                                                      "WHERE playerUUID = ?");

            query.setString(1, key.toString());

            ResultSet results = query.executeQuery();

            // We're only expecting one row - and should only ever have one per player!
            if(results != null
                    && results.next()) {

                World world = Bukkit.getWorld(UUID.fromString(results.getString("homeworldid")));

                if(world == null) {

                    world = player.getBedSpawnLocation().getWorld();

                }


                //playerUUID, name, joined, deathinsurance, lastpolicyuse, pvpoptin, playerrank, shieldsup, homeworld, homex, homey, homez
                playerRecord = new PlayerRecord(key, player.getName(),
                                                results.getTimestamp("joined").toLocalDateTime(),
                                                DeathInsuranceOption.valueOf(results.getString("deathinsurance")),
                                                results.getTimestamp("lastpolicyuse").toLocalDateTime(),
                                                results.getBoolean("pvpoptin"),
                                                PlayerRank.valueOf(results.getString("playerrank")),
                                                player.isOp() && results.getBoolean("shieldsup"),
                                                new Location(world,
                                                              results.getInt("homex"),
                                                              results.getInt("homey"),
                                                              results.getInt("homez")));


            }
            else {

                playerRecord = createPlayerRecord(player);

            }

            playerRecords.put(key, playerRecord);

            try {
                results.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                query.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            try {

                connection.close();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return playerRecord;

    }

    @Override
    protected PlayerRecord createPlayerRecord(Player player) {

        PlayerRecord playerRecord = null;
        Connection connection = getConnection();

        Location playerHomeLocation = player.getBedSpawnLocation();

        if(playerHomeLocation == null) {

            playerHomeLocation = player.getWorld().getSpawnLocation();

        }

        try {

            UUID playerUUID = player.getUniqueId();

            PreparedStatement insert = connection.prepareStatement("INSERT INTO users (playerUUID, name, joined, deathinsurance, lastpolicyuse, pvpoptin, playerrank, shieldsup, homeworldid, homeworldname, homex, homey, homez) " +
                                                                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            //playerUUID, name, joined, deathinsurance, lastpolicyuse, pvpoptin, playerrank, shieldsup, homeworld, homex, homey, homez
            insert.setString(1, playerUUID.toString());
            insert.setString(2, player.getName());
            insert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            insert.setString(4, DeathInsuranceOption.None.name());
            insert.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            insert.setBoolean(6, false);
            insert.setString(7, PlayerRank.None.name());
            insert.setBoolean(8, false);
            insert.setString(9, playerHomeLocation.getWorld().getUID().toString());
            insert.setString(10, playerHomeLocation.getWorld().getName());
            insert.setInt(11, playerHomeLocation.getBlockX());
            insert.setInt(12, playerHomeLocation.getBlockY());
            insert.setInt(13, playerHomeLocation.getBlockZ());


            insert.executeUpdate();
            insert.close();

            playerRecord = new PlayerRecord(playerUUID, player.getName(), LocalDateTime.now(),
                                                DeathInsuranceOption.None, LocalDateTime.now(), false, PlayerRank.None,
                                                false, player.getBedSpawnLocation());

            playerRecords.put(playerUUID, playerRecord);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            try {

                connection.close();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return playerRecord;

    }

    @Override
    public boolean save(PlayerRecord playerRecord) {

        if(playerRecord == null) {

            return false;

        }

        boolean result = false;
        Connection connection = getConnection();

        Location homeLocation = playerRecord.getHomeLocation();

        if(homeLocation == null) {

            Player player = Bukkit.getPlayer(playerRecord.getUUID());

            if(player != null) {

                if(player.getBedSpawnLocation() != null) {

                    homeLocation = player.getBedSpawnLocation();

                }
                else {

                    homeLocation = player.getWorld().getSpawnLocation();

                }

            }

        }

        try {

            PreparedStatement insert = connection.prepareStatement("UPDATE users " +
                                                                       "SET deathinsurance = ?, " +
                                                                       "    lastpolicyuse = ?, " +
                                                                       "    pvpoptin = ?, " +
                                                                       "    playerrank = ?, " +
                                                                       "    shieldsup = ?, " +
                                                                       "    homeworldid = ?, " +
                                                                       "    homeworldname = ?, " +
                                                                       "    homex = ?, " +
                                                                       "    homey = ?, " +
                                                                       "    homez = ? " +
                                                                       "WHERE playerUUID = ?");

            insert.setString(1, playerRecord.getDeathInsuranceOption().name());
            insert.setTimestamp(2, Timestamp.valueOf(playerRecord.getLastDIPolicyUsage()));
            insert.setBoolean(3, playerRecord.isPvpOptedIn());
            insert.setString(4, playerRecord.getPlayerRank().name());
            insert.setBoolean(5, playerRecord.isShieldsUp());
            insert.setString(6, homeLocation.getWorld().getUID().toString());
            insert.setString(7, homeLocation.getWorld().getName());
            insert.setInt(8, homeLocation.getBlockX());
            insert.setInt(9, homeLocation.getBlockY());
            insert.setInt(10, homeLocation.getBlockZ());
            insert.setString(11, playerRecord.getUUID().toString());


            result = insert.executeUpdate() > 0;

            insert.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            try {

                connection.close();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return result;

    }

    @Override
    public void recordPlayerDonation(Player player, double donation) {
        // TODO
    }

    @Override
    public void recordPlayerInsuredDeath(Player player, DeathInsuranceOption deathInsuranceOption, double feePaid, double feeDue, String originalDeathMessage, String errorMessage) {

        new BukkitRunnable() {
            @Override
            public void run() {

                Connection connection = getConnection();

                try {

                    String playerUUID = player.getUniqueId().toString();

                    PreparedStatement insert = connection.prepareStatement("INSERT INTO insureddeaths (playerUUID, created, policytype, feepaid, feedue, original, errormsg) " +
                                                                               "VALUES (?, ?, ?, ?, ?, ?, ?)");

                    insert.setString(1, playerUUID);
                    insert.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    insert.setString(3, deathInsuranceOption.name());
                    insert.setDouble(4, feePaid);
                    insert.setDouble(5, feeDue);
                    insert.setString(6, originalDeathMessage);
                    insert.setString(7, errorMessage);


                    insert.executeUpdate();

                    insert.close();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {

                    try {

                        connection.close();

                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

            }
        }.runTaskAsynchronously(this.plugin);

    }

    @Override
    public int getPlayerDIUseCountInLast24Hours(UUID playerUUID) {

        return getPlayerDIUseCountSinceDate(playerUUID, LocalDateTime.now().minusDays(1));

    }

    @Override
    public int getPlayerDIUseCountSinceDate(UUID playerUUID, LocalDateTime since) {

        int uses = 0;
        Connection connection = getConnection();

        try {

            // Query the database
            PreparedStatement query = connection.prepareStatement("SELECT COUNT(*) AS uses " +
                                                                      "FROM insureddeaths " +
                                                                      "WHERE playerUUID = ?" +
                                                                      "       AND created >= ?");

            query.setString(1, playerUUID.toString());
            query.setTimestamp(2, Timestamp.valueOf(since));

            ResultSet results = query.executeQuery();

            // We're only expecting one row - and should only ever have one per player!
            if(results.next()) {

                uses = results.getInt("uses");

            }

            try {
                results.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                query.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            try {

                connection.close();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return uses;

    }

    @Override
    public void loadMaps() {

        Connection connection = getConnection();

        try {

            // Query the database
            PreparedStatement query = connection.prepareStatement("SELECT id, mapid, playeruuid, created, maptype, walletaddress, additionaltext, frame, imagefilename, imagefilepath, status " +
                                                                      "FROM maps");

            ResultSet results = query.executeQuery();

            // We're only expecting one row - and should only ever have one per player!
            while (results.next()) {

                MonkeyMap map = new MonkeyMap(UUID.fromString(results.getString("id")),
                                                UUID.fromString(results.getString("playeruuid")),
                                                MonKeyType.valueOf(results.getString("maptype")),
                                                results.getString("walletaddress"),
                                                results.getString("frame"),
                                                results.getString("additionaltext"));

                map.setMapId(results.getInt("mapid"));
                map.setImageFileName(results.getString("imagefilename"));
                map.setFullFilePath(results.getString("imagefilepath"));
                map.setStatus(MonKeyMapStatus.valueOf(results.getString("status")));

                this.maps.put(map.getId(), map);

            }

            try {
                results.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                query.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            try {

                connection.close();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    @Override
    public boolean createMapRecord(MonkeyMap map) {

        if(map != null) {

            super.createMapRecord(map);

            boolean result = false;

            Connection connection = getConnection();

            try {

                PreparedStatement insert = connection.prepareStatement("INSERT INTO maps " +
                                                                           "(id, mapid, playeruuid, created, maptype, walletaddress, additionaltext, frame, imagefilename, imagefilepath, status) " +
                                                                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                insert.setString(1, map.getId().toString());
                insert.setInt(2, map.getMapId());
                insert.setString(3, map.getOwnerUUID().toString());
                insert.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                insert.setString(5, map.getMapType().name());
                insert.setString(6, map.getWalletAddress());
                insert.setString(7, map.getAdditionalText());
                insert.setString(8, map.getFrame());
                insert.setString(9, map.getImageFileName());
                insert.setString(10, map.getFullFilePath());
                insert.setString(11, map.getStatus().name());

                result = insert.executeUpdate() > 0;

                insert.close();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {

                try {

                    connection.close();

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            return result;

        }

        return false;

    }

    @Override
    public boolean save(MonkeyMap map) {

        boolean result = false;

        Connection connection = getConnection();

        try {

            PreparedStatement update = connection.prepareStatement("UPDATE maps " +
                                                                       "SET mapid = ?, " +
                                                                       "    walletaddress = ?, " +
                                                                       "    additionaltext = ?, " +
                                                                       "    frame = ?, " +
                                                                       "    imagefilename = ?, " +
                                                                       "    imagefilepath = ?, " +
                                                                       "    status = ? " +
                                                                       "WHERE id = ?");

            update.setInt(1, map.getMapId());
            update.setString(2, map.getWalletAddress());
            update.setString(3, map.getAdditionalText());
            update.setString(4, map.getFrame());
            update.setString(5, map.getImageFileName());
            update.setString(6, map.getFullFilePath());
            update.setString(7, map.getStatus().name());
            update.setString(8, map.getId().toString());

            result = update.executeUpdate() > 0;

            update.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            try {

                connection.close();

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return result;

    }

}
