package miner82.bananosuite.runnables;

import miner82.bananosuite.classes.MonKeyMapStatus;
import miner82.bananosuite.classes.MonKeyType;
import miner82.bananosuite.classes.MonkeyMap;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;
import miner82.bananosuite.io.FileManager;
import miner82.bananosuite.io.MonKeyGenerator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MonKeyMapRunnable extends BukkitRunnable {

    private final Player purchaser;
    private final IDBConnection db;
    private final ConfigEngine configEngine;
    private final Economy econ;
    private MonkeyMap map;

    private MonKeyMapRunnable(Player purchaser, IDBConnection db, ConfigEngine configEngine, Economy econ) {

        this.purchaser = purchaser;
        this.db = db;
        this.configEngine = configEngine;
        this.econ = econ;

    }

    public MonKeyMapRunnable(Player purchaser, IDBConnection db, ConfigEngine configEngine, Economy econ,
                                MonkeyMap map) {

        this(purchaser, db, configEngine, econ);

        this.map = map;

    }

    public MonKeyMapRunnable(Player purchaser, IDBConnection db, ConfigEngine configEngine, Economy econ,
                                MonKeyType mapType, String address, String frameKey,
                                String textOverride) {

        this(purchaser, db, configEngine, econ);

        this.map = new MonkeyMap(UUID.randomUUID(), purchaser.getUniqueId(), mapType, address, frameKey, textOverride);
        this.db.createMapRecord(map);

    }

    @Override
    public void run() {

        if(this.map == null
             || this.map.getStatus() == MonKeyMapStatus.Complete) {

            return;

        }

        // Process the payment
        double requiredPayment = this.configEngine.getMapPrice(map.getMapType());

        System.out.println("MonKeyMap payment due: " + requiredPayment);

        if (purchaser.isOp()
             || map.getStatus() == MonKeyMapStatus.AwaitingCollection) {

            requiredPayment = 0;

            System.out.println("MonKeyMap payment due adjusted to zero.");

        }

        if(requiredPayment > 0
                && !econ.has(purchaser, requiredPayment)) {

            if(purchaser.isOnline()) {

                purchaser.sendMessage(ChatColor.RED + "You do not have sufficient funds for this purchase! You need " + econ.format(requiredPayment) + " and you have " + econ.format(econ.getBalance(purchaser)) + ".");

            }

            return;

        }

        if(map.getStatus() == MonKeyMapStatus.ReadyForGeneration) {

            System.out.println("Requested: " + map.getMapType().toString() + " [" + map.getWalletAddress() + "]");

            // Download the image first - let's make sure we can get it
            String destinationDirectory = this.configEngine.getAbsoluteDataDirectoryPath(map.getMapType());
            String fileName = FileManager.getUniqueFilename(destinationDirectory, "png");
            String fullDestinationName = destinationDirectory + fileName;

            System.out.println("Destination Directory: " + destinationDirectory);
            System.out.println("Filename: " + fileName);
            System.out.println("Full Destination: " + fullDestinationName);

            map.setImageFileName(fileName);
            map.setFullFilePath(fullDestinationName);

            if (!MonKeyGenerator.generateMonKeyImage(this.configEngine, map)) {

                if(purchaser.isOnline()) {

                    purchaser.sendMessage(ChatColor.RED + "An error occurred generating the image! You have not been charged for this attempt.");

                }

                return;

            }

            map.setStatus(MonKeyMapStatus.AwaitingPayment);

            db.save(map);

        }

        // PROCESS THE PAYMENT
        if(map.getStatus() == MonKeyMapStatus.AwaitingPayment) {

            if(requiredPayment > 0) {

                try {

                    EconomyResponse response = econ.withdrawPlayer(purchaser, requiredPayment);

                    if (response != null
                            && response.transactionSuccess()) {

                        map.setStatus(MonKeyMapStatus.AwaitingCollection);
                        this.db.save(map);

                        if (purchaser.isOnline()) {

                            TextComponent message = new TextComponent(ChatColor.GREEN + "Your payment was successful and your map is ready for collection. Please use the '/monkeymap collect' command or click on this message to obtain it.");
                            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bananosuite:monkeymap collect"));

                            purchaser.spigot().sendMessage(message);

                        }

                    } else if (purchaser.isOnline()) {

                        purchaser.sendMessage(ChatColor.RED + "Payment failed! " + response.errorMessage);

                    }

                } catch (Exception ex) {

                    if (purchaser.isOnline()) {

                        purchaser.sendMessage(ChatColor.RED + "An error occurred while attempting to process your payment! " + ex.getMessage());

                    }

                    ex.printStackTrace();

                }

            }
            else {

                map.setStatus(MonKeyMapStatus.AwaitingCollection);
                db.save(map);

                if (purchaser.isOnline()) {

                    TextComponent message = new TextComponent(ChatColor.GREEN + "Your map is ready for collection. Please use the '/monkeymap collect' command or click on this message to obtain it.");
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bananosuite:monkeymap collect"));

                    purchaser.spigot().sendMessage(message);

                }

            }

        }

    }

}
