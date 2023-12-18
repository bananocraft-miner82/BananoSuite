package miner82.bananosuite.configuration;

import miner82.bananosuite.BananoSuitePlugin;
import miner82.bananosuite.classes.*;
import miner82.bananosuite.dbconnectors.DBConnectionType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.lang.Math;
import java.util.*;

public class ConfigEngine {

    private BananoSuitePlugin main;

    private List<PrizeClassification> prizeClassifications = new ArrayList<PrizeClassification>();
    private HashMap<PrizeClassification,List<DonationPrize>> donationPrizes = new HashMap<>();
    private HashMap<String,String> availableFrames = new HashMap<>();

    private boolean enablePlugin = true;
    private boolean showDebug = true;

    private String mongoDbConnectionString = "";
    private DBConnectionType connectionType = DBConnectionType.Json;

    private String mysqlServerName = "";
    private int mysqlPort = 3306;
    private String mysqlDatabaseName = "";
    private String mysqlUsername = "";
    private String mysqlPassword = "";

    private boolean donateCommandEnabled = true;
    private boolean monkeyMapsEnabled = true;
    private boolean pvpToggleEnabled = true;
    private boolean rainEnabled = true;
    private boolean teleportEnabled = true;

    private double minimumRainPerPlayer = 0.1;

    private boolean restrictHomeToOverworld = true;
    private boolean isSpawnTeleportFree = false;

    private boolean enableDeathInsurance = true;
    private double baseDeathInsurancePremium = 19;
    private double deathInsuranceMaximumCost = 1900;
    private double deathInsuranceGrowthRate = 0.19;

    private double teleportBaseCost = 10;
    private double teleportMaximumCost = 1000;
    private double teleportGrowthRate = 0.1;
    private int minimumTeleportDistance = 16;

    private boolean donationFireworks = true;
    private double donationFireworksThreshold = 6.9;
    private boolean donationRandomGift = true;

    private String monKeyImageSourceURL = "https://monkey.banano.cc/api/v1/monkey/{address}?format=png&size=128&background=true";

    private String imageDirectory = "images";
    private String qrDirectory = "maps";
    private String frameDirectory = "frames";

    private String applyFrame = "";
    private boolean allowCloning = false;
    private double monKeyPrice = 100;
    private double qrPrice = 250;

    // Random wild teleport settings
    private final int DEFAULT_MAX_SPAWN_RADIUS = 1000;
    private int maximumTeleportRadius = DEFAULT_MAX_SPAWN_RADIUS;
    private boolean wildTeleportInBoatOnWater = true;
    private org.bukkit.entity.Boat.Type wildTeleportInBoatType = org.bukkit.entity.Boat.Type.OAK;
    private int maxSearchXIfLavaWildTeleport = 10;
    private int maximumWildTeleportUses = 3;
    private double[] wildTeleportCosts = new double[this.maximumWildTeleportUses];

    public ConfigEngine(BananoSuitePlugin main) {

        this.main = main;

        initialiseConfig(main.getConfig());

    }

    public boolean getIsDebugMode() {
        return this.showDebug;
    }

    public void setIsDebugMode(boolean showDebug) {
        this.showDebug = showDebug;
    }

    public DBConnectionType getConnectionType() {
        return this.connectionType;
    }

    public String getMongoDbConnectionString() {
        return this.mongoDbConnectionString;
    }

    public String getMysqlServerName() {
        return this.mysqlServerName;
    }

    public int getMysqlPort() {
        return this.mysqlPort;
    }

    public String getMysqlDatabaseName() {
        return this.mysqlDatabaseName;
    }

    public String getMysqlUsername() {
        return this.mysqlUsername;
    }

    public String getMysqlPassword() {
        return this.mysqlPassword;
    }

    public boolean getIsEnabled() {
        return enablePlugin;
    }

    public void setIsEnabled(boolean enable) {
        this.enablePlugin = enable;
    }

    public double getMinimumRainPerPlayer() {
        return this.minimumRainPerPlayer;
    }

