package miner82.bananosuite.events;

import miner82.bananosuite.renderers.monKeyRenderer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapCloneEvent implements Listener {

    @EventHandler
    public void OnMapClone(PrepareItemCraftEvent args) {

        ItemStack result = args.getInventory().getResult();

        if (result != null) {

            if (result.getType() == Material.FILLED_MAP
                    && result.getAmount() >= 2) {
                System.out.println("Trying to clone a map");
                ItemStack firstSlot = args.getInventory().getItem(0);

                if (firstSlot != null
                        && firstSlot.getType() == Material.FILLED_MAP
                        && firstSlot.hasItemMeta()
                        && firstSlot.getItemMeta() instanceof MapMeta) {

                    MapMeta meta = (MapMeta) firstSlot.getItemMeta();

                    if (meta.hasMapView()) {

                        MapView mapView = meta.getMapView();

                        for (MapRenderer renderer : mapView.getRenderers()) {

                            if(renderer instanceof monKeyRenderer) {

                                args.getInventory().setResult(new ItemStack(Material.AIR));

                                for (HumanEntity humanEntity : args.getViewers()) {
                                    Player player = (Player) humanEntity;
                                    player.sendMessage(ChatColor.DARK_RED + "You cannot duplicate purchased maps!");
                                }

                                return;

                            }

                        }

                    }

                }

            }

        }

    }

}
