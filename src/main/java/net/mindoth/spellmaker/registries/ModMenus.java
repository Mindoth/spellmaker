package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.CalcinatorMenu;
import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SpellMaker.MOD_ID);

    public static final Supplier<MenuType<CalcinatorMenu>> CALCINATOR_MENU = registerMenuType("calcinator_menu", CalcinatorMenu::new);
    public static final Supplier<MenuType<SpellMakingMenu>> SPELL_MAKING_MENU = registerMenuType("spell_making_menu", SpellMakingMenu::new);

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }
}
