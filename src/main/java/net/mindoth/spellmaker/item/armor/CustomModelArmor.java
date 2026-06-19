package net.mindoth.spellmaker.item.armor;

import net.minecraft.client.model.HumanoidModel;

public interface CustomModelArmor {
    default HumanoidModel<?> getCustomArmorModel() {
        return null;
    }
}
