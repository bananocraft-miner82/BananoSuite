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
            else if(args.getDamager().getType() == EntityType.ARROW) {

                Projectile arrow = (Arrow) args.getDamager();
                if(arrow.getShooter() instanceof Entity) {

                    damager = (Entity) arrow.getShooter();

                }

            } else if (args.getDamager().getType() == EntityType.SPLASH_POTION) {

                Projectile potion = (ThrownPotion) args.getDamager();
                damager = (Entity) potion.getShooter();

            }

            if(damager != null
                 && damager instanceof Player) {

                // Damage is done from one player to another
                Player taker = (Player) args.getEntity();
                Player damagerPlayer = (Player) args.getDamager();

                if (!DB.getPlayerPvPOptIn(taker)
                        && !taker.equals(damagerPlayer)) {

                    args.setDamage(0);

                    damagerPlayer.damage(1, damagerPlayer);
                    damagerPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "This player has opted-out for PvP!");

                }

            }

        }

    }
}
