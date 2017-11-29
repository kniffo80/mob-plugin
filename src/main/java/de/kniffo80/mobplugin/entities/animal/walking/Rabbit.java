package de.kniffo80.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import de.kniffo80.mobplugin.entities.animal.WalkingAnimal;
import de.kniffo80.mobplugin.entities.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Rabbit extends WalkingAnimal {

    public static final int NETWORK_ID = 18;

    public Rabbit(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.75f;
    }

    @Override
    public double getSpeed() {
        return 1.2;
    }

    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(3);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.CARROT && distance <= 49;
        }
        return false;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            int rabbitHide = Utils.rand(0, 2); // drops 0-1 rabit hide
            int rawRabbit = Utils.rand(0, 2); // drops 0-1 raw rabit
            for (int i=0; i < rabbitHide; i++) {
                drops.add(Item.get(Item.RABBIT_HIDE, 0, 1));
            }
            for (int i=0; i < rawRabbit; i++) {
                drops.add(Item.get(this.isOnFire() ? Item.COOKED_RABBIT : Item.RAW_RABBIT, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }
    
    @Override
    public int getKillExperience () {
        return Utils.rand(1, 4); // gain 1-3 experience
    }

}