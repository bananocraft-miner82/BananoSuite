package miner82.bananosuite.classes;

public class PrizeClassification {

    private String key;
    private double donationThreshold;

    public PrizeClassification(String key, double donationThreshold) {
        this.key = key;
        this.donationThreshold = donationThreshold;
    }

    public String getKey() {
        return key;
    }

    public double getDonationThreshold() {
        return donationThreshold;
    }

}
