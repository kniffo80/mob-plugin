package de.kniffo80.mobplugin.entities.animal;

import cn.nukkit.Player;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import co.aikar.timings.Timings;
import de.kniffo80.mobplugin.entities.WalkingEntity;

public abstract class WalkingAnimal extends WalkingEntity implements Animal {

    public WalkingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public double getSpeed() {
        return 0.8;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = false;
        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
            hasUpdate = true;
            int airTicks = this.getDataPropertyShort(DATA_AIR) - tickDiff;
            if (airTicks <= -20) {
                airTicks = 0;
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.DROWNING, 2));
            }
            this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
        } else {
            this.setDataProperty(new ShortEntityData(DATA_AIR, 300));
        }

        Timings.entityBaseTickTimer.stopTiming();
        return hasUpdate;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive()) {
            if (++this.deadTicks >= 23) {
                this.close();
                return false;
            }
            return true;
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;
        this.entityBaseTick(tickDiff);

        Vector3 target = this.updateMove(tickDiff);
        if (target instanceof Player) {
            if (this.distanceSquared(target) <= 2) {
                this.pitch = 22;
                this.x = this.lastX;
                this.y = this.lastY;
                this.z = this.lastZ;
            }
        } else if (target != null && (Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2)) <= 1) {
            this.moveTime = 0;
        }
        return true;
    }

}
