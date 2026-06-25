package net.mindoth.spellmaker.block;

import com.mojang.serialization.MapCodec;
import net.mindoth.spellmaker.block.entity.AlembicBlockEntity;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AlembicBlock extends BaseEntityBlock {
    
    public static final MapCodec<AlembicBlock> CODEC = simpleCodec(AlembicBlock::new);

    public AlembicBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AlembicBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if ( !pLevel.isClientSide() ) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if ( entity instanceof AlembicBlockEntity alembicBlockEntity && pPlayer instanceof ServerPlayer player ) {
                player.openMenu(new SimpleMenuProvider(alembicBlockEntity, Component.translatable("container.spellmaker.alembic")), pPos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if ( level.isClientSide() ) return null;
        return createTickerHelper(blockEntityType, ModBlocks.ALEMBIC_BLOCK_ENTITY.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));
    }
}
