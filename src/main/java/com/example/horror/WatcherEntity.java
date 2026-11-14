package com.example.horror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Stalker. Moves toward you only while you are NOT looking at it.
 *  - watched from far away  -> silently fades away
 *  - watched from close / too long -> jumpscare
 *  - reaches you unseen     -> hits you and blinds you
 * A heartbeat plays when it is near and unseen.
 */
public class WatcherEntity extends Monster {
    private int stareTicks = 0;
    private int lifeTicks = 0;

    public WatcherEntity(EntityType<? extends WatcherEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.38)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        lifeTicks++;
        if (lifeTicks > 20 * HorrorConfig.WATCHER_LIFETIME_SECONDS.get()) {
            discard();
            return;
        }

        Player player = level().getNearestPlayer(getX(), getY(), getZ(), 64, true);
        if (player == null) return;

        double dist = distanceTo(player);

        if (isLookedAtBy(player)) {
            getNavigation().stop();
            setDeltaMovement(0, getDeltaMovement().y, 0);
            stareTicks++;

            if (dist < 7 || stareTicks > 20 * 6) {
                jumpscare(player);
            } else if (dist > 20 && stareTicks > 60) {
                // seen from far away: slip back into the dark
                level().playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_STARE,
                        SoundSource.HOSTILE, 0.6f, 0.5f);
                discard();
            }
        } else {
            stareTicks = Math.max(0, stareTicks - 1);
            getLookControl().setLookAt(player, 30f, 30f);
            getNavigation().moveTo(player, 1.25);

            if (dist < 24 && tickCount % 20 == 0) {
                float vol = (float) Math.max(0.3, 1.2 - dist / 24.0);
                level().playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT,
                        SoundSource.HOSTILE, vol, 1.0f);
            }

            if (dist < 2.0 && tickCount % 10 == 0) {
                player.hurt(damageSources().mobAttack(this), HorrorConfig.WATCHER_DAMAGE.get());
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200));
                level().playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_SCREAM,
                        SoundSource.HOSTILE, 1.0f, 0.6f);
            }
        }
    }

    private void jumpscare(Player player) {
        level().playSound(null, player.blockPosition(), SoundEvents.GHAST_SCREAM,
                SoundSource.HOSTILE, 1.5f, 0.5f);
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 160));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        discard();
    }

    private boolean isLookedAtBy(Player player) {
        Vec3 look = player.getViewVector(1.0f).normalize();
        Vec3 to = new Vec3(getX() - player.getX(), getEyeY() - player.getEyeY(), getZ() - player.getZ());
        if (to.lengthSqr() < 1.0E-4) return true;
        return look.dot(to.normalize()) > 0.95 && player.hasLineOfSight(this);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
