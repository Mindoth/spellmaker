package net.mindoth.spellmaker.item.armor;

public interface AdaptiveModelArmor {

    default ModArmorItem hoodItem() {
        return null;
    }
}
