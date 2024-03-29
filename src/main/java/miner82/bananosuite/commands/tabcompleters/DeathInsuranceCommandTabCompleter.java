package miner82.bananosuite.commands.tabcompleters;

import miner82.bananosuite.classes.DeathInsuranceOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DeathInsuranceCommandTabCompleter implements TabCompleter {

    public DeathInsuranceCommandTabCompleter() {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> results = new ArrayList<>();

        if(args.length == 1) {

            results.add("start");
            results.add("stop");
            results.add("quote");
            results.add("query");

        }
        else if(args.length == 2
                 && (args[0].equalsIgnoreCase("start")
                       || args[0].equalsIgnoreCase("quote"))) {

            results.add(DeathInsuranceOption.None.name());
            results.add(DeathInsuranceOption.Inventory.toString());
            results.add(DeathInsuranceOption.Full.toString());

        }

        return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
    }
}