package miner82.bananosuite.tabcompleters;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PvPOptInOutTabCompleter implements TabCompleter {
    private ConfigEngine configEngine;

    public PvPOptInOutTabCompleter(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> results = new ArrayList<>();

        if(args.length == 1) {

            results.add("PVPOFF");
            results.add("PVPON");
            results.add("QUERY");

        }

        return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
    }
}