package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.registries.ModRecipes;
import net.mindoth.spellmaker.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;

public class CalcinatorMenu extends AbstractFurnaceMenu {

    public CalcinatorMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenus.CALCINATOR_MENU.get(), ModRecipes.CALCINATOR_INPUT, RecipeBookType.FURNACE, containerId, inventory);
    }

    public CalcinatorMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.CALCINATOR_MENU.get(), ModRecipes.CALCINATOR_INPUT, RecipeBookType.FURNACE, containerId, inventory, container, data);
    }
}
