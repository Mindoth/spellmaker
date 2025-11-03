package net.mindoth.spellmaker.block.entity;

import net.mindoth.spellmaker.client.gui.menu.CalcinatorMenu;
import net.mindoth.spellmaker.recipe.CalcinatingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CalcinatorBlockEntity extends AbstractFurnaceBlockEntity {

    public CalcinatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CALCINATOR_BLOCK_ENTITY.get(), pPos, pBlockState, CalcinatingRecipe.Type.CALCINATING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.spellmaker.calcinator");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pId, Inventory pPlayer) {
        return new CalcinatorMenu(pId, pPlayer, this, this.dataAccess);
    }
}
