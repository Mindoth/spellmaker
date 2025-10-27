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

        PartDefinition bb_main = root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        bb_main.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(0, 57)
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
