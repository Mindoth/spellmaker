package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.SpellMakerClient;
import net.mindoth.spellmaker.client.model.ArcaneRobeModel;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ArcaneRobeItem extends ColorableArmorItem {
    public ArcaneRobeItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties, int defaultColor, AttributeContainer... attributes) {
        super(pMaterial, pType, pProperties, defaultColor, attributes);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public HumanoidModel<?> provideArmorModelForSlot() {
        HumanoidModel<?> model;
        if ( this == ModItems.ARCANE_ROBE_HOOD.get() ) model = new ArcaneRobeModel(ArcaneRobeModel.createBodyLayer(true).bakeRoot());
        else if ( this == ModItems.ARCANE_ROBE_HAT.get() ) model = new ArcaneRobeModel(ArcaneRobeModel.createBodyLayer(false).bakeRoot());
        else model = new ArcaneRobeModel(Minecraft.getInstance().getEntityModels().bakeLayer(SpellMakerClient.ARCANE_ROBE));
        return model;
    }
}
