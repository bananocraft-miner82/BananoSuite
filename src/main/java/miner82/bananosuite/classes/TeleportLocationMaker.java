package miner82.bananosuite.classes;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class TeleportLocationMaker {

    public static Location checkSafeLocation(Location location) {

        if(isSafeLocation(location)) {
            return location;
        }

        Location checkLocation = location.clone();

        for(int y = 0; y <= 16; y++) {

            checkLocation.setY(location.getY() - y);

            if(isSafeLocation(checkLocation)) {

                return checkLocation;

            }

        }

        return null;

    }

    public static boolean isSafeLocation(Location location) {

        Block feet = location.getBlock();
        if (!feet.getType().isAir() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isAir()) {
            return false; // not transparent (will suffocate)
        }

        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isAir()) {
            return false; // not transparent (will suffocate)
        }

        Block ground = feet.getRelative(BlockFace.DOWN);
        if (!ground.getType().isSolid()) {
            return false; // not solid
        }
        return true;
    }

}
