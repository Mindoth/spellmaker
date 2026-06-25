package net.mindoth.spellmaker.block.entity;

import net.mindoth.spellmaker.client.gui.menu.AlembicMenu;
import net.mindoth.spellmaker.recipe.AlembicRecipe;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("removal")
public class AlembicBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if ( !getLevel().isClientSide() ) {
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public AlembicBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.ALEMBIC_BLOCK_ENTITY.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> AlembicBlockEntity.this.progress;
                    case 1 -> AlembicBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: AlembicBlockEntity.this.progress = value;
                    case 1: AlembicBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.spellmaker.alembic");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new AlembicMenu(i, inventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for ( int i = 0; i < this.itemHandler.getSlots(); i++ ) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        this.itemHandler.serialize(output);
        output.putInt("alembic.progress", this.progress);
        output.putInt("alembic.max_progress", this.maxProgress);
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemHandler.deserialize(input);
        this.progress = input.getIntOr("alembic.progress", 0);
        this.maxProgress = input.getIntOr("alembic.max_progress", 0);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if ( hasRecipe() ) {
            increaseCraftingProgress();
            setChanged(level, blockPos, blockState);
            if ( hasCraftingFinished() ) {
                craftItem();
                resetProgress();
            }
        }
        else {
            resetProgress();
        }
    }

    private static final int INPUT_SLOT_0 = 0;
    private static final int INPUT_SLOT_1 = 1;
    private static final int OUTPUT_SLOT_0 = 1; //Change to 2.
    private static final int OUTPUT_SLOT_1 = 3;
    private static final int OUTPUT_SLOT_2 = 4;
    private static final int OUTPUT_SLOT_3 = 5;
    private static final int FUEL_SLOT = 6;

    private void craftItem() {
        /*
        Optional<RecipeHolder<AlembicRecipe>> recipe = getCurrentRecipe();
        ItemStack output = recipe.get().value().result().create();
        */
        ItemStack output = new ItemStack(Items.DIAMOND);

        this.itemHandler.extractItem(INPUT_SLOT_0, 1, false);
        //this.itemHandler.extractItem(INPUT_SLOT_1, 1, false);
        this.itemHandler.setStackInSlot(OUTPUT_SLOT_0, new ItemStack(output.getItem(), this.itemHandler.getStackInSlot(OUTPUT_SLOT_0).getCount() + output.getCount()));
        /*
        this.itemHandler.setStackInSlot(OUTPUT_SLOT_1, new ItemStack(output.getItem(), this.itemHandler.getStackInSlot(OUTPUT_SLOT_1).getCount() + output.getCount()));
        this.itemHandler.setStackInSlot(OUTPUT_SLOT_2, new ItemStack(output.getItem(), this.itemHandler.getStackInSlot(OUTPUT_SLOT_2).getCount() + output.getCount()));
        this.itemHandler.setStackInSlot(OUTPUT_SLOT_3, new ItemStack(output.getItem(), this.itemHandler.getStackInSlot(OUTPUT_SLOT_3).getCount() + output.getCount()));
        */
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 72;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        ItemStack output = new ItemStack(Items.DIAMOND);
        return this.itemHandler.getStackInSlot(INPUT_SLOT_0).getItem() == Items.DIRT &&
                canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    /*
    private boolean hasRecipe() {
        Optional<RecipeHolder<AlembicRecipe>> recipe = getCurrentRecipe();
        if ( recipe.isEmpty() ) {
            return false;
        }
        ItemStack output = recipe.get().value().result().create();
        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeHolder<AlembicRecipe>> getCurrentRecipe() {
        return ((ServerLevel)this.level).recipeAccess()
                .getRecipeFor(AlembicRecipe.Type.DISTILLING, new AlembicRecipeInput(this.itemHandler.getStackInSlot(INPUT_SLOT_0)), level);
    }
    */

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT_0).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT_0).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        ItemStack stack = this.itemHandler.getStackInSlot(OUTPUT_SLOT_0);
        int maxCount = stack.isEmpty() ? 64 : stack.getMaxStackSize();
        int currentCount = stack.getCount();
        return maxCount >= currentCount + count;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
