package net.mindoth.spellmaker.item.sigil;

import com.mojang.authlib.GameProfile;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;
import java.util.UUID;

public class ExcavationSigilItem extends SigilItem {
    public ExcavationSigilItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, maxMagnitude, magnitudeMultiplier, maxDuration, durationMultiplier);
    }

    @Override
    public void effectOnAllBlocksInList(BlockPos target, List<Integer> stats, DimVec3 location, Direction direction, boolean isInside) {
        int magnitude = stats.get(0);
        if ( magnitude > 0 ) handleMine(target, location.getLevel(), magnitude);
    }

    private static final GameProfile FAKE_PROFILE = new GameProfile(UUID.fromString("fdc17a6f-5d46-484e-9343-820f43c7b101"), "am_fake_player_profile");

    private void handleMine(BlockPos pos, Level dimension, int power) {
        if ( dimension instanceof ServerLevel serverLevel ) {
            FakePlayer fakePlayer = FakePlayerFactory.get(serverLevel, FAKE_PROFILE);
            Block block = serverLevel.getBlockState(pos).getBlock();
            BlockState blockState = serverLevel.getBlockState(pos);
            fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, getToolFromStrength(power));
            if ( block.canHarvestBlock(blockState, serverLevel, pos, fakePlayer) && block.defaultDestroyTime() >= 0 && !blockState.isAir() ) {
                serverLevel.destroyBlock(pos, true, fakePlayer);
            }
        }
    }

    private ItemStack getToolFromStrength(int power) {
        if ( power == 1 ) return new ItemStack(Items.WOODEN_PICKAXE);
        else if ( power == 2 ) return new ItemStack(Items.STONE_PICKAXE);
        else if ( power == 3 ) return new ItemStack(Items.IRON_PICKAXE);
        else return new ItemStack(Items.DIAMOND_PICKAXE);
    }
}
