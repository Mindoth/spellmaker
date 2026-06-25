package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.client.gui.menu.AlembicMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AlembicScreen extends AbstractAlembicScreen<AlembicMenu> {

    public AlembicScreen(AlembicMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
