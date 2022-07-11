package miner82.bananosuite.runnables;

import miner82.bananosuite.classes.PaymentCallbackParameters;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.util.HashMap;

public class PaymentProcessor extends BukkitRunnable {

    private Economy economy;
    private Player sender;
    private OfflinePlayer recipient; // If null, payment to server
    private double amount;
    private HashMap<String,Object> parameters = new HashMap<>();
    private Consumer<PaymentCallbackParameters> callback;

    public PaymentProcessor(Economy economy, Player sender, double amount, HashMap<String,Object> parameters, Consumer<PaymentCallbackParameters> callback) {
        this(economy, sender, null, amount, parameters, callback);
    }

    public PaymentProcessor(Economy economy, Player sender, OfflinePlayer recipient, double amount, HashMap<String,Object> parameters, Consumer<PaymentCallbackParameters> callback) {

        this.economy = economy;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.parameters = parameters;
        this.callback = callback;

    }

    @Override
    public void run() {

        EconomyResponse r = null;
        boolean success = false;

        if(economy == null
             || sender == null
             || amount < 0) {

            callback.accept(new PaymentCallbackParameters(sender, recipient, amount, new EconomyResponse( amount, 0, EconomyResponse.ResponseType.FAILURE, "Invalid transaction parameters!"), parameters));

            return;

        }

        if(amount > 0) {
            try {

                if (economy.has(sender, amount)) {

                    r = economy.withdrawPlayer(sender, amount);
                    success = r.transactionSuccess();

                } else {

                    callback.accept(new PaymentCallbackParameters(sender, recipient, amount, new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Insufficient balance!"), parameters));

                }

            } catch (Exception e) {
                System.out.println("An error occurred attempting to withdraw " + economy.format(amount) + " from " + sender.getName());
                e.printStackTrace();
            }

            if (success
                    && recipient != null) {

                try {

                    r = economy.depositPlayer(recipient, amount);

                } catch (Exception e) {
                    String recipientName = "SERVER";
                    if (recipient != null) {
                        recipientName = recipient.getName();
                    }

                    System.out.println("An error occurred attempting to deposit " + economy.format(amount) + " to " + recipientName);
                    e.printStackTrace();
                }

            }
        }
        else {

            // Zero value transaction - success.
            r = new EconomyResponse(amount, 0, EconomyResponse.ResponseType.SUCCESS, "");

        }

        if(r == null) {

            r = new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "No response received");

        }

        callback.accept(new PaymentCallbackParameters(sender, recipient, amount, r, parameters));

    }
}
