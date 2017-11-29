package de.kniffo80.mobplugin.entities.monster.walking;

import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import de.kniffo80.mobplugin.entities.monster.WalkingMonster;
import de.kniffo80.mobplugin.entities.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Creeper extends WalkingMonster implements EntityExplosive {

    public static final int NETWORK_ID   = 33;

    public static final int DATA_POWERED = 19;

    private int             bombTime     = 0;
    
    private boolean         exploded     = false;

    public Creeper(FullChunk chunk, CompoundTag nbt) {
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
        return 0.9;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (this.namedTag.getBoolean("powered") || this.namedTag.getBoolean("IsPowered")) {
            this.dataProperties.putBoolean(DATA_POWERED, true);
        }
        setMaxHealth(20);
    }

    public boolean isPowered() {
        return this.getDataPropertyBoolean(DATA_POWERED);
    }

    public void setPowered() {
        this.namedTag.putBoolean("powered", true);
        this.setDataProperty(new ByteEntityData(DATA_POWERED, 1));
    }

    public void setPowered(boolean powered) {
        this.namedTag.putBoolean("powered", powered);
        this.setDataProperty(new ByteEntityData(DATA_POWERED, powered ? 1 : 0));
    }

    public int getBombTime() {
        return this.bombTime;
    }

    @Override
    public void explode() {
        ExplosionPrimeEvent ev = new ExplosionPrimeEvent(this, 2.8);
        this.server.getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);
            if (ev.isBlockBreaking()) {
                explosion.explodeA();
            }
            explosion.explodeB();
            this.exploded = true;
        }
        this.close();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.server.getDifficulty() < 1) {
            this.close();
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

        if (!this.isMovement()) {
            return true;
        }

        if (this.isKnockback()) {
            this.move(this.motionX * tickDiff, this.motionY, this.motionZ * tickDiff);
            this.motionY -= this.getGravity() * tickDiff;
            this.updateMovement();
            return true;
        }

        Vector3 before = this.target;
        this.checkTarget();

        if (this.target instanceof EntityCreature || before != this.target) {
            double x = this.target.x - this.x;
            double y = this.target.y - this.y;
            double z = this.target.z - this.z;

            double diff = Math.abs(x) + Math.abs(z);
            double distance = Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2));
            if (distance <= 4.5) {
                if (target instanceof EntityCreature) {
                    this.bombTime += tickDiff;
                    if (this.bombTime >= 64) {
                        this.explode();
                        return false;
                    }
                } else if (Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2) <= 1) {
                    this.moveTime = 0;
                }
            } else {
                this.bombTime -= tickDiff;
                if (this.bombTime < 0) {
                    this.bombTime = 0;
                }

                this.motionX = this.getSpeed() * 0.15 * (x / diff);
                this.motionZ = this.getSpeed() * 0.15 * (z / diff);
            }
            this.yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }

        double dx = this.motionX * tickDiff;
        double dz = this.motionZ * tickDiff;
        boolean isJump = this.checkJump(dx, dz);
        if (this.stayTime > 0) {
            this.stayTime -= tickDiff;
            this.move(0, this.motionY * tickDiff, 0);
        } else {
            Vector2 be = new Vector2(this.x + dx, this.z + dz);
            this.move(dx, this.motionY * tickDiff, dz);
            Vector2 af = new Vector2(this.x, this.z);

            if ((be.x != af.x || be.y != af.y) && !isJump) {
                this.moveTime -= 90 * tickDiff;
            }
        }

        if (!isJump) {
            if (this.onGround) {
                this.motionY = 0;
            } else if (this.motionY > -this.getGravity() * 4) {
                if (!(this.level.getBlock(new Vector3(NukkitMath.floorDouble(this.x), (int) (this.y + 0.8), NukkitMath.floorDouble(this.z))) instanceof BlockLiquid)) {
                    this.motionY -= this.getGravity() * 1;
                }
            } else {
                this.motionY -= this.getGravity() * tickDiff;
            }
        }
        this.updateMovement();
        return true;
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        return null;
    }

    public void attackEntity(Entity player) {
        // creepers don't attack, they only explode
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (this.exploded && this.isPowered()) {
            // TODO: add creeper head
        }
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            int gunPowder = Utils.rand(0, 3); // drops 0-2 gunpowder
            for (int i=0; i < gunPowder; i++) {
                drops.add(Item.get(Item.GUNPOWDER, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }
    
    @Override
    public int getKillExperience () {
        return 5; // gain 5 experience
    }

}