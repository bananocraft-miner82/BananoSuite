package miner82.bananosuite.events;

import miner82.bananosuite.DB;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnOPDamageEvent implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent args) {

        if(args.getEntity() instanceof Player) {

            Player entity = (Player) args.getEntity();

            if(entity.isOp()
                    && DB.getPlayerShieldsUp(entity)) {

                args.setDamage(0);
                args.setCancelled(true);

            }

        }

    }

}
