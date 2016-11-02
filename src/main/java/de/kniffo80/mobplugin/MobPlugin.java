/**
 * MobPlugin.java
 * 
 * Created on 17:46:07
 */
package de.kniffo80.mobplugin;

import java.io.File;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import de.kniffo80.mobplugin.entities.BaseEntity;
import de.kniffo80.mobplugin.entities.monster.walking.Wolf;

/**
 * @author <a href="mailto:mige@whatevermobile.com">Michael Gertz (mige)</a>
 */
public class MobPlugin extends PluginBase implements Listener {

    private boolean mobAiEnabled = false;

    @Override
    public void onLoad() {
        registerEntities();
        Utils.logServerInfo("Plugin loaded successfully.");
    }

    @Override
    public void onEnable() {
        // Config reading and writing
        Config config = new Config(new File(this.getDataFolder(), "mobplugin.yml"));
        
        this.mobAiEnabled = config.getBoolean("entities.mob-ai", false);
        
        // register as listener to plugin events
        this.getServer().getPluginManager().registerEvents(this, this);
        
        Utils.logServerInfo(String.format("Plugin enabling successful [aiEnabled:%s]", this.mobAiEnabled));
    }

    @Override
    public void onDisable() {
        Utils.logServerInfo("Plugin disabled successful.");
    }
    

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] sub) {
        String output = "";
        
        if (sub.length == 0) {
            output += "no command given. Use 'mob spawn Wolf <opt:playername(if spawned by server)>' e.g.";
        } else {
            switch (sub[0]) {
                case "spawn":
                    String mob = sub[1];
                    Player playerThatSpawns = null;
                    
                    if (sub.length == 3) {
                        playerThatSpawns = this.getServer().getPlayer(sub[2]);
                    } else {
                        playerThatSpawns = (Player)commandSender;
                    }
                    
                    if (playerThatSpawns != null) {
                        Position pos = playerThatSpawns.getPosition();
    
                        Entity ent;
                        if ((ent = MobPlugin.create(mob, pos)) != null) {
                            ent.spawnToAll();
                            output += "spawned " + mob + " to " + playerThatSpawns.getName();
                        } else {
                            output += "Unable to spawn " + mob;  
                        }
                    } else {
                        output += "Unknown player " + (sub.length == 3 ? sub[2] : ((Player)commandSender).getName());
                    }
                    break;
                default:
                    output += "Unkown command.";
                    break;
            }
        }
        
        commandSender.sendMessage(output);
        return true;
    }
    
    private void registerEntities () {
        Entity.registerEntity(Wolf.class.getSimpleName(), Wolf.class);
        Utils.logServerInfo("registerEntites: done.");
    }
    
    /**
     * 
     * @param type
     * @param source
     * @param args
     * @return
     */
    public static Entity create(Object type, Position source, Object... args) {
        FullChunk chunk = source.getLevel().getChunk((int) source.x >> 4, (int) source.z >> 4, true);
        if (!chunk.isGenerated()) {
            chunk.setGenerated();
        }
        if (!chunk.isPopulated()) {
            chunk.setPopulated();
        }

        CompoundTag nbt = new CompoundTag().
                putList(new ListTag<DoubleTag>("Pos").
                        add(new DoubleTag("", source.x)).
                        add(new DoubleTag("", source.y)).
                        add(new DoubleTag("", source.z))).
                putList(new ListTag<DoubleTag>("Motion").
                        add(new DoubleTag("", 0)).
                        add(new DoubleTag("", 0)).
                        add(new DoubleTag("", 0))).
                putList(new ListTag<FloatTag>("Rotation").
                        add(new FloatTag("", source instanceof Location ? (float) ((Location) source).yaw : 0)).
                        add(new FloatTag("", source instanceof Location ? (float) ((Location) source).pitch : 0)));

        return Entity.createEntity(type.toString(), chunk, nbt, args);
    }
    
    
    // --- event listeners ---
    /**
     * This event is called when an entity dies. We need this for experience gain.
     * @param ev    the event that is received
     */
    @EventHandler
    public void EntityDeathEvent (EntityDeathEvent ev) {
        if (ev.getEntity() instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity)ev.getEntity();
            if (baseEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent)baseEntity.getLastDamageCause()).getDamager();
                if (damager instanceof Player) {
                    Player player = (Player)damager;
                    int killExperience = baseEntity.getKillExperience();
                    if (killExperience > 0 && player != null && player.isSurvival()) {
                        player.addExperience(killExperience);
                        // don't drop that fucking experience orbs because they're somehow buggy :(
//                        if (player.isSurvival()) {
//                            for (int i = 1; i <= killExperience; i++) {
//                                player.getLevel().dropExpOrb(baseEntity, 1);
//                            }
//                        }
                    }
                }
            }
        }
    }
    
}
