package net.mindoth.spellmaker.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mindoth.shadowizardlib.ShadowizardLib;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ProjectileSpellMultiRenderer extends EntityRenderer<ProjectileSpellMultiEntity> {

    public ProjectileSpellMultiRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ProjectileSpellMultiEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ShadowizardLib.MOD_ID, "textures/particle/clear.png");
    }

    @Override
    public void render(ProjectileSpellMultiEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
    }
}
