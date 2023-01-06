package miner82.bananosuite;

import miner82.bananosuite.commands.*;
import miner82.bananosuite.commands.tabcompleters.*;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.dbconnectors.JsonDBConnector;
import miner82.bananosuite.dbconnectors.MongoDBConnector;
import miner82.bananosuite.dbconnectors.MySQLDBConnector;
import miner82.bananosuite.events.*;
import miner82.bananosuite.interfaces.IDBConnection;
import miner82.bananosuite.io.FileManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class BananoSuitePlugin extends JavaPlugin {

    private ConfigEngine configEngine;
    private static Economy econ = null;
    private IDBConnection db;

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        this.configEngine = new ConfigEngine(this);

        switch (this.configEngine.getConnectionType()) {

            case MongoDB:

                this.db = new MongoDBConnector(this, this.configEngine);

                break;

            case MySQL:

                this.db = new MySQLDBConnector(this, this.configEngine);

                break;

            default:

                this.db = new JsonDBConnector(this, this.configEngine);

                break;

        }

        InitialiseImageDirectories();

        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoinEvent(this.db), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerLeaveEvent(this.db), this);
        Bukkit.getPluginManager().registerEvents(new OnOPDamageEvent(this.db), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerDamageEvent(this.db), this);
        Bukkit.getPluginManager().registerEvents(new InventoryMapCloneEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MapCloneEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MonkeyMapInitialiseEvent(this.db, this.configEngine), this);

        getCommand("shields").setExecutor(new ShieldsCommand(this.db, this.configEngine));
        getCommand("bananosuite").setExecutor(new AdminCommand(this.configEngine, this.db));
        getCommand("sethome").setExecutor(new SetHomeCommand(this.db, this.configEngine));
        //////getCommand("pvptoggle").setExecutor(new PvPOptInOutCommand(this.db, this.configEngine));


        getCommand("bananosuite").setTabCompleter(new AdminCommandTabCompleter(this.configEngine));
        getCommand("shields").setTabCompleter(new ShieldsCommandTabCompleter());
        //////getCommand("pvptoggle").setTabCompleter(new PvPOptInOutTabCompleter(this.configEngine));

        // Banano Brawl - players each place an equal wager and the winner takes the pot minus a house fee. Tournament option?
        // Banano Lottery Draws (weekly?) - players pay in for a ticket - 1 ban? - and the winner takes the pot minus a house fee
        // Player faucets - use economy bank feature? Will need developing.
        // PvP opt-out/in

        // Delayed to ensure vault is initialised by the time we try to call it
        Bukkit.getScheduler().runTaskLater(this, this::setupBananoSuite, 5);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().getScheduler().cancelTasks(this);

        this.db.shutdown();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();

        return econ != null;
    }

    private boolean setupBananoSuite() {

        if (!setupEconomy() ) {
            System.out.println(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        // Plugin startup logic
        getCommand("help").setExecutor(new HelpCommand());

        getCommand("donate").setExecutor(new DonateCommand(this.db, this.configEngine, this.econ));
        getCommand("rain").setExecutor(new RainCommand(this.configEngine, this.econ));
        getCommand("teleport").setExecutor(new TeleportCommand(this.db, this.configEngine, this.econ));
        getCommand("home").setExecutor(new TeleportHomeCommand(this.db, this.configEngine, this.econ));
        getCommand("spawn").setExecutor(new TeleportSpawnCommand(this.configEngine, this.econ));
        getCommand("deathinsurance").setExecutor(new DeathInsuranceCommand(this.configEngine, this.econ, this.db));
        getCommand("monkeymap").setExecutor(new MonKeyMapCommand(this, this.db, this.configEngine, econ));

        getCommand("rain").setTabCompleter(new RainTabCompleter());
        getCommand("deathinsurance").setTabCompleter(new DeathInsuranceCommandTabCompleter());
        getCommand("teleport").setTabCompleter(new TeleportCommandTabCompleter());
        getCommand("home").setTabCompleter(new TeleportDirectCommandTabCompleter());
        getCommand("spawn").setTabCompleter(new TeleportDirectCommandTabCompleter());
        getCommand("monkeymap").setTabCompleter(new MonKeyMapTabCompleter(this.configEngine));

        Bukkit.getPluginManager().registerEvents(new OnPlayerDeathEvent(this.db, this.configEngine, this.econ), this);

        return true;
    }

    private void InitialiseImageDirectories() {
        FileManager.MakeDirectories(this.configEngine.getAbsoluteQRDirectoryPath());
        FileManager.MakeDirectories(this.configEngine.getAbsoluteMonKeyDirectoryPath());
    }
}
