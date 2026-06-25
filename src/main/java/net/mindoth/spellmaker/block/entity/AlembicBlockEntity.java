package net.mindoth.spellmaker.block.entity;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.block.AlembicBlock;
import net.mindoth.spellmaker.client.gui.menu.AlembicMenu;
import net.mindoth.spellmaker.recipe.AlembicRecipe;
import net.mindoth.spellmaker.recipe.AlembicRecipeInput;
import net.mindoth.spellmaker.registries.ModBlocks;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("removal")
public class AlembicBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if ( !getLevel().isClientSide() ) {
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final int defaultCookingTime = 200;

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        boolean changed = false;
        boolean isLit;
        boolean wasLit;
        if ( this.litTimeRemaining > 0 ) {
            wasLit = true;
            --this.litTimeRemaining;
            isLit = this.litTimeRemaining > 0;
        }
        else {
            wasLit = false;
            isLit = false;
        }
        ItemStack fuel = this.itemHandler.getStackInSlot(FUEL_SLOT);
        boolean hasFuel = !fuel.isEmpty();
        boolean hasRecipe = hasRecipe();
        if ( isLit || hasFuel && hasRecipe ) {
            if ( hasRecipe() ) {
                if ( !isLit ) {
                    int newLitTime = getBurnDuration(level.fuelValues(), fuel);
                    this.litTimeRemaining = newLitTime;
                    this.litTotalTime = newLitTime;
                    if ( newLitTime > 0 ) {
                        consumeFuel(FUEL_SLOT, fuel);
                        isLit = true;
                        changed = true;
                    }
                }
                if ( isLit ) {
                    ++this.cookingTimer;
                    if ( this.cookingTimer >= this.cookingTotalTime ) {
                        resetProgress();
                        this.cookingTotalTime = defaultCookingTime;
                        craftItem();
                        changed = true;
                    }
                }
                else resetProgress();
            }
            else resetProgress();
        }
        else if ( this.cookingTimer > 0 ) resetProgress();
        if ( wasLit != isLit ) {
            changed = true;
            blockState = blockState.setValue(AlembicBlock.LIT, isLit);
            level.setBlock(blockPos, blockState, 3);
        }
        if ( changed ) setChanged(level, blockPos, blockState);
    }

    private void resetProgress() {
        this.cookingTimer = 0;
    }

    private int getBurnDuration(FuelValues fuelValues, ItemStack itemStack) {
        return itemStack.getBurnTime(AlembicRecipe.Type.DISTILLING, fuelValues);
    }

    private void consumeFuel(int slot, ItemStack fuel) {
        ItemStackTemplate remainder = fuel.getCraftingRemainder();
        fuel.shrink(1);
        if ( fuel.isEmpty() ) this.itemHandler.setStackInSlot(slot, remainder != null ? remainder.create() : ItemStack.EMPTY);
    }

    private static final int INPUT_SLOT_0 = 0;
    private static final int INPUT_SLOT_1 = 1;
    private static final int OUTPUT_SLOT_0 = 2;
    private static final int OUTPUT_SLOT_1 = 3;
    private static final int OUTPUT_SLOT_2 = 4;
    private static final int OUTPUT_SLOT_3 = 5;
    private static final int FUEL_SLOT = 6;

    private void craftItem() {
        Optional<RecipeHolder<AlembicRecipe>> recipeOptional = getCurrentRecipe();
        AlembicRecipe recipe = recipeOptional.get().value();
        Item stackItem0 = this.itemHandler.getStackInSlot(0).getItem();
        Item stackItem1 = this.itemHandler.getStackInSlot(1).getItem();
        Item recipeItem0 = recipe.getInput0().getValues().get(0).value();
        Item recipeItem1 = recipe.getInput1().getValues().get(0).value();
        int input0Amount = recipe.getInput0Amount();
        int input1Amount = recipe.getInput1Amount();
        if ( stackItem0 == recipeItem0 && stackItem1 == recipeItem1 ) {
            this.itemHandler.extractItem(INPUT_SLOT_0, input0Amount, false);
            this.itemHandler.extractItem(INPUT_SLOT_1, input1Amount, false);
        }
        else {
            this.itemHandler.extractItem(INPUT_SLOT_0, input1Amount, false);
            this.itemHandler.extractItem(INPUT_SLOT_1, input0Amount, false);
        }
        setOutputSlot(OUTPUT_SLOT_0, recipe.getResult0().create());
        if ( recipe.getResult1().isPresent() ) setOutputSlot(OUTPUT_SLOT_1, recipe.getResult1().get().create());
        if ( recipe.getResult2().isPresent() ) setOutputSlot(OUTPUT_SLOT_2, recipe.getResult2().get().create());
        if ( recipe.getResult3().isPresent() ) setOutputSlot(OUTPUT_SLOT_3, recipe.getResult3().get().create());
    }

    private void setOutputSlot(int slot, ItemStack output) {
        if ( !output.isEmpty() ) this.itemHandler.setStackInSlot(slot, new ItemStack(output.getItem(), this.itemHandler.getStackInSlot(slot).getCount() + output.getCount()));
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<AlembicRecipe>> recipe = getCurrentRecipe();
        if ( recipe.isEmpty() ) return false;

        boolean slot0 = isFitting(OUTPUT_SLOT_0, recipe.get().value().getResult0().create());
        boolean slot1 = recipe.get().value().getResult1().isEmpty() || isFitting(OUTPUT_SLOT_1, recipe.get().value().getResult1().get().create());
        boolean slot2 = recipe.get().value().getResult2().isEmpty() || isFitting(OUTPUT_SLOT_2, recipe.get().value().getResult2().get().create());
        boolean slot3 = recipe.get().value().getResult3().isEmpty() || isFitting(OUTPUT_SLOT_3, recipe.get().value().getResult3().get().create());

        return slot0 && slot1 && slot2 && slot3;
    }

    private boolean isFitting(int slot, ItemStack output) {
        return output.isEmpty() || (canInsertAmountIntoOutputSlot(slot, output) && canInsertItemIntoOutputSlot(slot, output));
    }

    private Optional<RecipeHolder<AlembicRecipe>> getCurrentRecipe() {
        List<ItemStack> inputs = Lists.newArrayList();
        inputs.add(this.itemHandler.getStackInSlot(INPUT_SLOT_0));
        inputs.add(this.itemHandler.getStackInSlot(INPUT_SLOT_1));
        return ((ServerLevel)this.getLevel()).recipeAccess()
                .getRecipeFor(AlembicRecipe.Type.DISTILLING, new AlembicRecipeInput(inputs), getLevel());
    }

    private boolean canInsertItemIntoOutputSlot(int slot, ItemStack output) {
        return this.itemHandler.getStackInSlot(slot).isEmpty() || this.itemHandler.getStackInSlot(slot).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int slot, ItemStack output) {
        ItemStack stack = this.itemHandler.getStackInSlot(slot);
        int maxCount = stack.isEmpty() ? 64 : stack.getMaxStackSize();
        int currentCount = stack.getCount();
        return maxCount >= currentCount + output.getCount();
    }

    protected final ContainerData data;
    private int litTimeRemaining = 0;
    private int litTotalTime = 0;
    private int cookingTimer = 0;
    private int cookingTotalTime = defaultCookingTime;

    public AlembicBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.ALEMBIC_BLOCK_ENTITY.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> AlembicBlockEntity.this.litTimeRemaining;
                    case 1 -> AlembicBlockEntity.this.litTotalTime;
                    case 2 -> AlembicBlockEntity.this.cookingTimer;
                    case 3 -> AlembicBlockEntity.this.cookingTotalTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: AlembicBlockEntity.this.litTimeRemaining = value;
                    case 1: AlembicBlockEntity.this.litTotalTime = value;
                    case 2: AlembicBlockEntity.this.cookingTimer = value;
                    case 3: AlembicBlockEntity.this.cookingTotalTime = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
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
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        drops();
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        this.itemHandler.serialize(output);
        output.putInt("alembic.lit_time_remaining", this.litTimeRemaining);
        output.putInt("alembic.lit_total_time", this.litTotalTime);
        output.putInt("alembic.cooking_time_spent", this.cookingTimer);
        output.putInt("alembic.cooking_total_time", this.cookingTotalTime);
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemHandler.deserialize(input);
        this.litTimeRemaining = input.getIntOr("alembic.lit_time_remaining", 0);
        this.litTotalTime = input.getIntOr("alembic.lit_total_time", 0);
        this.cookingTimer = input.getIntOr("alembic.cooking_time_spent", 0);
        this.cookingTotalTime = input.getIntOr("alembic.cooking_total_time", 0);
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
