package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.SpellMakerClient;
import net.mindoth.spellmaker.client.model.WoolRobeModel;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class WoolRobeItem extends ColorableArmorItem {
    public WoolRobeItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties, int defaultColor, AttributeContainer... attributes) {
        super(pMaterial, pType, pProperties, defaultColor, attributes);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public HumanoidModel<?> provideArmorModelForSlot() {
        HumanoidModel<?> model;
        if ( this == ModItems.WOOL_ROBE_HOOD.get() ) model = new WoolRobeModel(WoolRobeModel.createBodyLayer(true).bakeRoot());
        else if ( this == ModItems.WOOL_ROBE_HAT.get() ) model = new WoolRobeModel(WoolRobeModel.createBodyLayer(false).bakeRoot());
        else model = new WoolRobeModel(Minecraft.getInstance().getEntityModels().bakeLayer(SpellMakerClient.WOOL_ROBE));
        return model;
    }
}
