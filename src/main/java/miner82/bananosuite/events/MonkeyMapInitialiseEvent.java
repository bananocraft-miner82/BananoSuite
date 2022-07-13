package miner82.bananosuite.events;

import miner82.bananosuite.DB;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.renderers.monKeyRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MonkeyMapInitialiseEvent implements Listener {

    private final ConfigEngine configEngine;

    public MonkeyMapInitialiseEvent(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {

        MapView view = event.getMap();

        if(DB.isBananoMap(view)) {

            for(MapRenderer renderer : view.getRenderers()) {
                view.removeRenderer(renderer);
            }

            view.addRenderer(new monKeyRenderer(this.configEngine));

        }
    }

}
