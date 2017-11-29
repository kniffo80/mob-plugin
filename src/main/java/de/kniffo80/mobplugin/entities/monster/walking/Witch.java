package de.kniffo80.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.item.EntityPotion;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import de.kniffo80.mobplugin.MobPlugin;
import de.kniffo80.mobplugin.entities.monster.WalkingMonster;
import de.kniffo80.mobplugin.entities.utils.Utils;

public class Witch extends WalkingMonster {

    public static final int  NETWORK_ID   = 45;

    private static final int ATTACK_TICKS = 20; // how many ticks does the witch need to attack

    public Witch(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.72f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public double getSpeed() {
        return 1.0;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(26);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return !player.closed && player.spawned && player.isAlive() && player.isSurvival() && distance <= 100;
        }
        return creature.isAlive() && !creature.closed && distance <= 81;
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        super.attack(ev);
        return true;
    }

    /*
     * (@Override)
     * @see de.kniffo80.mobplugin.entities.monster.Monster#attackEntity(cn.nukkit.entity.Entity)
     */
    @Override
    public void attackEntity(Entity player) {
        if (MobPlugin.MOB_AI_ENABLED) {
            if (this.attackDelay > ATTACK_TICKS && this.distanceSquared(player) <= 8) { // they attack only beginning from 8 blocks away ...
                this.attackDelay = 0;
                if (player.isAlive() && !player.closed) {

                    double f = 2;
                    double yaw = this.yaw + Utils.rand(-220, 220) / 10;
                    double pitch = this.pitch + Utils.rand(-120, 120) / 10;
                    Location pos = new Location(this.x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5, this.y + this.getEyeHeight(),
                            this.z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5, yaw, pitch, this.level);

                    EntityPotion thrownPotion = (EntityPotion) MobPlugin.create("ThrownPotion", pos, this);

                    if (this.distance(player) <= 8 && !player.hasEffect(Effect.SLOWNESS)) {
                        thrownPotion.potionId = Potion.SLOWNESS;
                    } else if (player.getHealth() >= 8) {
                        thrownPotion.potionId = Potion.POISON;
                    } else if (this.distance(player) <= 3 && !player.hasEffect(Effect.WEAKNESS) && Utils.rand(0, 4) == 0) {
                        thrownPotion.potionId = Potion.WEAKNESS;
                    } else {
                        thrownPotion.potionId = Potion.HARMING;
                    }

                    thrownPotion.setMotion(new Vector3(-Math.sin(Math.toDegrees(yaw)) * Math.cos(Math.toDegrees(pitch)) * f * f, -Math.sin(Math.toDegrees(pitch)) * f * f,
                            Math.cos(Math.toDegrees(yaw)) * Math.cos(Math.toDegrees(pitch)) * f * f));
                    ProjectileLaunchEvent launch = new ProjectileLaunchEvent(thrownPotion);
                    this.server.getPluginManager().callEvent(launch);
                    if (launch.isCancelled()) {
                        thrownPotion.kill();
                    } else {
                        thrownPotion.spawnToAll();
                        this.level.addSound(new LaunchSound(this), this.getViewers().values());
                    }
                }
            } else {
                this.attackDelay ++;
            }
        }
    }

    @Override
    public Item[] getDrops() {
        return new Item[0];
    }

    @Override
    public int getKillExperience() {
        return 5; // gain 5 experience
    }

}
