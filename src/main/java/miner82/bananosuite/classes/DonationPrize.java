package miner82.bananosuite.classes;

import org.bukkit.Material;

public class DonationPrize {

    private Material material;
    private PrizeClassification classification;
    private double weighting;
    private int minimumQuantity;
    private int maximumQuantity;

    public DonationPrize(Material material, PrizeClassification prizeClassification, double weighting, int minimumQuantity, int maximumQuantity) {
        this.material = material;
        this.classification = prizeClassification;
        this.weighting = weighting;
        this.minimumQuantity = minimumQuantity;
        this.maximumQuantity = maximumQuantity;
    }

    public Material getMaterial() {
        return material;
    }

    public PrizeClassification getClassification() {
        return classification;
    }

    public double getWeighting() {
        return weighting;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public int getMaximumQuantity() {
        return maximumQuantity;
    }

}
