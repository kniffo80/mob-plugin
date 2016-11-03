/**
 * EntitySpawner.java
 * 
 * Created on 10:38:53
 */
package de.kniffo80.mobplugin.entities.autospawn;

import java.util.List;

import cn.nukkit.IPlayer;
import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public interface IEntitySpawner {

    public void spawn(List<Player> onlinePlayers, List<OfflinePlayer> offlinePlayers);

    public SpawnResult spawn(IPlayer iPlayer, Position pos, Level level);
    
    public int getEntityNetworkId ();
    
    public String getEntityName ();

}
