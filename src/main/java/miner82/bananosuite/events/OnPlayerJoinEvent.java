package miner82.bananosuite.events;

import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinEvent implements Listener {

    private final IDBConnection db;

    public OnPlayerJoinEvent(IDBConnection db) {
        this.db = db;
    }

    @EventHandler
    public void onJoinServer(PlayerJoinEvent event){

        Player player = event.getPlayer();

        if(this.db.initialisePlayerOnJoin(player)) {

            System.out.println("BananoSuite Player Profile for " + player.getName() + " [" + player.getUniqueId() + "] loaded successfully!");

        }
        else {

            System.out.println("BananoSuite Player Profile for " + player.getName() + " [" + player.getUniqueId() + "] could not be loaded!");

        }

        if(this.db.getMapsForCollection(player).size() > 0) {

            player.sendMessage(ChatColor.GOLD + "You have MonkeyMaps awaiting collection!");

        }

    }


}
