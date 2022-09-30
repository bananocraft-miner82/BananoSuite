package miner82.bananosuite.events;

import miner82.bananosuite.classes.PlayerRecord;
import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnPlayerDamageEvent implements Listener {

    private final IDBConnection db;

    public OnPlayerDamageEvent(IDBConnection db) {
        this.db = db;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent args) {

        if (args.getEntity() instanceof Player) {

            Entity damager = null;

            if(args.getDamager() instanceof Player) {

                damager = args.getDamager();

            }
            else if(args.getDamager() instanceof Projectile) {

                Projectile projectile = (Projectile) args.getDamager();

                if(projectile.getShooter() instanceof Player) {

                    damager = (Player) projectile.getShooter();

                }

            }

            if(damager != null
                 && damager instanceof Player) {

                // Damage is done from one player to another
                Player taker = (Player) args.getEntity();
                Player damagerPlayer = (Player) args.getDamager();

                PlayerRecord takerRecord = db.getPlayerRecord(taker);
                PlayerRecord damagerRecord = db.getPlayerRecord(damagerPlayer);

                if(takerRecord == null
                     || damagerRecord == null) {

                    return;

                }

                boolean takerOptedOut = !takerRecord.isPvpOptedIn();
                boolean damagerOptedOut = !damagerRecord.isPvpOptedIn();

                if ((takerOptedOut || damagerOptedOut)
                        && !taker.equals(damagerPlayer)) {

                    args.setCancelled(true);

                    if(takerOptedOut) {

                        damagerPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "This player has opted-out for PvP!");

                    }

                    if(damagerOptedOut) {

                        damagerPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "You are opted-out for PvP!");

                    }

                }

            }

        }

    }

}
