package de.kniffo80.mobplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nukkit.IPlayer;
import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import de.kniffo80.mobplugin.entities.autospawn.IEntitySpawner;
import de.kniffo80.mobplugin.entities.monster.walking.Wolf;
import de.kniffo80.mobplugin.entities.spawners.WolfSpawner;


public class AutoSpawnTask implements Runnable {

    private Map<Integer, Integer> maxSpawns      = new HashMap<>();

    private List<IEntitySpawner>  entitySpawners = new ArrayList<>();
    
    private Config                pluginConfig   = null;
    
    private MobPlugin             plugin = null;

    public AutoSpawnTask(MobPlugin plugin) {
        this.pluginConfig = plugin.getConfig();
        this.plugin = plugin;

        prepareMaxSpawns();
        try {
            prepareSpawnerClasses();
        } catch (Exception e) {
            FileLogger.warn("Unable to prepare spawner classes: ", e);
        }

        FileLogger.info("Starting AutoSpawnTask");
    }

    @Override
    public void run() {
        // when any player is online, we want to spawn in his range. if no player is online, we spawn for players, that
        // were online in the last day
        List<IPlayer> players = plugin.getAllRegisteredPlayers();
        List<Player> onlinePlayers = new ArrayList<>();
        List<OfflinePlayer> offlinePlayers = new ArrayList<>();

        // organize lists for later use
        for (IPlayer foundPlayer : players) {
            if (foundPlayer instanceof Player) {
                onlinePlayers.add((Player) foundPlayer);
            } else {
                // as offline player is not working currently, we need to only submit online players ...
                // offlinePlayers.add((OfflinePlayer) foundPlayer);
            }
        }
        
        FileLogger.debug(String.format("Found %d online and %d offline players", onlinePlayers.size(), offlinePlayers.size()));
        for (IEntitySpawner spawner : entitySpawners) {
            spawner.spawn(onlinePlayers, offlinePlayers);
        }
    }

    private void prepareSpawnerClasses() {
        entitySpawners.add(new WolfSpawner(this));
        FileLogger.debug(String.format("prepared %d spawner classes", this.entitySpawners.size()));
    }

    private void prepareMaxSpawns() {
        maxSpawns.put(Wolf.NETWORK_ID, this.pluginConfig.getInt("spawn-config.max-wolf", 0));
    }

    public boolean entitySpawnAllowed(Level level, int networkId) {
        int count = countEntity(level, networkId);
        if (count < maxSpawns.get(networkId)) {
            return true;
        }
        return false;
    }

    private int countEntity(Level level, int networkId) {
        int count = 0;
        for (Entity entity : level.getEntities()) {
            if (entity.isAlive() && entity.getNetworkId() == networkId) {
                count++;
            }
        }
        return count;
    }

    public void createEntity(Object type, Position pos) {
        Entity entity = MobPlugin.create(type, pos);
        if (entity != null) {
            entity.spawnToAll();
        }
    }

    public int getRandomSafeXZCoord(int degree, int safeDegree, int correctionDegree) {
        int addX = Utils.rand(degree / 2 * -1, degree / 2);
        if (addX >= 0) {
            if (degree < safeDegree) {
                addX = safeDegree;
                addX += Utils.rand(correctionDegree / 2 * -1, correctionDegree / 2);
            }
        } else {
            if (degree > safeDegree) {
                addX = -safeDegree;
                addX += Utils.rand(correctionDegree / 2 * -1, correctionDegree / 2);
            }
        }
        return addX;
    }

    public int getSafeYCoord(Level level, Position pos, int needDegree) {
        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        if (level.getBlockIdAt(x, y, z) == Block.AIR) {
            while (true) {
                y--;
                if (y > 127) {
                    y = 128;
                    break;
                }
                if (y < 1) {
                    y = 0;
                    break;
                }
                if (level.getBlockIdAt(x, y, z) != Block.AIR) {
                    int checkNeedDegree = needDegree;
                    int checkY = y;
                    while (true) {
                        checkY++;
                        checkNeedDegree--;
                        if (checkY > 255 || checkY < 1 || level.getBlockIdAt(x, checkY, z) != Block.AIR) {
                            break;
                        }

                        if (checkNeedDegree <= 0) {
                            return y;
                        }
                    }
                }
            }
        } else {
            while (true) {
                y++;
                if (y > 127) {
                    y = 128;
                    break;
                }

                if (y < 1) {
                    y = 0;
                    break;
                }

                if (level.getBlockIdAt(x, y, z) != Block.AIR) {
                    int checkNeedDegree = needDegree;
                    int checkY = y;
                    while (true) {
                        checkY--;
                        checkNeedDegree--;
                        if (checkY > 255 || checkY < 1 || level.getBlockIdAt(x, checkY, z) != Block.AIR) {
                            break;
                        }

                        if (checkNeedDegree <= 0) {
                            return y;
                        }
                    }
                }
            }
        }
        return y;
    }
 
}