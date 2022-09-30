package miner82.bananosuite.interfaces;

import miner82.bananosuite.classes.*;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import java.time.LocalDateTime;
import java.util.*;

public interface IDBConnection {

    void shutdown();

    PlayerRecord getPlayerRecord(UUID playerUUID);
    PlayerRecord getPlayerRecord(Player player);
    PlayerRecord getPlayerRecord(OfflinePlayer player);
    boolean initialisePlayerOnJoin(Player player);
    boolean deinitialisePlayerOnLeave(Player player);
    boolean save(PlayerRecord playerRecord);

    void recordPlayerDonation(Player player, double donation);

    void recordPlayerInsuredDeath(Player player, DeathInsuranceOption deathInsuranceOption, double feePaid, double feeDue, String originalDeathMessage, String errorMessage);
    int getPlayerDIUseCountInLast24Hours(UUID playerUUID);
    int getPlayerDIUseCountSinceDate(UUID playerUUID, LocalDateTime since);

    void loadMaps();
    boolean isBananoMap(MapView map);
    boolean createMapRecord(MonkeyMap map);
    MonkeyMap getMapRecord(ConfigEngine configEngine, MapView map);
    List<MonkeyMap> getMapsForCollection(Player player);
    List<MonkeyMap> getFailedMaps(Player player);
    int getUnfinishedMapCount(Player forPlayer);
    int getReadyForCollectionMapCount(Player forPlayer);
    boolean save(MonkeyMap map);


}
