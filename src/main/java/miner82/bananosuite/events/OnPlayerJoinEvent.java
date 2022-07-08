package miner82.bananosuite.events;

import miner82.bananosuite.DB;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinEvent implements Listener {

    @EventHandler
    public void onJoinServer(PlayerJoinEvent event){

        Player player = event.getPlayer();

        DB.initialisePlayerOnJoin(player);

    }


}
