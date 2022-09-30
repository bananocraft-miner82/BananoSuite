package miner82.bananosuite.classes;

import org.bukkit.ChatColor;

public enum PlayerRank {
    None(ChatColor.WHITE + "None", false),
    Builder(ChatColor.LIGHT_PURPLE + "Builder", false),
    Explorer(ChatColor.GREEN + "Explorer", false),
    Warrior(ChatColor.YELLOW + "Warrior", false),
    BCPD(ChatColor.AQUA + "BCPD", false),
    OP(ChatColor.RED + "ADMIN", false);

    private String display;
    private boolean perks;

    PlayerRank(String display, boolean perks) {
        this.display = display;
        this.perks = perks;
    }

    public String getDisplay() {
        return this.display;
    }

    public boolean getPerks() {
        return this.perks;
    }

}
