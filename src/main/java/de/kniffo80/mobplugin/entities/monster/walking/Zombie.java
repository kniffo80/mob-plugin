package de.kniffo80.mobplugin.entities.monster.walking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.timings.Timings;
import de.kniffo80.mobplugin.Utils;
import de.kniffo80.mobplugin.entities.monster.WalkingMonster;

public class Zombie extends WalkingMonster implements EntityAgeable {

    public static final int NETWORK_ID = 32;

    public Zombie(FullChunk chunk, CompoundTag nbt) {
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
        return 1.1;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

//        if (this.getDataProperty(DATA_AGEABLE_FLAGS) == null) {
//            this.setDataProperty(new ByteEntityData(DATA_AGEABLE_FLAGS, (byte) 0));
//        }
        this.setDamage(new int[] { 0, 2, 3, 4 });
        setMaxHealth(20);
    }

    @Override
    public boolean isBaby() {
        return false;
//        return this.getDataFlag(DATA_AGEABLE_FLAGS, DATA_FLAG_BABY);
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);

        if (this.isAlive()) {
            if (15 < this.getHealth()) {
                this.setDamage(new int[] { 0, 2, 3, 4 });
            } else if (10 < this.getHealth()) {
                this.setDamage(new int[] { 0, 3, 4, 6 });
            } else if (5 < this.getHealth()) {
                this.setDamage(new int[] { 0, 3, 5, 7 });
            } else {
                this.setDamage(new int[] { 0, 4, 6, 9 });
            }
        }
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 10 && player.distanceSquared(this) <= 1) {
            this.attackDelay = 0;
            HashMap<Integer, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.MODIFIER_BASE, (float) this.getDamage());

            if (player instanceof Player) {
                @SuppressWarnings("serial")
                HashMap<Integer, Float> armorValues = new HashMap<Integer, Float>() {

                    {
                        put(Item.LEATHER_CAP, 1f);
                        put(Item.LEATHER_TUNIC, 3f);
                        put(Item.LEATHER_PANTS, 2f);
                        put(Item.LEATHER_BOOTS, 1f);
                        put(Item.CHAIN_HELMET, 1f);
                        put(Item.CHAIN_CHESTPLATE, 5f);
                        put(Item.CHAIN_LEGGINGS, 4f);
                        put(Item.CHAIN_BOOTS, 1f);
                        put(Item.GOLD_HELMET, 1f);
                        put(Item.GOLD_CHESTPLATE, 5f);
                        put(Item.GOLD_LEGGINGS, 3f);
                        put(Item.GOLD_BOOTS, 1f);
                        put(Item.IRON_HELMET, 2f);
                        put(Item.IRON_CHESTPLATE, 6f);
                        put(Item.IRON_LEGGINGS, 5f);
                        put(Item.IRON_BOOTS, 2f);
                        put(Item.DIAMOND_HELMET, 3f);
                        put(Item.DIAMOND_CHESTPLATE, 8f);
                        put(Item.DIAMOND_LEGGINGS, 6f);
                        put(Item.DIAMOND_BOOTS, 3f);
                    }
                };

                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += armorValues.getOrDefault(i.getId(), 0f);
                }

                damage.put(EntityDamageEvent.MODIFIER_ARMOR,
                        (float) (damage.getOrDefault(EntityDamageEvent.MODIFIER_ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.MODIFIER_BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, damage));
        }
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = false;
        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        int time = this.getLevel().getTime() % Level.TIME_FULL;
        if (!this.isOnFire() && !this.level.isRaining() && !(time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE)) {
            this.setOnFire(100);
        }

        Timings.entityBaseTickTimer.stopTiming();
        return hasUpdate;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            int rottenFlesh = Utils.rand(0, 3); // drops 0-2 rotten flesh
            for (int i=0; i < rottenFlesh; i++) {
                drops.add(Item.get(Item.ROTTEN_FLESH, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }
    
    @Override
    public int getKillExperience () {
        return 5; // gain 5 experience
    }

}
