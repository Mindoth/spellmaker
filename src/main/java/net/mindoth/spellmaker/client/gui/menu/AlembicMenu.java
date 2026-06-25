package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.registries.ModMenus;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AlembicMenu extends AbstractAlembicMenu {

    public AlembicMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenus.ALEMBIC_MENU.get(), ModRecipes.ALEMBIC_INPUT_0, ModRecipes.ALEMBIC_INPUT_1, containerId, inventory, buf);
    }

    public AlembicMenu(int containerId, Inventory inventory, BlockEntity container, ContainerData data) {
        super(ModMenus.ALEMBIC_MENU.get(), ModRecipes.ALEMBIC_INPUT_0, ModRecipes.ALEMBIC_INPUT_1, containerId, inventory, container, data);
    }
}
