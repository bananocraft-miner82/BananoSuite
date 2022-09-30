package miner82.bananosuite.commands;

import miner82.bananosuite.BananoSuitePlugin;
import miner82.bananosuite.classes.MonKeyMapStatus;
import miner82.bananosuite.classes.MonkeyMap;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import miner82.bananosuite.classes.MonKeyType;
import miner82.bananosuite.renderers.monKeyRenderer;
import miner82.bananosuite.runnables.MonKeyMapRunnable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MonKeyMapCommand extends BaseCommand implements CommandExecutor {

    private final int ARG_COMMAND = 0;
    private final int ARG_MAPTYPE = 1;
    private final int ARG_ADDRESS = 2;
    private final int ARG_MONKEY_FRAME = 3;
    private final int ARG_QRCODE_ADDITIONAL = 3;
    private final int ARG_MONKEY_ADDITIONAL = 4;

    private final BananoSuitePlugin plugin;
    private final IDBConnection db;
    private final ConfigEngine configEngine;
    private final Economy econ;

    public MonKeyMapCommand(BananoSuitePlugin plugin, IDBConnection db, ConfigEngine configEngine, Economy econ) {
        this.plugin = plugin;
        this.db = db;
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!this.configEngine.getIsEnabled()
                    || !this.configEngine.getMonkeyMapsEnabled()) {

                SendMessage(player, "That command is not enabled on this server.", ChatColor.GOLD);

                return false;

            }

            if (args.length == 0) {

                SendMessage(player, "Arguments must be provided to use this command!", ChatColor.RED);

                return false;

            }

            if (args[ARG_COMMAND].equalsIgnoreCase("help")) {

                SendMessage(player, "MonKeyMaps come in two varieties: QR Codes and MonKeys.", ChatColor.GOLD);
                SendMessage(player, "QR Codes are generated using the address you provide, with any text after replacing your name. These cost " + econ.format(this.configEngine.getMapPrice(MonKeyType.QRCode)) + ".", ChatColor.GOLD);
                SendMessage(player, "MonKeys are the monkey images generated using the address you provide, with any text after replacing your name. These cost " + econ.format(this.configEngine.getMapPrice(MonKeyType.MonKey)) + ".", ChatColor.GOLD);

                return true;

            }
            else if (args[ARG_COMMAND].equalsIgnoreCase("buy")) {

                MonKeyType mapType = args[ARG_MAPTYPE].equalsIgnoreCase("qrcode") ? MonKeyType.QRCode : MonKeyType.MonKey;
                String walletAddress = args[ARG_ADDRESS].toLowerCase(Locale.ROOT).trim();
                String frameKey = "";
                String textOverride = "";

                if (mapType == MonKeyType.MonKey) {

                    if(args.length >= ARG_MONKEY_FRAME
                            && !args[ARG_MONKEY_FRAME].equalsIgnoreCase("none")) {

                        frameKey = args[ARG_MONKEY_FRAME];

                    }

                    if(args.length > ARG_MONKEY_ADDITIONAL
                            && args[ARG_MONKEY_ADDITIONAL].length() > 0) {

                        for (int index = ARG_MONKEY_ADDITIONAL; index < args.length; index++) {

                            textOverride += " " + args[index];

                        }

                        textOverride = textOverride.trim();

                    }

                }
                else if (mapType == MonKeyType.QRCode) {

                    if(args.length > ARG_QRCODE_ADDITIONAL) {

                        for (int index = ARG_QRCODE_ADDITIONAL; index < args.length; index++) {

                            textOverride += " " + args[index];

                        }

                        textOverride = textOverride.trim();

                    }
                    else {

                        textOverride = player.getName();

                    }

                }

                SendMessage(player, "Your MonKeyMap request is being processed...", ChatColor.GOLD);

                new MonKeyMapRunnable(player, this.db, this.configEngine, this.econ, mapType, walletAddress, frameKey, textOverride)
                        .runTaskAsynchronously(this.plugin);

                return true;

            }
            else if (args[ARG_COMMAND].equalsIgnoreCase("query")) {

                int mapCount = this.db.getUnfinishedMapCount(player);
                int readyCount = this.db.getReadyForCollectionMapCount(player);

                if (mapCount > 0) {

                    SendMessage(player, "You have " + mapCount + " pending or processing maps.", ChatColor.GOLD);

                    if(readyCount > 0) {
                        SendMessage(player, "There are " + mapCount + " ready for collection.", ChatColor.GOLD);
                    }

                }
                else {

                    SendMessage(player, "You have no pending or processing maps.", ChatColor.GOLD);

                }

            }
            else if(args[ARG_COMMAND].equalsIgnoreCase("retry")) {

                List<MonkeyMap> maps = db.getFailedMaps(player);

                if (maps.size() > 0) {

                    for (MonkeyMap monkeyMap : maps) {

                        new MonKeyMapRunnable(player, this.db, this.configEngine, this.econ, monkeyMap)
                                .runTaskAsynchronously(this.plugin);

                    }

                    SendMessage(player, "There were " + maps.size() + " that were resubmitted for processing. Separate notifications will be issued when these have been processed.", ChatColor.GOLD);

                } else {

                    SendMessage(player, "There are no maps awaiting processing.", ChatColor.GOLD);

                }

                return true;

            }
            else if (args[ARG_COMMAND].equalsIgnoreCase("collect")) {

                /// Get the available maps pending collection for the player.
                List<MonkeyMap> maps = db.getMapsForCollection(player);

                if (maps.size() > 0) {

                    for (MonkeyMap monkeyMap : maps) {

                        try {

                            MapView mapView = Bukkit.createMap(player.getWorld());

                            for (MapRenderer renderer : mapView.getRenderers()) {

                                mapView.removeRenderer(renderer);

                            }

                            mapView.addRenderer(new monKeyRenderer(this.db, this.configEngine));

                            ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
                            MapMeta meta = (MapMeta) map.getItemMeta();

                            meta.setMapView(mapView);

                            if (monkeyMap.hasAdditionalText()) {

                                meta.setDisplayName(monkeyMap.getAdditionalText());

                            } else if (monkeyMap.getMapType() == MonKeyType.QRCode) {

                                meta.setDisplayName(player.getName() + "'s QR");

                            } else {

                                meta.setDisplayName(player.getName() + "'s MonKey");

                            }

                            map.setItemMeta(meta);

                            HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(map);

                            if (!failedItems.isEmpty()) {

                                for (ItemStack item : failedItems.values()) {

                                    player.getWorld().dropItem(player.getLocation(), item);

                                }


                            }

                            monkeyMap.setMapId(mapView.getId());
                            monkeyMap.setStatus(MonKeyMapStatus.Complete);

                            db.save(monkeyMap);

                        }
                        catch (Exception ex) {

                            ex.printStackTrace();

                        }

                    }

                    if (maps.size() == 1) {

                        SendMessage(player, "Your new map has been added to your inventory. Thank you for your purchase!", ChatColor.GOLD);

                    } else {

                        SendMessage(player, "Your new maps have been added to your inventory. Thank you for your purchase!", ChatColor.GOLD);

                    }

                } else {

                    SendMessage(player, "There are no maps awaiting collection.", ChatColor.GOLD);

                }

                return true;

            }

        }

        return false;

    }

}
