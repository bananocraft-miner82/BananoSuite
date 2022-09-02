package miner82.bananosuite.commands;

import miner82.bananosuite.DB;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.io.FileManager;
import miner82.bananosuite.io.ImageManager;
import miner82.bananosuite.classes.MonKeyType;
import miner82.bananosuite.renderers.monKeyRenderer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;


public class MonKeyMapCommand extends BaseCommand implements CommandExecutor {

    private final String WALLET_ADDRESS = "WalletAddress";
    private final String MAP_TYPE = "MapType";
    private final String INCLUDE_FRAME = "IncludeFrame";

    private ConfigEngine configEngine;
    private Economy econ;

    public MonKeyMapCommand(ConfigEngine configEngine, Economy econ) {
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if(!this.configEngine.getIsEnabled()
                    || !this.configEngine.getMonkeyMapsEnabled()) {

                SendMessage(player, "That command is not enabled on this server.", ChatColor.GOLD);

                return false;

            }

            if(args.length > 0
                 && args[0].equalsIgnoreCase("help")) {

                SendMessage(player, "MonKeyMaps come in two varieties: QR Codes and MonKeys.", ChatColor.GOLD);
                SendMessage(player, "QR Codes are generated using the address you provide, with any text after replacing your name. These cost " + econ.format(this.configEngine.getMapPrice(MonKeyType.QRCode)) + ".", ChatColor.GOLD);
                SendMessage(player, "MonKeys are the monkey images generated using the address you provide, with any text after replacing your name. These cost " + econ.format(this.configEngine.getMapPrice(MonKeyType.MonKey)) + ".", ChatColor.GOLD);

                return true;

            }

            System.out.println("Player has permission. Commencing image retrieval...");

            if (args.length >= 2
                    && (args[0].equalsIgnoreCase("monkey") || args[0].equalsIgnoreCase("qrcode"))
                    && args[1].startsWith("ban_")) {

                MonKeyType mapType = args[0].equalsIgnoreCase("qrcode") ? MonKeyType.QRCode : MonKeyType.MonKey;
                String address = args[1].toLowerCase(Locale.ROOT).trim();
                String frameKey = "";

                if(mapType == MonKeyType.MonKey
                        && args.length >= 3
                        && !args[2].equalsIgnoreCase("none")) {

                    frameKey = args[2];

                }

                // Process the payment
                double requiredPayment = this.configEngine.getMapPrice(mapType);

                if(player.isOp()) {
                    requiredPayment = 0;
                }

                if(requiredPayment > 0
                      && !econ.has(player, requiredPayment)) {

                    player.sendMessage(ChatColor.RED + "You do not have sufficient funds for this purchase! You need " + econ.format(requiredPayment) + " and you have " + econ.format(econ.getBalance(player)) + ".");
                    return false;

                }

                System.out.println("Requested: " + mapType.toString() + " [" + address + "]");

                // override it for now:
                //address = "ban_1bananoo34ofbo8famhsj9e88um3j5xsa5f9sx457bxc8m1awnw4mmes8s1e";

                // Download the image first - let's make sure we can get it
                String monKeyURL = this.configEngine.getMonKeyImageSourceURL(address);

                System.out.println("MonkeyURL: " + monKeyURL);

                String destinationDirectory = this.configEngine.getAbsoluteDataDirectoryPath(mapType);

                System.out.println("Destination Directory: " + destinationDirectory);

                String fileName = FileManager.getUniqueFilename(destinationDirectory, "png");
                System.out.println("Filename: " + fileName);

                String fullDestinationName = destinationDirectory + fileName;
                System.out.println("Full Destination: " + fullDestinationName);

                try {
                    File outputFile = new File(fullDestinationName);
                    BufferedImage image;

                    if(mapType == MonKeyType.MonKey) {

                        System.out.println("Attempting MonKey download...");

                        FileManager.downloadFile(monKeyURL, fullDestinationName);

                        image = ImageManager.resizeImage(ImageIO.read(outputFile), 128, 128);

                        if(this.configEngine.getAvailableFrames().containsKey(frameKey)) {

                            try {
                                String frameImageFileName =  this.configEngine.getAvailableFrames().getOrDefault(frameKey, "");

                                if(frameImageFileName != "") {

                                    File frameFile = new File(this.configEngine.getMonKeyFrameDirectoryPath() + frameImageFileName);
                                    BufferedImage frame = ImageIO.read(frameFile);

                                    Graphics g = image.getGraphics();
                                    g.drawImage(frame, 0, 0, null);

                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                    }
                    else {

                        image = ImageManager.generateQRCodeImage(address);

                        Graphics2D gO = image.createGraphics();
                        gO.setColor(Color.BLUE);
                        gO.setFont(new Font( "SansSerif", Font.BOLD, 12 ));

                        if(args.length >= 3) {

                            String text = "";

                            for(int index = 2; index < args.length; index++) {

                                text += " " + args[index];

                            }

                            if(text.trim().length() == 0) {
                                text = player.getName();
                            }

                            gO.drawString(text.trim(), 5, 126);

                        }
                        else {
                            gO.drawString(player.getName(), 5, 126);
                        }
                    }

                    ImageIO.write(image, "png", outputFile);
                }
                catch (IOException ex) {
                    System.out.println("Error downloading monKey: " + address + System.lineSeparator() + ex.toString());
                    player.sendMessage(ChatColor.RED + "An error occurred retrieving your monKey! You have not been charged for this attempt.");
                    return false;
                } catch (Exception ex) {
                    System.out.println("Error generating QR Code: " + address + System.lineSeparator() + ex.toString());
                    player.sendMessage(ChatColor.RED + "An error occurred generating the image! You have not been charged for this attempt.");
                    return false;
                }

                // PROCESS THE PAYMENT
                boolean paymentSuccessful = false;

                if(requiredPayment > 0) {

                    try {
                        EconomyResponse response = econ.withdrawPlayer(player, requiredPayment);

                        if(response != null
                             && response.transactionSuccess()) {

                            paymentSuccessful = true;

                        }
                        else {

                            paymentSuccessful = false;
                            player.sendMessage(ChatColor.RED + "Payment failed! " + response.errorMessage);

                        }

                    }
                    catch (Exception ex) {

                        player.sendMessage(ChatColor.RED + "An error occurred while attempting to process your payment! " + ex.getMessage());

                    }

                }
                else {

                    paymentSuccessful = true;

                }

                if (paymentSuccessful) {

                    System.out.println("Generating monKey: Provided address: " + address);
                    //MapView mapView = Bukkit.createMap(player.getWorld());

                    MapView mapView = Bukkit.createMap(player.getWorld());

                    for(MapRenderer renderer : mapView.getRenderers()) {
                        mapView.removeRenderer(renderer);
                    }

                    mapView.addRenderer(new monKeyRenderer(this.configEngine));

                    DB.createMapRecord(mapView, player, mapType, fileName);

                    ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
                    MapMeta meta = (MapMeta) map.getItemMeta();

                    meta.setMapView(mapView);

                    if(mapType == MonKeyType.QRCode) {
                        meta.setDisplayName(player.getName() + "'s QR");
                    }
                    else {
                        meta.setDisplayName(player.getName() + "'s MonKey");
                    }
                    map.setItemMeta(meta);

                    player.getInventory().addItem(map);

                    player.sendMessage(ChatColor.GOLD + "Your new map has been added to your inventory. Thank you for your purchase!");
                }
                else {
                    // Delete the downloaded image
                    File file = new File(fullDestinationName);

                    try {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    catch (SecurityException ex) {

                    }
                }


                return true;

            } else {

                player.sendMessage(ChatColor.DARK_RED + "A Banano address (starting with ban_) must be provided!");

            }
        }

        return false;
    }

}
