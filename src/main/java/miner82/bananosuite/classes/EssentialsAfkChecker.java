package miner82.bananosuite.classes;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EssentialsAfkChecker {

    public static boolean checkPlayerEssentialsIsAfk(Player player) {

        try {

            Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");

            if (essentials != null) {
                User user = ((Essentials) essentials).getUser(player);

                return user.isAfk();
            }

        }
        catch (Exception ex) {

        }

        return false;

    }

}
