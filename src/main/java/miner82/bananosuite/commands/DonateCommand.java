package miner82.bananosuite.commands;

import miner82.bananosuite.DB;
import miner82.bananosuite.Main;
import miner82.bananosuite.classes.*;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.runnables.PaymentProcessor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DonateCommand extends BaseCommand implements CommandExecutor {

    private Economy econ;
    private ConfigEngine configEngine;

    public DonateCommand(ConfigEngine configEngine, Economy econ) {
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(args.length != 1) {

            SendMessage(player, "Incorrect arguments. Expected 1, received " + args.length, ChatColor.RED);

            return false;

        }

        // Process the donation
        try {
            double donation = Double.parseDouble(args[0]);

            if(donation <= 0) {

                SendMessage( player, "The donation amount must be greater than zero!", ChatColor.RED);

                return false;

            }

            new PaymentProcessor(econ,
                                 player,
                                 donation,
                                 new HashMap<String,Object>(),
                                 this::DonationCallback)
                  .runTaskAsynchronously(Main.getPlugin(Main.class));

        }
        catch (NumberFormatException ex) {
            SendMessage( player, "A properly formatted decimal value must be provided!", ChatColor.RED);

            return false;
        }

        return true;

    }

    private void DonationCallback(PaymentCallbackParameters parameters) {

        Player donater = parameters.getSender();
        double donation = parameters.getAmount();

        if(parameters.getTransactionWasSuccessful()) {

            DB.recordPlayerDonation(donater, donation);

            Random random = new Random();
            int randomQuantity = random.nextInt(15) + 1;

            boolean giftedFireworks = false;
            boolean randomGift = false;

            if(donation >= this.configEngine.getDonationFireworksThreshold()) {

                try {

                    ItemStack star = new ItemStack(Material.FIREWORK_ROCKET, randomQuantity);

                    FireworkEffect effect = FireworkEffect.builder().withColor(Color.YELLOW)
                            .withTrail()
                            .withFlicker()
                            .withFade(Color.SILVER)
                            .build();

                    FireworkEffect effect2 = FireworkEffect.builder().withColor(Color.ORANGE)
                            .withTrail()
                            .withFlicker()
                            .withFade(Color.ORANGE)
                            .build();

                    FireworkMeta metaData = (FireworkMeta) star.getItemMeta();
                    // Add effects
                    metaData.addEffect(effect);
                    metaData.addEffect(effect2);
                    metaData.setPower(2);
                    star.setItemMeta(metaData);

                    donater.getInventory().addItem(star);

                    giftedFireworks = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            // Random prize for donating
            // Pick a random prize classification from the available options
            try {
                List<PrizeClassification> availablePrizeClassifications = this.configEngine.getPrizeClassifications().stream().filter(x -> x.getDonationThreshold() <= donation).toList();

                if (availablePrizeClassifications != null
                        && !availablePrizeClassifications.isEmpty()) {

                    List<DonationPrize> potentialPrizes = new ArrayList<DonationPrize>();

                    // Get a list of all potential prizes
                    for (PrizeClassification prizeClassification : availablePrizeClassifications) {

                        potentialPrizes.addAll(this.configEngine.getDonationPrizes().get(prizeClassification));

                    }

                    if (potentialPrizes.size() > 0) {

                        int prizeIndex = miner82.bananosuite.classes.Math.RandomBetween(0, potentialPrizes.size());

                        DonationPrize prize = potentialPrizes.get(prizeIndex);

                        if (prize != null) {

                            int quantity = miner82.bananosuite.classes.Math.RandomBetween(prize.getMinimumQuantity(), prize.getMaximumQuantity());

                            ItemStack donationPrize = new ItemStack(prize.getMaterial(), quantity);
                            donater.getInventory().addItem(donationPrize);

                            randomGift = true;

                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SendMessage(donater, "Thank you for your donation of " + econ.format(donation) + "!", ChatColor.GREEN);

            if(giftedFireworks && randomGift) {

                SendMessage(donater, "You've been sent some fireworks and a surprise gift as a thank you!", ChatColor.GREEN);

            }
            else if(giftedFireworks) {

                SendMessage(donater, "You've been sent some fireworks as a thank you!", ChatColor.GREEN);

            }
            else if(randomGift) {

                SendMessage(donater, "You've been sent a surprise gift as a thank you!", ChatColor.GREEN);

            }

        }
        else {

            SendMessage(donater, "Your donation of " + econ.format(donation) + " was unsuccessful! " + parameters.getTransactionError(), ChatColor.RED);

        }

    }

}
