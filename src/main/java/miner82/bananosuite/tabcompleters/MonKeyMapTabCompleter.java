package miner82.bananosuite.tabcompleters;

import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.classes.MonKeyType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class MonKeyMapTabCompleter implements TabCompleter {

    private ConfigEngine configEngine;

    public MonKeyMapTabCompleter(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> results = new ArrayList<>();

        if(args.length == 1) {

            results.add(MonKeyType.MonKey.toString());
            results.add(MonKeyType.QRCode.toString());
            results.add("help");

        }
        else if(args.length == 2
                 && args[1] == null || args[1] == "") {

            results.add("ban_");

        }
        else if(args.length == 3
                 && args[0].equalsIgnoreCase(MonKeyType.MonKey.toString())
                 && this.configEngine.getApplyFrame().length() > 0) {

            results.add("none");

            for(String key : this.configEngine.getAvailableFrames().keySet()) {

                results.add(key);

            }


        }

        return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
    }
}