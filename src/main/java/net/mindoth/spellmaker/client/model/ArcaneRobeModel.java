package net.mindoth.spellmaker.client.model;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class ArcaneRobeModel extends HumanoidArmorModel<LivingEntity> {

    public ArcaneRobeModel(ModelPart part) {
        super(part);
    }

    public static LayerDefinition createBodyLayer(boolean isHood) {
        float deform = 1.0F;
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(deform), 0);
        PartDefinition root = mesh.getRoot();

        if ( isHood ) {
            PartDefinition bb_main = root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

            bb_main.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(44, 40)
                    .addBox(-4.0F, -4.0F, -1.0F, 8.0F, 5.0F, 2.0F,
                            new CubeDeformation(deform - 0.05F)), PartPose.offsetAndRotation(0.0F, -3.65F, 4.9F, -0.3927F, 0.0F, 0.0F));
        }
        else {
            PartDefinition bb_main = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                            .addBox(-4.0F, -8.0F, -4.0F, 0, 0, 0, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 0.0F, 0.0F));

            bb_main.addOrReplaceChild("bot", CubeListBuilder.create().texOffs(0, 47)
                    .addBox(-8.0F, -30.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 35).addBox(-4.0F, -34.5F, -4.0F, 8.0F, 4.0F, 8.0F,
                            new CubeDeformation(0.51F)), PartPose.offset(0.0F, 24.0F, 0.0F));

            bb_main.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 53)
                    .addBox(-2.0F, -4.0F, -2.0F, 4.0F, 6.0F, 4.0F,
                            new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -13.0F, 0.25F, -0.1745F, 0.0F, 0.0F));
        }

        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(deform)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(deform)), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
