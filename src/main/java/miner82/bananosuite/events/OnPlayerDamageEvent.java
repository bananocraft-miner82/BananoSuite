package miner82.bananosuite.events;

import miner82.bananosuite.DB;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnPlayerDamageEvent implements Listener {


    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent args) {

        if (args.getDamager() instanceof Player
                && args.getEntity() instanceof Player) {

            // Damage is done from one player to another
            Player taker = (Player) args.getEntity();
            Player damagerPlayer = (Player) args.getDamager();

            if(!DB.getPlayerPvPOptIn(taker)
                 && !taker.equals(damagerPlayer)) {

                args.setDamage(0);

                damagerPlayer.damage(1, damagerPlayer);
                damagerPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "This player has opted-out for PvP!");

            }

        }

    }
}
