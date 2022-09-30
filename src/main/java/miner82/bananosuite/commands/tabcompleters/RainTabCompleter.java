package miner82.bananosuite.commands.tabcompleters;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RainTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> results = new ArrayList<>();

        if(args.length == 1
             && args[0].length() == 0) {

            results.add("[amount]");

        }
        else if(args.length == 2) {

            results.add("everyone");
            results.add("nearby");

        }
        else if(args.length == 3
                  && args[1].equalsIgnoreCase("nearby")
                  && args[2].length() == 0) {

            results.add("[distance]");

        }

        return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
    }

}
