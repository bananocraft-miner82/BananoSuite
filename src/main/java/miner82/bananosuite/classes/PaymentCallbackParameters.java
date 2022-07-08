package miner82.bananosuite.classes;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PaymentCallbackParameters {

    private Player sender;
    private OfflinePlayer recipient;
    private double amount;
    private HashMap<String,Object> parameterValues = new HashMap<>();
    private EconomyResponse economyResponse;

    public PaymentCallbackParameters(Player sender, OfflinePlayer recipient, double amount,
                                     EconomyResponse response, HashMap<String,Object> parameterValues) {

        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.economyResponse = response;
        this.parameterValues = parameterValues;

    }

    public PaymentCallbackParameters(Player sender, double amount,
                                     EconomyResponse response, HashMap<String,Object> parameterValues) {

        this(sender, null, amount, response, parameterValues);

    }

    public Player getSender() {
        return sender;
    }

    public OfflinePlayer getRecipient() {
        return recipient;
    }

    public double getAmount() {
        return amount;
    }

    public HashMap<String, Object> getParameterValues() {
        return parameterValues;
    }

    public EconomyResponse getEconomyResponse() {
        return economyResponse;
    }

    public boolean getTransactionWasSuccessful() {
        if(this.economyResponse != null) {
            try {
                return this.economyResponse.transactionSuccess();
            }
            catch (Exception e) {

            }
        }

        return false;
    }

    public String getTransactionError() {
        if(this.economyResponse != null
             && !this.economyResponse.transactionSuccess()) {
            try {
                return this.economyResponse.errorMessage;
            }
            catch (Exception e) {

            }
        }

        return "";
    }

}
