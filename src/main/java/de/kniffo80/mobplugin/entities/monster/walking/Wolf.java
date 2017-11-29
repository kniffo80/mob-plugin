package de.kniffo80.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import de.kniffo80.mobplugin.MobPlugin;
import de.kniffo80.mobplugin.entities.monster.TameableMonster;

import java.util.HashMap;

public class Wolf extends TameableMonster {

    public static final int     NETWORK_ID           = 14;

    private static final String NBT_KEY_ANGRY        = "Angry";

    private static final String NBT_KEY_COLLAR_COLOR = "CollarColor";

    private int                 angry                = 0;

    private DyeColor            collarColor          = DyeColor.RED; // red is default

    public Wolf(FullChunk chunk, CompoundTag nbt) {
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
        return 0.9f;
    }

    @Override
    public double getSpeed() {
        return 1.2;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains(NBT_KEY_ANGRY)) {
            this.angry = this.namedTag.getInt(NBT_KEY_ANGRY);
        }

        if (this.namedTag.contains(NBT_KEY_COLLAR_COLOR)) {
            this.collarColor = DyeColor.getByDyeData(this.namedTag.getInt(NBT_KEY_COLLAR_COLOR));
        }

        this.setMaxHealth(8);
        this.fireProof = true;
        // this.setDamage(new int[] { 0, 3, 4, 6 });
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt(NBT_KEY_ANGRY, this.angry);
        this.namedTag.putInt(NBT_KEY_COLLAR_COLOR, this.collarColor.getDyeData());
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return this.isAngry() && super.targetOption(creature, distance);
    }

    public boolean isAngry() {
        return this.angry > 0;
    }

    public void setAngry(boolean angry) {
        this.angry = angry ? 1 : 0;
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        super.attack(ev);

        if (!ev.isCancelled()) {
            this.setAngry(true);
        }
        return true;
    }

    @Override
    public void attackEntity(Entity player) {
        if (MobPlugin.MOB_AI_ENABLED) {
            if (this.attackDelay > 10 && this.distanceSquared(player) < 1.6) {
                this.attackDelay = 0;
                HashMap<EntityDamageEvent.DamageModifier, Float> damage = new HashMap<>();
                damage.put(EntityDamageEvent.DamageModifier.BASE, (float) this.getDamage());

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

                    damage.put(EntityDamageEvent.DamageModifier.ARMOR,
                            (float) (damage.getOrDefault(EntityDamageEvent.DamageModifier.ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 1f) * points * 0.04)));
                }
                player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
            }
        }
    }

    @Override
    public Item[] getDrops() {
        return new Item[0];
    }

    @Override
    public int getKillExperience() {
        return 3; // gain 3 experience
    }

    /**
     * Sets the color of the wolves collar (default is 14)
     * 
     * @param color the color to be set (when tamed it should be RED)
     */
    public void setCollarColor(DyeColor color) {
        this.namedTag.putInt(NBT_KEY_COLLAR_COLOR, color.getDyeData());
        this.setDataProperty(new IntEntityData(DATA_COLOUR, color.getColor().getRGB()));
        this.collarColor = color;
    }

}
