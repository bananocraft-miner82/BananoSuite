package miner82.bananosuite.commands;

import miner82.bananosuite.BananoSuitePlugin;
import miner82.bananosuite.classes.DistanceCalculator;
import miner82.bananosuite.classes.EssentialsAfkChecker;
import miner82.bananosuite.classes.PaymentCallbackParameters;
import miner82.bananosuite.runnables.PaymentProcessor;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.classes.Math;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;

public class RainCommand extends BaseCommand implements CommandExecutor {

    private final String KEY_RECIPIENTS = "Recipients";

    private final Economy econ;
    private final ConfigEngine configEngine;

    public RainCommand(ConfigEngine configEngine, Economy econ) {
        this.configEngine = configEngine;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {

            System.out.println("The Rain command can only be used in-game!");

        }

        final Player player = (Player) sender;

        if(!this.configEngine.getIsEnabled()
                || !this.configEngine.getRainEnabled()) {

            SendMessage(player, "That command is not enabled on this server.", ChatColor.GOLD);

            return false;

        }

        if(args.length == 0) {

            SendMessage(player, "Incorrect arguments. Expected 1, received " + args.length, ChatColor.RED);

            return false;

        }

        // Process the rain!
        try {

            final double rainAmount = Double.parseDouble(args[0]);
            Object[] onlinePlayers;

            if(args.length > 1
                 && args[1].equalsIgnoreCase("nearby")) {

                final int distance = tryParseInt(args[2], 64);

                onlinePlayers = Bukkit.getOnlinePlayers().stream()
                                                         .filter(x -> !x.equals(player)
                                                                        && !EssentialsAfkChecker.checkPlayerEssentialsIsAfk(player)
                                                                        && x.getWorld().getEnvironment() == player.getWorld().getEnvironment()
                                                                        && DistanceCalculator.calculateDistance(player.getLocation(), x.getLocation()) < (double)distance)
                                                         .toArray();

            }
            else {

                onlinePlayers = Bukkit.getOnlinePlayers().stream()
                                                         .filter(x -> !x.equals(player)
                                                                        && !EssentialsAfkChecker.checkPlayerEssentialsIsAfk(player)).toArray();

            }


            int playersOnline = onlinePlayers.length;
            boolean testmode = false;

            if(args.length > 1
                 && (player == null
                     || player.isOp())) {

                testmode = args[1].equalsIgnoreCase("testmode");

                if(testmode) {

                    playersOnline = 1;
                    onlinePlayers = new Object[] { player };

                }

            }

            if(!testmode
                 && playersOnline <= 0) {

                SendMessage( player, "There must be other players online to use this command!", ChatColor.RED);

                return false;

            }
            else if(rainAmount <= 0) {

                SendMessage( player, "The rain amount must be greater than zero!", ChatColor.RED);

                return false;

            }
            else if(!testmode
                     && (rainAmount / (double)playersOnline) < this.configEngine.getMinimumRainPerPlayer()) {

                SendMessage( player, "The rain amount must be greater than " + econ.format(this.configEngine.getMinimumRainPerPlayer()) + " per player! There are currently " + playersOnline + " other players online.", ChatColor.RED);

                return false;

            }

            if(!testmode) {

                double rainPerPlayer = Math.Round((rainAmount / (double) playersOnline), 2);
                final double totalRain = rainPerPlayer * playersOnline;
                HashMap<String, Object> params = new HashMap<String, Object>();

                params.put(KEY_RECIPIENTS, onlinePlayers);

                Bukkit.broadcastMessage(ChatColor.GOLD + "Hmm, it's beginning to look like rain...");

                new PaymentProcessor(econ, player, totalRain, params, this::RainCallback)
                        .runTaskAsynchronously(BananoSuitePlugin.getPlugin(BananoSuitePlugin.class));

            }
            else {

                SendMessage(player, "Test mode activated: weather triggering...", ChatColor.GREEN);

            }

            TriggerWeather();

        }
        catch (NumberFormatException ex) {

            SendMessage( player, "A properly formatted decimal value must be provided!", ChatColor.RED);

            return false;

        }

        return true;

    }

    private void TriggerWeather() {
        final int duration = (50 + new Random().nextInt(10)) * 20;

        for(World world : Bukkit.getWorlds()) {

            if (world.getEnvironment().equals(World.Environment.NORMAL)) {

                world.setStorm(true);
                world.setWeatherDuration(duration);

            }

            world.setThundering(true);
            world.setThunderDuration(duration);

        }
    }

    private void RainCallback(PaymentCallbackParameters parameters) {

        Player player = parameters.getSender();
        double totalRain = parameters.getAmount();
        Object[] recipients = null;

        if(parameters.getParameterValues().containsKey(KEY_RECIPIENTS)
             && parameters.getParameterValues().get(KEY_RECIPIENTS) instanceof Object[]) {
            recipients = (Object[])parameters.getParameterValues().get(KEY_RECIPIENTS);
        }

        if(recipients == null
            || recipients.length == 0) {

            SendMessage(player, "The recipients list could not be generated!", ChatColor.RED);
            return;

        }

        final double rainPerPlayer = totalRain / recipients.length;

        if(parameters.getTransactionWasSuccessful()) {

            SendMessage( player, "Thank you for your rain of " + econ.format(totalRain) + ".", ChatColor.GREEN);
            SendMessage( player, "This will be shared out as " + econ.format(rainPerPlayer) + " for each online player!", ChatColor.GREEN);

            for (Object recipient : recipients) {

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        try {

                            if(recipient instanceof Player) {

                                Player recipientPlayer = (Player)recipient;

                                if(!recipientPlayer.getUniqueId().equals(player.getUniqueId())) {

                                    EconomyResponse rainDeposit = econ.depositPlayer(recipientPlayer, rainPerPlayer);

                                    if (rainDeposit.transactionSuccess()) {

                                        SendMessage(recipientPlayer, "You have received " + econ.format(rainPerPlayer) + " as part of a rain from " + player.getDisplayName() + "!", ChatColor.GOLD);
                                        SendMessage(player, recipientPlayer.getDisplayName() + " has been sent " + econ.format(rainPerPlayer) + " from your rain!", ChatColor.GOLD);

                                    }

                                }

                            }

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }.runTaskAsynchronously(BananoSuitePlugin.getPlugin(BananoSuitePlugin.class));


            }

        }
        else {

            SendMessage( player, "We appreciate the gesture, but you don't have enough " + econ.currencyNamePlural() + " to do that.", ChatColor.RED);

        }
    }

    private int tryParseInt(String value, int defaultVal) {

        try {

            return Integer.parseInt(value);

        } catch (NumberFormatException e) {

            return defaultVal;

        }

    }

}
