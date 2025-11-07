package net.mindoth.spellmaker.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class ModItemExtension implements IClientItemExtensions {

    public static final ModItemExtension INSTANCE = new ModItemExtension();

    private ModItemExtension() {
    }

    @Override
    @NotNull
    public Model getHumanoidArmorModel(@NotNull ItemStack stack, @NotNull EquipmentClientInfo.LayerType layerType, @NotNull Model original) {
        /*if ( original instanceof HumanoidModel<?> humanoidModel ) {
            ClientHooks.copyModelProperties(humanoidModel, ModLayerEvents.SIMPLE_ROBE_MODEL);
        }
        return ModLayerEvents.SIMPLE_ROBE_MODEL;*/
        //return new SimpleRobeModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModLayerEvents.SIMPLE_ROBE_LAYER));
        return ModLayerEvents.SIMPLE_ROBE_MODEL != null ? ModLayerEvents.SIMPLE_ROBE_MODEL : original;
    }
}
