package com.example.horror;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NightWatcher.MOD_ID);

    public static final RegistryObject<Item> WATCHER_SPAWN_EGG = ITEMS.register("watcher_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WATCHER, 0x101014, 0xFFFFFF, new Item.Properties()));
}
