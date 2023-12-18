package miner82.bananosuite.classes;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LocationRandomiser {

    private final ConfigEngine configEngine;
    private final Random random = new Random();

    public LocationRandomiser(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    public Location calculateRandomLocation(Player forPlayer) {

        if(forPlayer != null) {

            return calculateRandomLocation(forPlayer, forPlayer.getWorld().getSpawnLocation().clone());

        }

        return null;

    }

    public Location calculateRandomLocation(Player forPlayer, Location aroundSourceLocation) {

        if(forPlayer == null
             || aroundSourceLocation == null) {

            return null;

        }

        World world = forPlayer.getWorld();
        Location location = world.getSpawnLocation().clone();

        if(aroundSourceLocation != null) {

            location = aroundSourceLocation.clone();

        }

        if(this.configEngine.getIsDebugMode()) {

            System.out.println("[BS " + forPlayer.getName() + "] Reconfiguring initial wild teleport location for player " + forPlayer.getName() + " [" + forPlayer.getUniqueId() + "]...");
            System.out.println("[BS " + forPlayer.getName() + "] Initial teleport location: " + location.toString());

        }

        int maxRadius = this.configEngine.getMaximumTeleportRadius();
        int spawnX = location.getBlockX();
        int spawnY = location.getBlockY();
        int spawnZ = location.getBlockZ();

        spawnX = random.nextInt(spawnX - maxRadius, spawnX + maxRadius);
        spawnZ = random.nextInt(spawnZ - maxRadius, spawnZ + maxRadius);

        // Now find the highest block at that point, in an effort to not spawn them in a cave...
        spawnY = world.getHighestBlockYAt(spawnX, spawnZ);

        location.setX(spawnX);
        location.setY(spawnY);
        location.setZ(spawnZ);

        // Let's check the block is safe to spawn them on it...
        Block block = world.getBlockAt(location);

        if(this.configEngine.getIsDebugMode()) {

            System.out.println("[BS " + forPlayer.getName() + "] Wild teleport block type: " + block.getType());

        }

        if(block.getType().name().endsWith("_LEAVES")) {

            if(this.configEngine.getIsDebugMode()) {

                System.out.println("[BS " + forPlayer.getName() + "] New teleport location is on a tree. Reconfiguring to ground level...");

            }

            while(block.getType().name().endsWith("_LEAVES")
                    || block.getType().isAir()) {

                location.setY(location.getBlockY() - 1);

                block = world.getBlockAt(location);

            }

        }

        if(block.getType() == Material.LAVA) {

            int blockCounter = 0;

            if(this.configEngine.getIsDebugMode()) {

                System.out.println("[BS " + forPlayer.getName() + "] New teleport location is on lava. Reconfiguring to safe location...");

            }

            // Try to move the player to the edge of the lava
            while(block.getType() == Material.LAVA
                    && blockCounter < this.configEngine.getMaxSearchXIfLavaWildTeleport()) {

                location.setX(location.getBlockX() + 1);
                location.setY(world.getHighestBlockYAt(location));

                block = world.getHighestBlockAt(location);

                blockCounter++;

            }

            // If it's still lava within the max search distance blocks... hard luck!
            if(block.getType() == Material.LAVA) {

                if(this.configEngine.getIsDebugMode()) {

                    System.out.println("[BS " + forPlayer.getName() + "] Safe spot over lava unavailable. Setting base block to Cobblestone.");

                }

                block.setType(Material.COBBLESTONE);


                Block playerFeet = block.getRelative(BlockFace.UP);

                playerFeet.setType(Material.WATER);

            }

        }

        location.setY(location.getY() + 1);

        if(this.configEngine.getIsDebugMode()) {

            System.out.println("[BS " + forPlayer.getName() + "] Player join location randomised to " + location.toString());

        }

        return location;

    }

    public boolean setPlayerInBoatIfWaterLocation(Player player, Location spawnLocation) {

        if(player == null
                || spawnLocation == null) {

            return false;

        }

        World world = player.getWorld();
        Block block = world.getHighestBlockAt(spawnLocation);

        if(this.configEngine.getIsDebugMode()) {

            System.out.println("[BS " + player.getName() + "] Destination block type: " + block.getType());

        }

        if(block.getType() == Material.WATER
             && this.configEngine.isWildTeleportInBoatOnWater()) {

            try {

                if(this.configEngine.getIsDebugMode()) {

                    System.out.println("[BS " + player.getName() + "] New destination location is on water. Attempting to place the player in a boat...");

                }

                // If it's water, let's at least put them in a boat so they have a fair chance.
                Boat boat = (Boat) world.spawnEntity(spawnLocation, EntityType.BOAT, true);

                boat.setBoatType(this.configEngine.getWildTeleportInBoatType());

                if(!boat.addPassenger(player)) {

                    try {

                        System.out.println("[BS " + player.getName() + "] Could not place the player in a boat... attempting to give them a boat instead...");

                        player.getInventory().setItemInMainHand(new ItemStack(this.configEngine.getWildTeleportInBoatType().getMaterial(), 1));

                    }
                    catch (Exception ex) {

                        ex.printStackTrace();

                    }

                }

                return true;

            }
            catch (Exception ex) {

                // If it's water and we couldn't put them in a boat, let's at least give them a boat so they have a fair chance.
                player.getInventory().setItemInMainHand(new ItemStack(this.configEngine.getWildTeleportInBoatType().getMaterial(), 1));

                if(this.configEngine.getIsDebugMode()) {

                    System.out.println("[BS " + player.getName() + "] An error occurred attempting to place the player in a boat:");

                }

                ex.printStackTrace();

            }

        }

        return false;

    }

    public boolean givePlayerLocationAir(Player player, Location spawnLocation) {

        if(player == null
                || spawnLocation == null) {

            return false;

        }

        boolean changedBlock = false;

        World world = spawnLocation.getWorld();
        Block feet = world.getBlockAt(spawnLocation.getBlockX(), spawnLocation.getBlockY() + 1, spawnLocation.getBlockZ());

        if(!feet.getType().isAir()) {

            if(this.configEngine.getIsDebugMode()) {

                System.out.println("[RJL " + player.getName() + "] The player has " + feet.getType().name() + " at their feet... setting to Air.");

            }

            feet.setType(Material.AIR);
            changedBlock = true;

        }

        Block body = world.getBlockAt(spawnLocation.getBlockX(), spawnLocation.getBlockY() + 2, spawnLocation.getBlockZ());

        if(!body.getType().isAir()) {

            System.out.println("[RJL " + player.getName() + "] The player has " + body.getType().name() + " at their head level... setting to Air.");

            body.setType(Material.AIR);
            changedBlock = true;

        }

        return changedBlock;

    }

}

