package miner82.bananosuite.tabcompleters;

import miner82.bananosuite.classes.DeathInsuranceOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class StartDeathInsuranceCommandTabCompleter implements TabCompleter {

    public StartDeathInsuranceCommandTabCompleter() {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> results = new ArrayList<>();

        if(args.length == 1) {

            results.add(DeathInsuranceOption.None.toString());
            results.add(DeathInsuranceOption.Inventory.toString());
            results.add(DeathInsuranceOption.Full.toString());

        }

        return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
    }
}