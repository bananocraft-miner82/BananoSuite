package miner82.bananosuite.configuration;

import miner82.bananosuite.Main;
import miner82.bananosuite.classes.DonationPrize;
import miner82.bananosuite.classes.PrizeClassification;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConfigEngine {

    private Main main;

    private List<PrizeClassification> prizeClassifications = new ArrayList<PrizeClassification>();
    private HashMap<PrizeClassification,List<DonationPrize>> donationPrizes = new HashMap<>();

    private boolean enablePlugin = true;

    private boolean restrictHomeToOverworld = true;
    private boolean isSpawnTeleportFree = false;

    private boolean enableDeathInsurance = true;
    private double baseDeathInsurancePremium = 19;
    private double deathInsuranceMaximumCost = 1900;
    private double deathInsuranceGrowthRate = 0.19;

    private double teleportBaseCost = 10;
    private double teleportMaximumCost = 1000;
    private double teleportGrowthRate = 0.1;

    private boolean donationFireworks = true;
    private boolean donationRandomGift = true;

    public ConfigEngine(Main main) {

        this.main = main;

        initialiseConfig(main.getConfig());

    }

    public boolean getIsEnabled() {
        return enablePlugin;
    }

    public void setIsEnabled(boolean enable) {
        this.enablePlugin = enable;
    }

    public boolean getRestrictHomeToOverworld() {
        return this.restrictHomeToOverworld;
    }

    public void setRestrictHomeToOverworld(boolean value) {
        this.restrictHomeToOverworld = value;
    }

    public boolean getIsSpawnTeleportFree() { return this.isSpawnTeleportFree; }

    public void setIsSpawnTeleportFree(boolean value) {
        this.isSpawnTeleportFree = value;
    }

    public boolean getDeathInsuranceEnabled() {
        return this.enableDeathInsurance;
    }

    public void setDeathInsuranceEnabled(boolean enable) {
        this.enableDeathInsurance = enable;
    }

    public double getBaseDeathInsurancePremium()
    {
        return this.baseDeathInsurancePremium;
    }

    public void setBaseDeathInsurancePremium(double basePremium) {
        this.baseDeathInsurancePremium = basePremium;
    }

    public double getDeathInsuranceMaximumCost() {
        return deathInsuranceMaximumCost;
    }

    public void setDeathInsuranceMaximumCost(double deathInsuranceMaximumCost) {
        this.deathInsuranceMaximumCost = deathInsuranceMaximumCost;
    }

    public double getDeathInsuranceGrowthRate() {
        return deathInsuranceGrowthRate;
    }

    public void setDeathInsuranceGrowthRate(double deathInsuranceGrowthRate) {
        this.deathInsuranceGrowthRate = deathInsuranceGrowthRate;
    }

    public double getTeleportBaseCost() {
        return this.teleportBaseCost;
    }

    public void setTeleportBaseCost(double cost) {
        this.teleportBaseCost = cost;
    }

    public double getTeleportMaximumCost() {
        return this.teleportMaximumCost;
    }

    public void setTeleportMaximumCost(double cost) {
        this.teleportMaximumCost = cost;
    }

    public double getTeleportGrowthRate() { return this.teleportGrowthRate; }

    public void setTeleportGrowthRate(double value) { this.teleportGrowthRate = value; }

    public List<PrizeClassification> getPrizeClassifications() {
        return prizeClassifications;
    }

    public HashMap<PrizeClassification, List<DonationPrize>> getDonationPrizes() {
        return donationPrizes;
    }

    public boolean enableDonationFireworks() {
        return this.donationFireworks;
    }

    public void setEnableDonationFireworks(boolean donationFireworks) {
        this.donationFireworks = donationFireworks;
    }

    public boolean enableDonationRandomGift() {
        return this.donationRandomGift;
    }

    public void setEnableDonationRandomGift(boolean donationRandomGift) {
        this.donationRandomGift = donationRandomGift;
    }

    private void loadDonationPrizes() {

        FileConfiguration configuration = main.getConfig();

        System.out.println("Loading Donation Prizes...");

        donationPrizes.clear();

        if (configuration.contains("DonationClassifications")) {

            System.out.println("- Classifications section found...");

            ConfigurationSection classifications = configuration.getConfigurationSection("DonationClassifications");

            if(classifications != null) {

                System.out.println("- Attempting to load classifications...");

                for(String key : classifications.getKeys(false)) {

                    System.out.println("- Classification Key identified: " + key);

                    if(classifications.isConfigurationSection(key)) {

                        try {

                            ConfigurationSection section = classifications.getConfigurationSection(key);

                            double donationThreshold = section.getDouble("Threshold");

                            PrizeClassification prizeClassification = new PrizeClassification(key, donationThreshold);

                            prizeClassifications.add(prizeClassification);
                            donationPrizes.put(prizeClassification, new ArrayList<DonationPrize>());

                            System.out.println("Loading donation classification: [" + key + "] Threshold: " + donationThreshold);

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
            else {
                System.out.println("- Failed loading classifications!");
            }

        }
        else {
            System.out.println("- No classifications configured!");
        }

        if (prizeClassifications.size() > 0
              && configuration.contains("DonationPrizes")) {

            ConfigurationSection section = configuration.getConfigurationSection("DonationPrizes");

            if(section != null) {

                for(String key : section.getKeys(false)) {

                    if(section.isConfigurationSection(key)
                         && Material.getMaterial(key) != null) {

                        try {

                            Material material = Material.getMaterial(key);

                            ConfigurationSection prizeConfig = section.getConfigurationSection(key);

                            String classification = prizeConfig.getString("Classification");
                            double weighting = prizeConfig.getDouble("Weighting");
                            int minimumQuantity = prizeConfig.getInt("Minimum");
                            int maximumQuantity = prizeConfig.getInt("Maximum");

                            Optional<PrizeClassification> prizeClassification = donationPrizes.keySet().stream().filter(x -> x.getKey().equalsIgnoreCase(classification)).findFirst();

                            if(prizeClassification.isPresent()) {

                                PrizeClassification prizeClassificationKey = prizeClassification.get();
                                DonationPrize prize = new DonationPrize(material, prizeClassification.get(), weighting, minimumQuantity, maximumQuantity);

                                donationPrizes.get(prizeClassificationKey).add(prize);

                                System.out.println("Random prize configuration added: [" + prize.getMaterial().toString() + "] Quantity Range: " + minimumQuantity + "->" + maximumQuantity + ".");

                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

            }

        }
    }

    public boolean save() {

        FileConfiguration config = this.main.getConfig();

        config.set("EnableBananoSuite", this.enablePlugin);
        config.set("RestrictHomeToOverworld", this.restrictHomeToOverworld);
        config.set("FreeSpawnTeleport", this.isSpawnTeleportFree);
        config.set("EnableDeathInsurance", this.enableDeathInsurance);
        config.set("DeathInsuranceBasePremium", this.baseDeathInsurancePremium);
        config.set("DeathInsuranceMaximumPremium", this.deathInsuranceMaximumCost);
        config.set("DeathInsuranceGrowthRate", this.deathInsuranceGrowthRate);
        config.set("DonationGiftFireworks", this.donationFireworks);
        config.set("DonationRandomGift", this.donationRandomGift);
        config.set("TeleportBasePremium", this.teleportBaseCost);
        config.set("TeleportMaximumPremium", this.teleportMaximumCost);
        config.set("TeleportGrowthRate", this.teleportGrowthRate);
        this.main.saveConfig();

        return true;

    }

    public void reload() {
        initialiseConfig(main.getConfig());
    }

    private void initialiseConfig(FileConfiguration configuration) {

        System.out.println("initialiseConfig");

        if(configuration != null) {

            if(!configuration.getBoolean("EnableBananoSuite")) {

                System.out.println("Plugin is disabled. Exiting config initialisation...");

                return;
            }
            else {

                System.out.println("Loading BananoSuite Configuration...");

            }

            setIsEnabled(true);

            enableDeathInsurance = configuration.getBoolean("EnableDeathInsurance");
            System.out.println("- Death Insurance: " + (enableDeathInsurance ? "Active" : "Deactivated"));

            restrictHomeToOverworld = configuration.getBoolean("RestrictHomeToOverworld");
            System.out.println("- Home Command Restricted to Overworld: " + (restrictHomeToOverworld ? "Active" : "Deactivated"));

            isSpawnTeleportFree = configuration.getBoolean("FreeSpawnTeleport");
            System.out.println("- Spawn Teleport is free: " + (isSpawnTeleportFree ? "Active" : "Deactivated"));

            teleportBaseCost = configuration.getDouble("TeleportBasePremium");
            System.out.println("- Teleport Base Premium: " + teleportBaseCost);

            teleportMaximumCost = configuration.getDouble("TeleportMaximumPremium");
            System.out.println("- Teleport Maximum Premium: " + teleportMaximumCost);

            teleportGrowthRate = configuration.getDouble("TeleportGrowthRate");
            System.out.println("- Teleport Cost Growth Rate: " + teleportGrowthRate);

            baseDeathInsurancePremium = configuration.getDouble("DeathInsuranceBasePremium");
            System.out.println("- Death Insurance Base Premium: " + baseDeathInsurancePremium);

            deathInsuranceMaximumCost = configuration.getDouble("DeathInsuranceMaximumPremium");
            System.out.println("- Death Insurance Maximum Premium: " + deathInsuranceMaximumCost);

            deathInsuranceGrowthRate = configuration.getDouble("deathInsuranceGrowthRate");
            System.out.println("- Death Insurance Premium Growth Rate: " + deathInsuranceGrowthRate);

            donationFireworks = configuration.getBoolean("DonationGiftFireworks");
            System.out.println("- Fireworks reward on donation: " + (donationFireworks ? "Active" : "Deactivated"));

            donationRandomGift = configuration.getBoolean("DonationRandomGift");
            System.out.println("- Random Gift reward on donation: " + (donationRandomGift ? "Active" : "Deactivated"));

            loadDonationPrizes();

        }
        else {

            System.out.println("Cannot find configuration file!");

        }

    }

}
