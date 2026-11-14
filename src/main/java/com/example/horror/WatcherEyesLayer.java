package com.example.horror;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Glowing white eyes, visible even in total darkness. */
public class WatcherEyesLayer extends EyesLayer<WatcherEntity, PlayerModel<WatcherEntity>> {
    private static final RenderType EYES = RenderType.eyes(
            new ResourceLocation(NightWatcher.MOD_ID, "textures/entity/watcher_eyes.png"));

    public WatcherEyesLayer(RenderLayerParent<WatcherEntity, PlayerModel<WatcherEntity>> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
