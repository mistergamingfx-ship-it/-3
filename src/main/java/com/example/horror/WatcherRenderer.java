package com.example.horror;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WatcherRenderer extends HumanoidMobRenderer<WatcherEntity, PlayerModel<WatcherEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NightWatcher.MOD_ID, "textures/entity/watcher.png");

    public WatcherRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        this.addLayer(new WatcherEyesLayer(this));
    }

    @Override
    protected void scale(WatcherEntity entity, PoseStack pose, float partialTick) {
        pose.scale(0.95f, 1.15f, 0.95f); // taller and thinner than a player
    }

    @Override
    public ResourceLocation getTextureLocation(WatcherEntity entity) {
        return TEXTURE;
    }
}
