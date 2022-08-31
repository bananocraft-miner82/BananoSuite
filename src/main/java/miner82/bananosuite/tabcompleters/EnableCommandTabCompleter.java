package miner82.bananosuite.tabcompleters;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class EnableCommandTabCompleter implements TabCompleter{
    private ConfigEngine configEngine;

    public EnableCommandTabCompleter(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> results = new ArrayList<>();

        if(args.length == 1) {

            results.add("deathinsurance");
            results.add("donate");
            results.add("monkeymaps");
            results.add("pvptoggle");
            results.add("rain");
            results.add("teleport");

        }
        else if(args.length == 2) {

            results.add("enable");
            results.add("disable");
            results.add("status");

        }

        return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
    }
}