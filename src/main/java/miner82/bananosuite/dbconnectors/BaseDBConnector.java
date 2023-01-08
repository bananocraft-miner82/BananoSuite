package miner82.bananosuite.dbconnectors;

import miner82.bananosuite.BananoSuitePlugin;
import miner82.bananosuite.classes.*;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.time.LocalDateTime;
import java.util.*;

public class BaseDBConnector implements IDBConnection {

    protected final BananoSuitePlugin plugin;
    protected final ConfigEngine configEngine;

    protected final HashMap<UUID, PlayerRecord> playerRecords = new HashMap<UUID, PlayerRecord>(); // UUID as key
    protected final HashMap<UUID, MonkeyMap> maps = new HashMap<UUID, MonkeyMap>();

    public BaseDBConnector(BananoSuitePlugin plugin, ConfigEngine configEngine) {

        this.plugin = plugin;
        this.configEngine = configEngine;

    }

    @Override
    public void shutdown() {

    }

    @Override
    public PlayerRecord getPlayerRecord(UUID playerUUID) {

        if(playerRecords.containsKey(playerUUID)) {

            return playerRecords.get(playerUUID);

        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);

        return getPlayerRecord(player.getPlayer());

    }

    @Override
    public PlayerRecord getPlayerRecord(Player player) {

        UUID key = player.getUniqueId();

        if(playerRecords.containsKey(key)) {

            return playerRecords.get(key);

        }

        return loadPlayerRecord(player);

    }

    @Override
    public PlayerRecord getPlayerRecord(OfflinePlayer player) {

        return getPlayerRecord(player.getPlayer());

    }

    protected PlayerRecord loadPlayerRecord(Player player) {

        UUID key = player.getUniqueId();

        if(playerRecords.containsKey(key)) {

            return playerRecords.get(key);

        }
        else {

            return createPlayerRecord(player);

        }

    }

    protected PlayerRecord createPlayerRecord(Player player) {

        return null;

    }

    @Override
    public boolean initialisePlayerOnJoin(Player player) {

        PlayerRecord playerRecord = loadPlayerRecord(player);

        return playerRecord != null;

    }

    @Override
    public boolean deinitialisePlayerOnLeave(Player player) {

        boolean success = false;

        try {

            PlayerRecord playerRecord = getPlayerRecord(player);

            success = save(playerRecord);

            this.playerRecords.remove(player.getUniqueId());

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return success;
    }

    @Override
    public boolean save(PlayerRecord playerRecord) {

        return false;

    }

    @Override
    public void recordPlayerDonation(Player player, double donation) {

    }

    @Override
    public void recordPlayerInsuredDeath(Player player, DeathInsuranceOption deathInsuranceOption, double feePaid, double feeDue, String originalDeathMessage, String errorMessage) {

    }

    @Override
    public int getPlayerDIUseCountInLast24Hours(UUID playerUUID) {

        return 0;

    }

    @Override
    public int getPlayerDIUseCountSinceDate(UUID playerUUID, LocalDateTime since) {

        return 0;

    }

    @Override
    public void loadMaps() {

    }

    @Override
    public boolean isBananoMap(MapView map) {

        if(maps.values().stream().anyMatch(x -> x.getMapId() == map.getId())) {

            return true;

        }

        return false;

    }

    @Override
    public MonkeyMap getMapRecord(ConfigEngine configEngine, MapView map) {

        Optional<MonkeyMap> optionalMap = maps.values().stream().filter(x -> x.getMapId() == map.getId()).findFirst();

        if(optionalMap.isPresent()) {

            return optionalMap.get();

        }

        return null;

    }

    @Override
    public List<MonkeyMap> getMapsForCollection(Player player) {

        List<MonkeyMap> maps = new ArrayList<>();

        for(MonkeyMap map : this.maps.values().stream().filter(x -> x.getOwnerUUID().equals(player.getUniqueId())
                                                                      && x.getStatus() == MonKeyMapStatus.AwaitingCollection).toList()) {

            maps.add(map);

        }

        return maps;

    }

    @Override
    public List<MonkeyMap> getFailedMaps(Player player) {

        List<MonkeyMap> maps = new ArrayList<>();

        for(MonkeyMap map : this.maps.values().stream().filter(x -> x.getOwnerUUID().equals(player.getUniqueId())
                                                                     && (x.getStatus() == MonKeyMapStatus.AwaitingPayment
                                                                            || x.getStatus() == MonKeyMapStatus.ReadyForGeneration)).toList()) {

            maps.add(map);

        }

        return maps;

    }

    @Override
    public int getUnfinishedMapCount(Player forPlayer) {

        int mapCount = this.maps.values().stream().filter(x -> x.getOwnerUUID().equals(forPlayer.getUniqueId())
                                                                && x.getStatus() != MonKeyMapStatus.Complete
                                                                && x.getStatus() != MonKeyMapStatus.AwaitingCollection).toList().size();

        return mapCount;

    }

    @Override
    public int getReadyForCollectionMapCount(Player forPlayer) {

        int mapCount = this.maps.values().stream().filter(x -> x.getOwnerUUID().equals(forPlayer.getUniqueId())
                && x.getStatus() == MonKeyMapStatus.AwaitingCollection).toList().size();

        return mapCount;

    }

    @Override
    public boolean createMapRecord(MonkeyMap map) {

        this.maps.put(map.getId(), map);

        return true;

    }

    @Override
    public boolean save(MonkeyMap map) {

        return false;

    }

    @Override
    public boolean reloadMaps() {

        this.maps.clear();

        loadMaps();

        return true;

    }

}
