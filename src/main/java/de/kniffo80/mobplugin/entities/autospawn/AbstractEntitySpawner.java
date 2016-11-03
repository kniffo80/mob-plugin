/**
 * AbstractEntitySpawner.java
 * 
 * Created on 10:40:29
 */
package de.kniffo80.mobplugin.entities.autospawn;

import java.util.List;

import cn.nukkit.IPlayer;
import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import de.kniffo80.mobplugin.AutoSpawnTask;
import de.kniffo80.mobplugin.FileLogger;
import de.kniffo80.mobplugin.Utils;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public abstract class AbstractEntitySpawner implements IEntitySpawner {

    protected AutoSpawnTask    spawnTask;

    protected Server            server;

    public AbstractEntitySpawner(AutoSpawnTask spawnTask) {
        this.spawnTask = spawnTask;
        this.server = Server.getInstance();
    }

    /*
     * (@Override)
     * @see cn.nukkit.entity.ai.IEntitySpawner#spawn(java.util.List, java.util.List)
     */
    @Override
    public void spawn(List<Player> onlinePlayers, List<OfflinePlayer> offlinePlayers) {
        // first spawn everything for online players ...
        if (isSpawnAllowedByDifficulty()) {
            SpawnResult lastSpawnResult = null;
            for (Player player : onlinePlayers) {
                lastSpawnResult = spawn(player);
                if (lastSpawnResult.equals(SpawnResult.MAX_SPAWN_REACHED)) {
                    break;
                }
            }
            if (lastSpawnResult == null || !lastSpawnResult.equals(SpawnResult.MAX_SPAWN_REACHED)) {
                for (OfflinePlayer player : offlinePlayers) {
                    lastSpawnResult = spawn(player);
                    // stop spawning because max spawn is reached!
                    break;
                }
            }
        } else {
            FileLogger.debug(String.format("Spawn not allowed because of difficulty [entityName:%s]", getEntityName()));
        }
        
    }
    
    protected SpawnResult spawn (IPlayer iPlayer) {
//        boolean offlinePlayer = iPlayer instanceof OfflinePlayer;
//
//        Level level = offlinePlayer ? ((OfflinePlayer) iPlayer).getLevel() : ((Player) iPlayer).getLevel();
//        
//        if (!isEntitySpawnAllowed(level)) {
//            return SpawnResult.MAX_SPAWN_REACHED;
//        }
//        
//        Position pos = offlinePlayer ? ((OfflinePlayer) iPlayer).getLastKnownPosition() : ((Player) iPlayer).getPosition();
        Position pos = ((Player)iPlayer).getPosition();
        Level level = ((Player)iPlayer).getLevel();
        
        if (this.spawnTask.entitySpawnAllowed(level, getEntityNetworkId())) {
            if (pos != null) {
                // get a random safe position for spawn
                pos.x += this.spawnTask.getRandomSafeXZCoord(50, 26, 6);
                pos.z += this.spawnTask.getRandomSafeXZCoord(50, 26, 6);
                pos.y = this.spawnTask.getSafeYCoord(level, pos, 3);
            }
            
            if (pos == null) {
                return SpawnResult.POSITION_MISMATCH;
            }
        } else {
            return SpawnResult.MAX_SPAWN_REACHED;
        }
        
        return spawn(iPlayer, pos, level);
    }

    /**
     * A simple method that evaluates based on the difficulty set in server if a spawn is allowed or not
     * 
     * @return
     */
    protected boolean isSpawnAllowedByDifficulty() {

        int randomNumber = Utils.rand(0, 4);

        switch (getCurrentDifficulty()) {
            case PEACEFUL:
                return randomNumber == 0;
            case EASY:
                return randomNumber <= 1;
            case NORMAL:
                return randomNumber <= 2;
            case HARD:
                return true; // in hard: always spawn
            default:
                return true;
        }
    }

    /**
     * Returns currently set difficulty as en {@link Enum}
     * 
     * @return a {@link Difficulty} instance
     */
    protected Difficulty getCurrentDifficulty() {
        return Difficulty.getByDiffculty(this.server.getDifficulty());
    }
    
}
