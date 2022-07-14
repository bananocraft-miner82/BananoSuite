package miner82.bananosuite.configuration;

import miner82.bananosuite.Main;
import miner82.bananosuite.classes.DonationPrize;
import miner82.bananosuite.classes.MonKeyType;
import miner82.bananosuite.classes.PrizeClassification;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class ConfigEngine {

    private Main main;

    private List<PrizeClassification> prizeClassifications = new ArrayList<PrizeClassification>();
    private HashMap<PrizeClassification,List<DonationPrize>> donationPrizes = new HashMap<>();
    private HashMap<String,String> availableFrames = new HashMap<>();

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
    private double donationFireworksThreshold = 6.9;
    private boolean donationRandomGift = true;

    private String mongoURI = "";
    private String monKeyImageSourceURL = "https://monkey.banano.cc/api/v1/monkey/{address}?format=png&size=128&background=true";

    private String imageDirectory = "images";
    private String qrDirectory = "maps";
    private String frameDirectory = "frames";

    private String applyFrame = "";
    private boolean allowCloning = false;
    private double monKeyPrice = 100;
    private double qrPrice = 250;

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

    public double getDonationFireworksThreshold() {
        return this.donationFireworksThreshold;
    }

    public void setDonationFireworksThreshold(double value) {

        if(value < 0) {
            value = 0;
        }

        this.donationFireworksThreshold = value;
    }

    public boolean enableDonationRandomGift() {
        return this.donationRandomGift;
    }

    public void setEnableDonationRandomGift(boolean donationRandomGift) {
        this.donationRandomGift = donationRandomGift;
    }

    public String getImageDirectory() {
        return this.imageDirectory;
    }

    public String getMonKeyFrameDirectoryPath() {
        String path = main.getDataFolder().getAbsolutePath();

        if(!path.endsWith(File.separator)) {
            path += File.separator;
        }

        path += this.frameDirectory + File.separator;

        return path;
    }

    public String getAbsoluteMonKeyDirectoryPath() {
        String path = main.getDataFolder().getAbsolutePath();

        if(!path.endsWith(File.separator)) {
            path += File.separator;
        }

        path += this.imageDirectory + File.separator;

        return path;
    }

    public String getAbsoluteDataDirectoryPath() {
        String path = main.getDataFolder().getAbsolutePath();

        if(!path.endsWith(File.separator)) {
            path += File.separator;
        }

        return path;
    }

    public String getQRDirectory() {
        return this.qrDirectory;
    }

    public String getAbsoluteQRDirectoryPath() {
        String path = main.getDataFolder().getAbsolutePath();

        if(!path.endsWith(File.separator)) {
            path += File.separator;
        }

        path += this.qrDirectory + File.separator;

        return path;
    }

    public String getAbsoluteDataDirectoryPath(MonKeyType type) {
        if(type == MonKeyType.MonKey) {
            return this.getAbsoluteMonKeyDirectoryPath();
        }
        else {
            return this.getAbsoluteQRDirectoryPath();
        }
    }

    public String getApplyFrame() {
        return this.applyFrame;
    }

    public HashMap<String, String> getAvailableFrames() { return this.availableFrames; }

    public void setApplyFrame(String newValue) {
        this.applyFrame = newValue;
    }

    public boolean getAllowCloning() {
        return this.allowCloning;
    }

    public void setAllowCloning(boolean newValue) {
        this.allowCloning = newValue;
    }

    public double getMapPrice(MonKeyType mapType) {
        if(mapType == MonKeyType.MonKey) {
            return getMonKeyPrice();
        }
        else {
            return getQrPrice();
        }
    }

    public double getMonKeyPrice() {
        return monKeyPrice;
    }

    public void setMonKeyPrice(double monKeyPrice) {
        this.monKeyPrice = monKeyPrice;
    }

    public double getQrPrice() {
        return qrPrice;
    }

    public void setQrPrice(double qrPrice) {
        this.qrPrice = qrPrice;
    }

    public String getMonKeyImageSourceURL() {
        return monKeyImageSourceURL;
    }

    public String getMonKeyImageSourceURL(String banAddress) {
        return monKeyImageSourceURL.replace("{address}", banAddress);
    }

    public void setMonKeyImageSourceURL(String monKeyImageSourceURL) {
        this.monKeyImageSourceURL = monKeyImageSourceURL;
    }

    private void loadFrames() {

        FileConfiguration configuration = main.getConfig();

        System.out.println("Loading Available MonKey Frames...");

        availableFrames.clear();

        if (configuration.contains("AvailableFrames")) {

            System.out.println("- AvailableFrames section found...");

            ConfigurationSection frames = configuration.getConfigurationSection("AvailableFrames");

            if (frames != null) {

                System.out.println("- Attempting to load frames...");

                for (String key : frames.getKeys(false)) {

                    System.out.println("- Frame Key identified: " + key);

                    if (frames.isConfigurationSection(key)) {

                        try {

                            ConfigurationSection section = frames.getConfigurationSection(key);

                            String imageFileName = section.getString("Image");

                            availableFrames.put(key, imageFileName);

                            System.out.println("Loaded frame: [" + key + "] Image: " + imageFileName);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

            } else {
                System.out.println("- Failed loading frames!");
            }

        } else {
            System.out.println("- No frames configured.");
        }

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
        config.set("DonationGiftFireworksThreshold", this.donationFireworksThreshold);
        config.set("DonationRandomGift", this.donationRandomGift);
        config.set("TeleportBasePremium", this.teleportBaseCost);
        config.set("TeleportMaximumPremium", this.teleportMaximumCost);
        config.set("TeleportGrowthRate", this.teleportGrowthRate);
        config.set("mongoURI", this.imageDirectory);
        config.set("monKeyImageSourceURL", this.monKeyImageSourceURL);

        config.set("ApplyFrame", this.applyFrame);
        config.set("AllowCloning", this.allowCloning);
        config.set("MonKeyPrice", this.monKeyPrice);
        config.set("QRPrice", this.qrPrice);


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

            donationFireworksThreshold = configuration.getDouble("DonationGiftFireworksThreshold");
            System.out.println("- Fireworks reward threshold: " + this.donationFireworksThreshold);

            donationRandomGift = configuration.getBoolean("DonationRandomGift");
            System.out.println("- Random Gift reward on donation: " + (donationRandomGift ? "Active" : "Deactivated"));

            this.monKeyImageSourceURL = configuration.getString("monKeyImageSourceURL");
            System.out.println("MonKey Image Source URL: " + this.monKeyImageSourceURL);

            this.imageDirectory = configuration.getString("ImageDirectory");
            System.out.println("Registered Image Directory: " + this.imageDirectory);

            this.qrDirectory = configuration.getString("QRDirectory");
            System.out.println("Registered QR Directory: " + this.qrDirectory);

            this.applyFrame = configuration.getString("ApplyFrame");
            System.out.println("Frame Enabled: " + this.applyFrame);

            this.allowCloning = configuration.getBoolean("AllowCloning");
            System.out.println("Map Cloning Enabled: " + this.allowCloning);

            this.monKeyPrice = configuration.getDouble("MonKeyPrice");
            System.out.println("MonKey Map Price: " + this.monKeyPrice);

            this.qrPrice = configuration.getDouble("QRPrice");
            System.out.println("QR Map Price: " + this.qrPrice);

            loadDonationPrizes();
            loadFrames();

        }
        else {

            System.out.println("Cannot find configuration file!");

        }

    }

}
