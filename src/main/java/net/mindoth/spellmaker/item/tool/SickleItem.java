package net.mindoth.spellmaker.item.tool;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.List;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class SickleItem extends Item {

    public SickleItem(Properties properties) {
        super(properties);
    }

    @SubscribeEvent
    public static void onBlockBreak(final BreakBlockEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();
        if ( level.isClientSide() ) return;
        if ( player.getAbilities().instabuild ) return;
        if ( !(player.getMainHandItem().getItem() instanceof SickleItem) ) return;
        BlockState blockState = event.getState();
        Vec3 playerPos = player.getBoundingBox().getCenter();
        BlockPos blockPos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        Block block = blockState.getBlock();
        if ( !(block instanceof VegetationBlock) ) return;
        if ( level instanceof ServerLevel serverLevel ) {
            List<ItemStack> drops = Block.getDrops(blockState, serverLevel, blockPos, blockEntity, player, player.getMainHandItem());
            for ( ItemStack itemStack : drops ) {
                if ( !player.getInventory().add(itemStack) ) {
                    ItemEntity drop = new ItemEntity(level, playerPos.x, playerPos.y, playerPos.z, itemStack);
                    drop.setDeltaMovement(0, 0, 0);
                    drop.setNoPickUpDelay();
                    level.addFreshEntity(drop);
                }
            }
        }
        level.removeBlock(blockPos, false);

        if ( level instanceof ServerLevel serverLevel ) {
            ItemStack stack = player.getMainHandItem();
            stack.hurtAndBreak(1, serverLevel, player,
                    (holder) -> player.onEquippedItemBroken(stack.getItem(), player.getEquipmentSlotForItem(stack)));
        }
    }
}