    public void setMinimumRainPerPlayer(double value) {
        if(value > 0) {
            this.minimumRainPerPlayer = value;
        }
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

    public boolean getDonateCommandIsEnabled() { return this.donateCommandEnabled; }

    public void setDonateCommandIsEnabled(boolean enabled) {
        this.donateCommandEnabled = enabled;
    }

    public boolean getMonkeyMapsEnabled() { return this.monkeyMapsEnabled; }

    public void setMonkeyMapsEnabled(boolean enabled) {
        this.monkeyMapsEnabled = enabled;
    }

    public boolean getPvpToggleEnabled() { return this.pvpToggleEnabled; }

    public void setPvpToggleEnabled(boolean enabled) {
        this.pvpToggleEnabled = enabled;
    }

    public boolean getRainEnabled() { return this.rainEnabled; }

    public void setRainEnabled(boolean enabled) {
        this.rainEnabled = enabled;
    }

    public boolean getTeleportEnabled() { return this.teleportEnabled; }

    public void setTeleportEnabled(boolean enabled) {
        this.teleportEnabled = enabled;
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

    public int getMinimumTeleportDistance() {
        return this.minimumTeleportDistance;
    }

    public void setMinimumTeleportDistance(int distance) {
        this.minimumTeleportDistance = distance;
    }

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

    public int getMaximumTeleportRadius() {
        return this.maximumTeleportRadius;
    }

    public void setMaximumTeleportRadius(int newMaximumTeleportRadius) {

        newMaximumTeleportRadius = Math.abs(newMaximumTeleportRadius);

        if(this.maximumTeleportRadius != newMaximumTeleportRadius) {

            this.maximumTeleportRadius = newMaximumTeleportRadius;

            save();

        }

    }

    public boolean isWildTeleportInBoatOnWater() {
        return this.wildTeleportInBoatOnWater;
    }

    public void setWildTeleportInBoatOnWater(boolean giveBoat) {

        if(this.wildTeleportInBoatOnWater != giveBoat) {

            this.wildTeleportInBoatOnWater = giveBoat;

            save();

        }

    }

    public org.bukkit.entity.Boat.Type getWildTeleportInBoatType() {
        return this.wildTeleportInBoatType;
    }

    public void setWildTeleportInBoatType(org.bukkit.entity.Boat.Type boatType) {

        if(this.wildTeleportInBoatType != boatType) {

            this.wildTeleportInBoatType = boatType;

            save();

        }

    }

    public int getMaxSearchXIfLavaWildTeleport() {
        return this.maxSearchXIfLavaWildTeleport;
    }

    public void setMaxSearchXIfLavaWildTeleport(int maxDistance) {

        if(this.maxSearchXIfLavaWildTeleport != maxDistance) {

            this.maxSearchXIfLavaWildTeleport = maxDistance;

            save();

        }

    }

    public int getMaximumWildTeleportUses() {
        return this.maximumWildTeleportUses;
    }

    public void setMaximumWildTeleportUses(int newValue) {

        if(this.maximumWildTeleportUses != newValue) {

            this.maximumWildTeleportUses = newValue;

            save();

        }

    }

    public double getWildTeleportCost(int attempt) {

        if(this.wildTeleportCosts.length > attempt) {

            return this.wildTeleportCosts[attempt];

        }

        return 0;

    }

    public boolean save() {

        FileConfiguration config = this.main.getConfig();

        config.set("EnableBananoSuite", this.enablePlugin);
        config.set("DebugMode", this.showDebug);

        config.set("EnableDonateCommand", this.donateCommandEnabled);
        config.set("EnableMonKeyMaps", this.monkeyMapsEnabled);
        config.set("EnablePvpToggle", this.pvpToggleEnabled);
        config.set("EnableRainCommand", this.rainEnabled);
        config.set("EnableTeleportCommands", this.teleportEnabled);

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
        config.set("MinimumTeleportDistance", this.minimumTeleportDistance);
        config.set("ImageDirectory", this.imageDirectory);
        config.set("monKeyImageSourceURL", this.monKeyImageSourceURL);
        config.set("MinimumRainPerPlayer", this.minimumRainPerPlayer);

        config.set("ApplyFrame", this.applyFrame);
        config.set("AllowCloning", this.allowCloning);
        config.set("MonKeyPrice", this.monKeyPrice);
        config.set("QRPrice", this.qrPrice);

        config.set("MaximumWildTeleportRadius", this.maximumTeleportRadius);
        config.set("SpawnInBoatOnWater", this.wildTeleportInBoatOnWater);
        config.set("SpawnInBoatType", this.wildTeleportInBoatType.name());
        config.set("MaxSearchXOnLava", this.maxSearchXIfLavaWildTeleport);
        config.set("MaximumWildTeleportUses", this.maximumWildTeleportUses);

        List<String> teleportCosts = new ArrayList<>();

        for (double cost : this.wildTeleportCosts) {

            teleportCosts.add(String.valueOf(cost));

        }

        config.set("WildTeleportCosts", teleportCosts);

        this.main.saveConfig();

        return true;

    }

    public void reload() {
        main.reloadConfig();
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
            this.showDebug = configuration.getBoolean("ShowDebug");
            System.out.println("Debug Mode enabled: " + this.showDebug);

            if(configuration.contains("mongoURI")
                    && configuration.getString("mongoURI").length() > 0) {

                this.mongoDbConnectionString = configuration.getString("mongoURI");
                this.connectionType = DBConnectionType.MongoDB;

                System.out.println("MongoDB connection details identified!");

            }
            else if(configuration.contains("mysqlServerName")
                    && configuration.getString("mysqlServerName").length() > 0) {

                this.mysqlServerName = configuration.getString("mysqlServerName");

                if(configuration.isInt("mysqlPort")) {

                    this.mysqlPort = configuration.getInt("mysqlPort");

                } else {

                    this.mysqlPort = Integer.parseInt(configuration.getString("mysqlPort"));

                }

                this.mysqlDatabaseName = configuration.getString("mysqlDatabaseName");
                this.mysqlUsername = configuration.getString("mysqlUsername");
                this.mysqlPassword = configuration.getString("mysqlPassword");
                this.connectionType = DBConnectionType.MySQL;

                System.out.println("MySQL connection details identified!");

            }
            else {

                this.mongoDbConnectionString = "";
                this.connectionType = DBConnectionType.Json;

                System.out.println("No connection details identified! Using Json default...");

            }

            donateCommandEnabled = configuration.getBoolean("EnableDonateCommand");
            System.out.println("- Donate Command: " + (donateCommandEnabled ? "Active" : "Deactivated"));

            monkeyMapsEnabled = configuration.getBoolean("EnableMonKeyMaps");
            System.out.println("- MonkeyMaps Command: " + (monkeyMapsEnabled ? "Active" : "Deactivated"));

            pvpToggleEnabled = configuration.getBoolean("EnablePvpToggle");
            System.out.println("- PvP Toggle Command: " + (pvpToggleEnabled ? "Active" : "Deactivated"));

            rainEnabled = configuration.getBoolean("EnableRainCommand");
            System.out.println("- Rain Command: " + (rainEnabled ? "Active" : "Deactivated"));

            teleportEnabled = configuration.getBoolean("EnableTeleportCommands");
            System.out.println("- Teleport Commands: " + (teleportEnabled ? "Active" : "Deactivated"));

            minimumRainPerPlayer = configuration.getDouble("MinimumRainPerPlayer");
            System.out.println("- Minimum Rain Per Player: " + this.minimumRainPerPlayer);

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

            minimumTeleportDistance = configuration.getInt("MinimumTeleportDistance");
            System.out.println("- Teleport Minimum Distance: " + minimumTeleportDistance);

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

            this.maximumTeleportRadius = configuration.getInt("MaximumWildTeleportRadius");
            System.out.println("Maximum Wild Teleport Radius: " + this.maximumTeleportRadius);

            if(configuration.contains("MaximumWildTeleportRadius")) {

                try {

                    this.maximumTeleportRadius = configuration.getInt("MaximumWildTeleportRadius");

                }
                catch (Exception ex) {

                    System.out.println("Invalid MaximumWildTeleportRadius value... using default instead.");

                }

            }

            System.out.println("- Maximum wild teleport location radius from world spawn set: " + this.maximumTeleportRadius);

            if(configuration.contains("SpawnInBoatOnWater")) {

                try {

                    this.wildTeleportInBoatOnWater = configuration.getBoolean("SpawnInBoatOnWater");

                }
                catch (Exception ex) {

                    System.out.println("Invalid SpawnInBoatOnWater value... using default instead.");

                }

            }

            System.out.println("- Player will wild teleport in a boat if on water: " + this.wildTeleportInBoatOnWater);

            if(configuration.contains("SpawnInBoatType")) {

                try {

                    this.wildTeleportInBoatType = org.bukkit.entity.Boat.Type.valueOf(configuration.getString("SpawnInBoatType"));

                }
                catch (Exception ex) {

                    System.out.println("Invalid SpawnInBoatType value... using default instead.");

                }

            }

            System.out.println("- Player will wild teleport with boat type if on water: " + this.wildTeleportInBoatType.name());

            if(configuration.contains("MaxSearchXOnLava")) {

                try {

                    this.maxSearchXIfLavaWildTeleport = configuration.getInt("MaxSearchXOnLava");

                }
                catch (Exception ex) {

                    System.out.println("Invalid MaxSearchXOnLava value... using default instead.");

                }

            }

            System.out.println("- Maximum distance to search for safety if wild teleport on lava set: " + this.maxSearchXIfLavaWildTeleport);

            if(configuration.contains("MaximumWildTeleportUses")) {

                try {

                    this.maximumWildTeleportUses = configuration.getInt("MaximumWildTeleportUses");

                }
                catch (Exception ex) {

                    System.out.println("Invalid MaximumWildTeleportUses value... using default instead.");

                }

            }

            System.out.println("- Maximum number of wild teleport uses set: " + this.maximumWildTeleportUses);

            if(configuration.contains("WildTeleportCosts")) {

                try {

                    this.wildTeleportCosts = new double[this.maximumWildTeleportUses];

                    List<String> costs = configuration.getStringList("WildTeleportCosts");
                    int index = 0;

                    for(String cost : costs) {

                        if(this.wildTeleportCosts.length > index) {

                            try {

                                this.wildTeleportCosts[index] = Double.parseDouble(cost);

                            }
                            catch (Exception ex) {

                                System.out.println("Invalid wild teleport cost: " + cost);

                            }

                            index++;

                        }

                    }


                }
                catch (Exception ex) {

                    System.out.println("Invalid WildTeleportCosts value... using default instead.");

                }

            }

            System.out.println("- Wild teleport costs set: " + this.wildTeleportCosts.toString());

        }
        else {

            System.out.println("Cannot find configuration file!");

        }

    }

}
