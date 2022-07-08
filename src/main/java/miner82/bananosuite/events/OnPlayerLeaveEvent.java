package miner82.bananosuite.events;

import miner82.bananosuite.DB;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerLeaveEvent implements Listener {

    @EventHandler
    public void onLeaveServer(PlayerQuitEvent event){

        Player player = event.getPlayer();

        DB.deinitialisePlayerOnLeave(player);

    }

}
