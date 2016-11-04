/**
 * MobPlugin.java
 * 
 * Created on 17:46:07
 */
package de.kniffo80.mobplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.nukkit.IPlayer;
import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.food.Food;
import cn.nukkit.item.food.FoodNormal;
import cn.nukkit.level.Level;
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
import de.kniffo80.mobplugin.entities.projectile.EntityFireBall;
import de.kniffo80.mobplugin.items.ItemMuttonCooked;
import de.kniffo80.mobplugin.items.ItemMuttonRaw;
import de.kniffo80.mobplugin.items.MobPluginItems;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz (kniffo80)</a>
 */
public class MobPlugin extends PluginBase implements Listener {

    public static boolean MOB_AI_ENABLED = false;

    private Config        pluginConfig   = null;

    @Override
    public void onLoad() {
        registerEntities();
        registerItems();
        Utils.logServerInfo("Plugin loaded successfully.");
    }

    @Override
    public void onEnable() {
        // Config reading and writing
        
        System.out.println("datafolder: " + this.getDataFolder());
        
        pluginConfig = new Config(new File(this.getDataFolder(), "config.yml"));

        // we need this flag as it's controlled by the plugin's entities
        MOB_AI_ENABLED = pluginConfig.getBoolean("entities.mob-ai", false);
        int spawnDelay = pluginConfig.getInt("entities.auto-spawn-tick", 0);

        // register as listener to plugin events
        this.getServer().getPluginManager().registerEvents(this, this);
        
        if (spawnDelay > 0) {
            this.getServer().getScheduler().scheduleRepeatingTask(new AutoSpawnTask(this), spawnDelay, true);
        }

        Utils.logServerInfo(String.format("Plugin enabling successful [aiEnabled:%s] [autoSpawnTick:%d]", MOB_AI_ENABLED, spawnDelay));
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
                        playerThatSpawns = (Player) commandSender;
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
                        output += "Unknown player " + (sub.length == 3 ? sub[2] : ((Player) commandSender).getName());
                    }
                    break;
                case "removeall":
                    int count = 0;
                    for (Level level : getServer().getLevels().values()) {
                        for (Entity entity : level.getEntities()) {
                            if (entity instanceof BaseEntity && !entity.closed && entity.isAlive()) {
                                entity.close();
                                count++;
                            }
                        }
                    }
                    output += "Removed " + count + " entities from all levels.";
                    break;
                case "removeitems":
                    count = 0;
                    for (Level level : getServer().getLevels().values()) {
                        for (Entity entity : level.getEntities()) {
                            if (entity instanceof EntityItem && entity.isOnGround()) {
                                entity.close();
                                count++;
                            }
                        }
                    }
                    output += "Removed " + count + " items on ground from all levels.";
                    break;
                default:
                    output += "Unkown command.";
                    break;
            }
        }

        commandSender.sendMessage(output);
        return true;
    }

    /**
     * Returns plugin specific yml configuration
     * @return  a {@link Config} instance
     */
    public Config getPluginConfig() {
        return this.pluginConfig;
    }

    private void registerEntities() {
        Entity.registerEntity(Wolf.class.getSimpleName(), Wolf.class);
        
        Entity.registerEntity("FireBall", EntityFireBall.class);
        
        Utils.logServerInfo("registerEntites: done.");
    }
    
    private void registerItems () {
        // register the new items
        Item.addCreativeItem(new ItemMuttonCooked());
        Item.addCreativeItem(new ItemMuttonRaw());
        
        // register the items as food 
        Food.registerFood(new FoodNormal(6, 9.6F).addRelative(MobPluginItems.COOKED_MUTTON), this);
        Food.registerFood(new FoodNormal(2, 1.2F).addRelative(MobPluginItems.RAW_MUTTON), this);
        
        Item.list[MobPluginItems.COOKED_MUTTON] = ItemMuttonCooked.class;
        Item.list[MobPluginItems.RAW_MUTTON] = ItemMuttonRaw.class;
        
        Utils.logServerInfo("registerItems: done.");
    }

    /**
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

        CompoundTag nbt = new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.x)).add(new DoubleTag("", source.y)).add(new DoubleTag("", source.z)))
                .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", source instanceof Location ? (float) ((Location) source).yaw : 0))
                        .add(new FloatTag("", source instanceof Location ? (float) ((Location) source).pitch : 0)));

        return Entity.createEntity(type.toString(), chunk, nbt, args);
    }
    
    /**
     * Returns all registered players to the current server
     * @return  a {@link List} containing a number of {@link IPlayer} elements, which can be {@link Player} or {@link OfflinePlayer}
     */
    public List<IPlayer> getAllRegisteredPlayers () {
        List<IPlayer> playerList = new ArrayList<>();
        for (Player player : this.getServer().getOnlinePlayers().values()) {
            playerList.add(player);
        }
        // now get all stores offline players ...
        File playerDirectory = new File(this.getServer().getDataPath() + "players");
        File entry;
        String[] storedFiles = playerDirectory.list();
        if (storedFiles != null && storedFiles.length > 0) {
            for (String file : storedFiles) {
                entry = new File(file);
                String filename = entry.getName();
                filename = filename.substring(0, filename.indexOf(".dat"));
                if (!isPlayerAlreadyInList(filename, playerList)) {
                    playerList.add(new OfflinePlayer(this.getServer(), filename));
                }
            }
        }
        return playerList;
    }
    
    /**
     * checks if a given player name's player instance is already in the given list
     * @param name          the name of the player to be checked   
     * @param playerList    the existing entries
     * @return <code>true</code> if the player is already in the list
     */
    private boolean isPlayerAlreadyInList (String name, List<IPlayer> playerList) {
        for (IPlayer player : playerList) {
            if (player.getName().toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // --- event listeners ---
    /**
     * This event is called when an entity dies. We need this for experience gain.
     * 
     * @param ev the event that is received
     */
    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent ev) {
        if (ev.getEntity() instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) ev.getEntity();
            if (baseEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) baseEntity.getLastDamageCause()).getDamager();
                if (damager instanceof Player) {
                    Player player = (Player) damager;
                    int killExperience = baseEntity.getKillExperience();
                    if (killExperience > 0 && player != null && player.isSurvival()) {
                        player.addExperience(killExperience);
                        // don't drop that fucking experience orbs because they're somehow buggy :(
                        // if (player.isSurvival()) {
                        // for (int i = 1; i <= killExperience; i++) {
                        // player.getLevel().dropExpOrb(baseEntity, 1);
                        // }
                        // }
                    }
                }
            }
        }
    }

}
