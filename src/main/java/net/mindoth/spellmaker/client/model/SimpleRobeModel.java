package net.mindoth.spellmaker.client.model;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SimpleRobeModel extends HumanoidArmorModel {

    public SimpleRobeModel(ModelPart part) {
        super(part);
    }

    public static LayerDefinition createBodyLayer() {
        float deform = 1.0F;
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(deform), 0);
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(deform)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(deform)), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }
}
