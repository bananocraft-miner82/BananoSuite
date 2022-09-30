package miner82.bananosuite.events;

import miner82.bananosuite.classes.PlayerRecord;
import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnOPDamageEvent implements Listener {

    private final IDBConnection db;

    public OnOPDamageEvent(IDBConnection db) {
        this.db = db;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent args) {

        if(args.getEntity() instanceof Player) {

            Player player = (Player) args.getEntity();
            PlayerRecord playerRecord = db.getPlayerRecord(player);

            if(player.isOp()
                 && playerRecord != null
                 && playerRecord.isShieldsUp()) {

                args.setDamage(0);
                args.setCancelled(true);

            }

        }

    }

}
