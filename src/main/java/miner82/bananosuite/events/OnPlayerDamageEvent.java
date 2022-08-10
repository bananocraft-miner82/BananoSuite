package miner82.bananosuite.events;

import miner82.bananosuite.DB;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnPlayerDamageEvent implements Listener {


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

                boolean takerOptedOut = !DB.getPlayerPvPOptIn(taker);
                boolean damagerOptedOut = !DB.getPlayerPvPOptIn(damagerPlayer);

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
