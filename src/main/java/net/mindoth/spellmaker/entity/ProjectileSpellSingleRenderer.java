package net.mindoth.spellmaker.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class ProjectileSpellSingleRenderer extends EntityRenderer<ProjectileSpellSingleEntity, EntityRenderState> {

    public ProjectileSpellSingleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
