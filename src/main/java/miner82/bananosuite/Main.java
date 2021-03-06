package miner82.bananosuite;

import miner82.bananosuite.commands.*;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.events.*;
import miner82.bananosuite.io.FileManager;
import miner82.bananosuite.tabcompleters.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ConfigEngine configEngine;
    private static Economy econ = null;

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        this.configEngine = new ConfigEngine(this);

        InitialiseImageDirectories();

        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerLeaveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnOPDamageEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerDamageEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryMapCloneEvent(this.configEngine), this);
        Bukkit.getPluginManager().registerEvents(new MapCloneEvent(this.configEngine), this);
        Bukkit.getPluginManager().registerEvents(new MonkeyMapInitialiseEvent(this.configEngine), this);

        getCommand("reloadbananosuiteconfig").setExecutor(new ReloadConfigurationCommand(this.configEngine));
        getCommand("raiseshields").setExecutor(new RaiseShieldsCommand(this.configEngine));
        // Command ideas:
        // Death Insurance
        getCommand("enabledeathinsurance").setExecutor(new EnableDeathInsuranceCommand(this.configEngine));
        getCommand("stopdeathinsurance").setExecutor(new StopDeathInsuranceCommand(this.configEngine));

        getCommand("sethome").setExecutor(new SetHomeCommand(this.configEngine));
        getCommand("pvptoggle").setExecutor(new PvPOptInOutCommand(this.configEngine));


        getCommand("enabledeathinsurance").setTabCompleter(new EnableDeathInsuranceCommandTabCompleter(this.configEngine));
        getCommand("pvptoggle").setTabCompleter(new PvPOptInOutTabCompleter(this.configEngine));

        // Donate - possibly gift a random item from a weighted list? Change player messaging colour for a period of time.
        // Home Teleport - DONE
        // Banano Brawl - players each place an equal wager and the winner takes the pot minus a house fee. Tournament option?
        // Banano Lottery Draws (weekly?) - players pay in for a ticket - 1 ban? - and the winner takes the pot minus a house fee
        // Player faucets - use economy bank feature? Will need developing.
        // PvP opt-out/in
        // Rain on thunderstorm - DONE

        // Delayed to ensure vault is initialised by the time we try to call it
        Bukkit.getScheduler().runTaskLater(this, this::setupBananoSuite, 5);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().getScheduler().cancelTasks(this);
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
        getCommand("donate").setExecutor(new DonateCommand(this.configEngine, this.econ));
        getCommand("rain").setExecutor(new RainCommand(this.configEngine, this.econ));
        getCommand("tphome").setExecutor(new HomeTeleportCommand(this.configEngine, this.econ));
        getCommand("tpspawn").setExecutor(new SpawnTeleportCommand(this.configEngine, this.econ));
        getCommand("tpquote").setExecutor(new TeleportQuoteCommand(this.configEngine, this.econ));
        getCommand("startdeathinsurance").setExecutor(new StartDeathInsuranceCommand(this.configEngine, this.econ));
        getCommand("quotedeathinsurance").setExecutor(new QuoteDeathInsuranceCommand(this.configEngine, this.econ));
        getCommand("buymonkeymap").setExecutor(new MonKeyMapCommand(this.configEngine, econ));

        getCommand("quotedeathinsurance").setTabCompleter(new QuoteDeathInsuranceCommandTabCompleter());
        getCommand("startdeathinsurance").setTabCompleter(new StartDeathInsuranceCommandTabCompleter());
        getCommand("tpquote").setTabCompleter(new TeleportQuoteCommandTabCompleter());
        getCommand("tphome").setTabCompleter(new DirectTeleportCommandTabCompleter());
        getCommand("tpspawn").setTabCompleter(new DirectTeleportCommandTabCompleter());
        getCommand("buymonkeymap").setTabCompleter(new MonKeyMapTabCompleter(this.configEngine));

        Bukkit.getPluginManager().registerEvents(new OnPlayerDeathEvent(this.configEngine, this.econ), this);
        //Bukkit.getPluginManager().registerEvents(new OnBlockMined(this.configEngine, this.econ), this);

        return true;
    }

    private void InitialiseImageDirectories() {
        FileManager.MakeDirectories(this.configEngine.getAbsoluteQRDirectoryPath());
        FileManager.MakeDirectories(this.configEngine.getAbsoluteMonKeyDirectoryPath());
    }
}
