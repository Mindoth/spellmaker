package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.client.gui.menu.CalcinatorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

//@OnlyIn(Dist.CLIENT)
public class CalcinatorScreen extends AbstractCalcinatorScreen<CalcinatorMenu> {

    public CalcinatorScreen(CalcinatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
}
