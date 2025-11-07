package net.mindoth.spellmaker.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class SimpleRobeModel<T extends HumanoidRenderState> extends HumanoidModel<T> {

    //TODO: Armor "breathes" on an armorstand
    public SimpleRobeModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        float deform = 1.0F;
        CubeDeformation scale = new CubeDeformation(deform);
        MeshDefinition mesh = HumanoidModel.createMesh(scale, 0);
        PartDefinition root = mesh.getRoot();

        PartDefinition headPart = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition hatPart = headPart.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale.extend(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        hatPart.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(0, 57)
                .addBox(-4.0F, -4.0F, -1.0F, 8.0F, 5.0F, 2.0F,
                        new CubeDeformation(deform - 0.05F)), PartPose.offsetAndRotation(0.0F, -3.65F, 4.9F, -0.3927F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(deform)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(deform)), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
