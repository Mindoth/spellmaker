package net.mindoth.spellmaker.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mindoth.shadowizardlib.ShadowizardLib;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ProjectileSpellSingleRenderer extends EntityRenderer<ProjectileSpellSingleEntity> {

    public ProjectileSpellSingleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ProjectileSpellSingleEntity entity) {
        return new ResourceLocation(ShadowizardLib.MOD_ID, "textures/particle/clear.png");
    }

    @Override
    public void render(ProjectileSpellSingleEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
    }
}
