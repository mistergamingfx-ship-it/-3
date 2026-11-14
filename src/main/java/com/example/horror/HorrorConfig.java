package com.example.horror;

import net.minecraftforge.common.ForgeConfigSpec;

/** Config file: config/nightwatcher-common.toml */
public class HorrorConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue EVENT_INTERVAL;
    public static final ForgeConfigSpec.IntValue AMBIENT_CHANCE;
    public static final ForgeConfigSpec.IntValue DARKNESS_CHANCE;
    public static final ForgeConfigSpec.IntValue FAKE_JOIN_CHANCE;
    public static final ForgeConfigSpec.IntValue TORCH_CHANCE;
    public static final ForgeConfigSpec.IntValue DOOR_CHANCE;
    public static final ForgeConfigSpec.IntValue WATCHER_CHANCE;
    public static final ForgeConfigSpec.BooleanValue WATCHER_ONLY_AT_NIGHT;
    public static final ForgeConfigSpec.IntValue WATCHER_LIFETIME_SECONDS;
    public static final ForgeConfigSpec.IntValue WATCHER_DAMAGE;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("events");
        EVENT_INTERVAL = b.comment("How often (ticks) events are rolled per player. 20 ticks = 1 second.")
                .defineInRange("eventIntervalTicks", 100, 20, 24000);
        AMBIENT_CHANCE = b.comment("% chance of a creepy sound behind you each roll")
                .defineInRange("ambientSoundChance", 25, 0, 100);
        DARKNESS_CHANCE = b.comment("% chance of sudden darkness")
                .defineInRange("darknessChance", 4, 0, 100);
        FAKE_JOIN_CHANCE = b.comment("% chance of a fake 'player joined the game' message")
                .defineInRange("fakeJoinChance", 3, 0, 100);
        TORCH_CHANCE = b.comment("% chance a nearby torch gets extinguished")
                .defineInRange("torchExtinguishChance", 6, 0, 100);
        DOOR_CHANCE = b.comment("% chance a nearby wooden door opens/closes by itself")
                .defineInRange("doorChance", 5, 0, 100);
        b.pop();

        b.push("watcher");
        WATCHER_CHANCE = b.comment("% chance each roll that The Watcher appears")
                .defineInRange("spawnChance", 10, 0, 100);
        WATCHER_ONLY_AT_NIGHT = b.define("onlyAtNight", true);
        WATCHER_LIFETIME_SECONDS = b.defineInRange("lifetimeSeconds", 120, 10, 3600);
        WATCHER_DAMAGE = b.defineInRange("damage", 6, 0, 100);
        b.pop();

        SPEC = b.build();
    }
}
