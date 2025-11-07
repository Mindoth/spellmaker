package net.mindoth.spellmaker.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.Set;
import java.util.function.Function;

public class SimpleRobeModel extends HumanoidModel {

    //TODO: Armor "breathes" on an armorstand
    public SimpleRobeModel(ModelPart root) {
        super(root);
    }

    public static ArmorModelSet<LayerDefinition> SIMPLE_ROBE_ARMOR_LAYER =
            SimpleRobeModel.createArmorSet().map(mesh -> LayerDefinition.create(mesh, 64, 64));

    public static LayerDefinition createLayerByType(ArmorType type) {
        if ( type == ArmorType.HELMET ) return createHeadLayer();
        if ( type == ArmorType.CHESTPLATE ) return createBodyLayer();
        if ( type == ArmorType.LEGGINGS ) return createLegsLayer();
        if ( type == ArmorType.BOOTS ) return createBootsLayer();
        return null;
    }

    public static LayerDefinition createHeadLayer() {
        return SIMPLE_ROBE_ARMOR_LAYER.head();
    }

    public static LayerDefinition createBodyLayer() {
        return SIMPLE_ROBE_ARMOR_LAYER.chest();
    }

    public static LayerDefinition createLegsLayer() {
        return SIMPLE_ROBE_ARMOR_LAYER.legs();
    }

    public static LayerDefinition createBootsLayer() {
        return SIMPLE_ROBE_ARMOR_LAYER.feet();
    }

    public static ArmorModelSet<MeshDefinition> createArmorSet() {
        return createArmorSet(SimpleRobeModel::createBaseArmor);
    }

    public static ArmorModelSet<MeshDefinition> createArmorSet(Function<CubeDeformation, MeshDefinition> base) {
        MeshDefinition head = base.apply(new CubeDeformation(1.0F));
        head.getRoot().retainPartsAndChildren(Set.of("head"));
        MeshDefinition body = base.apply(new CubeDeformation(1.0F));
        body.getRoot().retainExactParts(Set.of("body", "left_arm", "right_arm"));
        MeshDefinition legs = base.apply(new CubeDeformation(1.0F));
        legs.getRoot().retainExactParts(Set.of("left_leg", "right_leg", "body"));
        MeshDefinition boots = base.apply(new CubeDeformation(1.0F));
        boots.getRoot().retainExactParts(Set.of("left_leg", "right_leg"));
        return new ArmorModelSet(head, body, legs, boots);
    }

    public static MeshDefinition createBaseArmor(CubeDeformation scale) {
        float deform = 1.0F;
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

        return mesh;
        //return LayerDefinition.create(mesh, 64, 64);
    }
}
