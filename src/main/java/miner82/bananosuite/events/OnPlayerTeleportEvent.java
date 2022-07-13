package miner82.bananosuite.events;

import miner82.bananosuite.classes.TeleportPremiumCalculator;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;


public class OnPlayerTeleportEvent implements Listener {

    private ConfigEngine configEngine;

    public OnPlayerTeleportEvent(ConfigEngine engine) {
        this.configEngine = engine;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){

        //Player player = event.getPlayer();

        //if(event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND
        //     && !player.isOp()) {

        //    double teleportCost = TeleportPremiumCalculator.calculateTeleportCost(this.configEngine, event.getFrom(), event.getTo());


        //}

    }

}
