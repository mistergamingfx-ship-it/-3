package com.example.horror;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NightWatcher.MOD_ID)
public class HorrorEvents {
    private static final SoundEvent[] SOUNDS = {
            SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.CHEST_OPEN, SoundEvents.ENDERMAN_STARE,
            SoundEvents.GHAST_AMBIENT, SoundEvents.ZOMBIE_AMBIENT, SoundEvents.GRAVEL_STEP,
            SoundEvents.CREEPER_PRIMED, SoundEvents.STONE_STEP, SoundEvents.WARDEN_NEARBY_CLOSE
    };
    private static final String[] NAMES = {"Herobrine", "Entity_303", "null", "Steve_"};

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.side != LogicalSide.SERVER) return;
        if (!(e.level instanceof ServerLevel level)) return;
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;
        if (level.getGameTime() % HorrorConfig.EVENT_INTERVAL.get() != 0) return;

        RandomSource r = level.random;
        for (ServerPlayer p : level.players()) {
            if (p.isCreative() || p.isSpectator()) continue;

            if (roll(r, HorrorConfig.AMBIENT_CHANCE.get())) ambientSound(level, p, r);
            if (roll(r, HorrorConfig.DARKNESS_CHANCE.get()))
                p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100));
            if (roll(r, HorrorConfig.FAKE_JOIN_CHANCE.get()))
                p.sendSystemMessage(Component.translatable("multiplayer.player.joined",
                        NAMES[r.nextInt(NAMES.length)]).withStyle(ChatFormatting.YELLOW));
            if (roll(r, HorrorConfig.TORCH_CHANCE.get())) extinguishTorch(level, p, r);
            if (roll(r, HorrorConfig.DOOR_CHANCE.get())) toggleDoor(level, p, r);
            if (roll(r, HorrorConfig.WATCHER_CHANCE.get())) trySpawnWatcher(level, p, r);
        }
    }

    private static boolean roll(RandomSource r, int percent) {
        return percent > 0 && r.nextInt(100) < percent;
    }

    private static void ambientSound(ServerLevel level, ServerPlayer p, RandomSource r) {
        double angle = r.nextDouble() * Math.PI * 2;
        double d = 6 + r.nextDouble() * 6;
        double x = p.getX() + Math.cos(angle) * d;
        double z = p.getZ() + Math.sin(angle) * d;
        level.playSound(null, x, p.getY(), z, SOUNDS[r.nextInt(SOUNDS.length)],
                SoundSource.AMBIENT, 1.0f, 0.5f + r.nextFloat() * 0.5f);
    }

    private static void extinguishTorch(ServerLevel level, ServerPlayer p, RandomSource r) {
        BlockPos c = p.blockPosition();
        for (int i = 0; i < 60; i++) {
            BlockPos pos = c.offset(r.nextInt(17) - 8, r.nextInt(7) - 3, r.nextInt(17) - 8);
            BlockState s = level.getBlockState(pos);
            if (s.is(Blocks.TORCH) || s.is(Blocks.WALL_TORCH)
                    || s.is(Blocks.SOUL_TORCH) || s.is(Blocks.SOUL_WALL_TORCH)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.8f, 1.0f);
                return;
            }
        }
    }

    private static void toggleDoor(ServerLevel level, ServerPlayer p, RandomSource r) {
        BlockPos c = p.blockPosition();
        for (int i = 0; i < 60; i++) {
            BlockPos pos = c.offset(r.nextInt(17) - 8, r.nextInt(5) - 2, r.nextInt(17) - 8);
            BlockState s = level.getBlockState(pos);
            if (s.is(BlockTags.WOODEN_DOORS) && s.getBlock() instanceof DoorBlock door) {
                door.setOpen(null, level, s, pos, !s.getValue(DoorBlock.OPEN));
                return;
            }
        }
    }

    private static void trySpawnWatcher(ServerLevel level, ServerPlayer p, RandomSource r) {
        if (level.dimension() != Level.OVERWORLD) return;
        if (HorrorConfig.WATCHER_ONLY_AT_NIGHT.get() && !level.isNight()) return;
        if (!level.getEntitiesOfClass(WatcherEntity.class, p.getBoundingBox().inflate(80)).isEmpty()) return;

        double angle = r.nextDouble() * Math.PI * 2;
        double d = 24 + r.nextDouble() * 8;
        int x = (int) (p.getX() + Math.cos(angle) * d);
        int z = (int) (p.getZ() + Math.sin(angle) * d);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

        WatcherEntity w = ModEntities.WATCHER.get().create(level);
        if (w == null) return;
        w.moveTo(x + 0.5, y, z + 0.5, r.nextFloat() * 360f, 0f);
        level.addFreshEntity(w);
    }
}
