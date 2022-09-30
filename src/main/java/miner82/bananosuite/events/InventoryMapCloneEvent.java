package miner82.bananosuite.events;

import miner82.bananosuite.renderers.monKeyRenderer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class InventoryMapCloneEvent implements Listener {

    @EventHandler
    public void InventoryMapCloneEvent(InventoryClickEvent args) {
        Player player = (Player)args.getWhoClicked();
        ItemStack itemStack = args.getCurrentItem();

        if (itemStack != null
                && args.getSlotType() == InventoryType.SlotType.RESULT) {

            if (itemStack.getType() == Material.FILLED_MAP && itemStack.getAmount() >= 2) {

                if(itemStack.hasItemMeta()
                     && itemStack.getItemMeta() instanceof MapMeta) {

                    MapView mapView = ((MapMeta) itemStack.getItemMeta()).getMapView();

                    for (MapRenderer renderer : mapView.getRenderers()) {

                        if(renderer instanceof monKeyRenderer) {

                            args.setCancelled(true);

                            player.sendMessage(ChatColor.DARK_RED + "You cannot duplicate purchased maps!");

                            return;

                        }

                    }

                }

            }

        }

    }

}
