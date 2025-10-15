package net.mindoth.spellmaker.block;

import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class SpellMakingTableBlock extends Block {

    private static final Component CONTAINER_TITLE = Component.translatable("container.spellmaker.spell_making");

    public SpellMakingTableBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if ( level.isClientSide || !(player instanceof ServerPlayer serverPlayer) ) return InteractionResult.SUCCESS;
        else {
            NetworkHooks.openScreen(serverPlayer, getMenuProvider(level, pos), pos);
            return InteractionResult.CONSUME;
        }
    }

    public static MenuProvider getMenuProvider(Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, access) -> new SpellMakingMenu(id, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }
}
