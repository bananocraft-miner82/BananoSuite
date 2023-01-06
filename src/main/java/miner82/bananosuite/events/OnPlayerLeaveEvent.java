package miner82.bananosuite.events;

import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerLeaveEvent implements Listener {

    private final IDBConnection db;

    public OnPlayerLeaveEvent(IDBConnection db) {
        this.db = db;
    }

    @EventHandler
    public void onLeaveServer(PlayerQuitEvent event){

        Player player = event.getPlayer();

        if(db.deinitialisePlayerOnLeave(player)) {

            System.out.println("BananoSuite Player Profile for " + player.getName() + " [" + player.getUniqueId() + "] unloaded successfully!");

        }
        else {

            System.out.println("BananoSuite Player Profile for " + player.getName() + " [" + player.getUniqueId() + "]  had no changes to save, or encountered issues while unloading!");

        }

    }

}
