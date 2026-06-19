package net.mindoth.spellmaker.item.armor;

public interface CustomModelArmor {

    default ModArmorItem hoodItem() {
        return null;
    }
}
