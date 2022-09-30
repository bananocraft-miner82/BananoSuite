package miner82.bananosuite.events;

import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import miner82.bananosuite.renderers.monKeyRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MonkeyMapInitialiseEvent implements Listener {

    private final IDBConnection db;
    private final ConfigEngine configEngine;

    public MonkeyMapInitialiseEvent(IDBConnection db, ConfigEngine configEngine) {
        this.db = db;
        this.configEngine = configEngine;
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {

        MapView view = event.getMap();

        if(db.isBananoMap(view)
             && !view.getRenderers().stream().allMatch(x -> x instanceof monKeyRenderer)) {

            for(MapRenderer renderer : view.getRenderers()) {
                view.removeRenderer(renderer);
            }

            view.addRenderer(new monKeyRenderer(this.db, this.configEngine));

        }
    }

}
