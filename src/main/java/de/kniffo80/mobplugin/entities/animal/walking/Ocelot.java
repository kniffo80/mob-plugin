package de.kniffo80.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import de.kniffo80.mobplugin.Utils;
import de.kniffo80.mobplugin.entities.animal.WalkingAnimal;

public class Ocelot extends WalkingAnimal {

    public static final int NETWORK_ID = 22;

    public Ocelot(FullChunk chunk, CompoundTag nbt) {
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
        return 1.4;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
    }

    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.RAW_FISH && distance <= 49;
        }
        return false;
    }

    public Item[] getDrops() {
        return new Item[0];
    }
    
    public int getKillExperience () {
        return Utils.rand(1, 4); // gain 1-3 experience
    }

}
