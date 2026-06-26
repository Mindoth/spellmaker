package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.block.entity.AlembicBlockEntity;
import net.mindoth.spellmaker.recipe.DistillingRecipe;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

@SuppressWarnings("removal")
public abstract class AbstractAlembicMenu extends AbstractContainerMenu {

    public final AlembicBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public AbstractAlembicMenu(MenuType<?> menuType, ResourceKey<RecipePropertySet> acceptedInputs0, ResourceKey<RecipePropertySet> acceptedInputs1, int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(menuType, acceptedInputs0, acceptedInputs1, containerId, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(7));
    }

    private final RecipePropertySet acceptedInputs0;
    private final RecipePropertySet acceptedInputs1;

    public AbstractAlembicMenu(MenuType<?> menuType, ResourceKey<RecipePropertySet> acceptedInputs0, ResourceKey<RecipePropertySet> acceptedInputs1, int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(menuType, pContainerId);
        this.blockEntity = ((AlembicBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        this.acceptedInputs0 = this.level.recipeAccess().propertySet(acceptedInputs0);
        this.acceptedInputs1 = this.level.recipeAccess().propertySet(acceptedInputs1);

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        int INPUT_Y = 17;
        int OUTPUT_ROW_UPPER = 26;
        int OUTPUT_COLUMN_LEFT = 106;
        int OUTPUT_ROW_LOWER = 44;
        int OUTPUT_COLUMN_RIGHT = 124;
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 47, INPUT_Y));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 1, 65, INPUT_Y));
        this.addSlot(new AlembicResultSlot(this.blockEntity.itemHandler, 2, OUTPUT_COLUMN_LEFT, OUTPUT_ROW_UPPER));
        this.addSlot(new AlembicResultSlot(this.blockEntity.itemHandler, 3, OUTPUT_COLUMN_RIGHT, OUTPUT_ROW_UPPER));
        this.addSlot(new AlembicResultSlot(this.blockEntity.itemHandler, 4, OUTPUT_COLUMN_LEFT, OUTPUT_ROW_LOWER));
        this.addSlot(new AlembicResultSlot(this.blockEntity.itemHandler, 5, OUTPUT_COLUMN_RIGHT, OUTPUT_ROW_LOWER));
        this.addSlot(new AlembicFuelSlot(this, this.blockEntity.itemHandler, 6, 56, 53));

        addDataSlots(data);
    }

    protected boolean canDistill(ItemStack itemStack) {
        return this.acceptedInputs0.test(itemStack) || this.acceptedInputs1.test(itemStack);
    }

    protected boolean isFuel(ItemStack itemStack) {
        return itemStack.getBurnTime(ModRecipes.DISTILLING_RECIPE_TYPE.get(), this.level.fuelValues()) > 0;
    }

    public boolean isCrafting() {
        return data.get(2) > 0;
    }

    public float getBurnProgress() {
        int current = this.data.get(2);
        int total = this.data.get(3);
        return total != 0 && current != 0 ? Mth.clamp((float)current / (float)total, 0.0F, 1.0F) : 0.0F;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    public float getLitProgress() {
        int litDuration = this.data.get(1);
        if ( litDuration == 0 ) litDuration = 200;

        return Mth.clamp((float)this.data.get(0) / (float)litDuration, 0.0F, 1.0F);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int CUSTOM_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int CUSTOM_INVENTORY_SLOT_COUNT = 7;
    private static final int CUSTOM_INVENTORY_LAST_SLOT_INDEX = CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT - 1;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int slotIndex) {
        Slot sourceSlot = slots.get(slotIndex);
        if ( sourceSlot == null || !sourceSlot.hasItem() ) return ItemStack.EMPTY;
        ItemStack stack = sourceSlot.getItem();
        ItemStack copy = stack.copy();

        if ( slotIndex >= 38 && slotIndex < 42 ) {
            if ( !moveItemStackTo(stack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, true) ) {
                return ItemStack.EMPTY;
            }
            sourceSlot.onQuickCraft(stack, copy);
        }
        else if ( slotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT ) {
            if ( canDistill(stack) ) {
                if ( !moveItemStackTo(stack, CUSTOM_INVENTORY_FIRST_SLOT_INDEX, CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT, false) ) {
                    return ItemStack.EMPTY;
                }
            }
            else if ( isFuel(stack) ) {
                if ( !moveItemStackTo(stack, CUSTOM_INVENTORY_LAST_SLOT_INDEX, CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT, false) ) {
                    return ItemStack.EMPTY;
                }
            }
            else if ( slotIndex >= VANILLA_FIRST_SLOT_INDEX && slotIndex < PLAYER_INVENTORY_SLOT_COUNT ) {
                if ( !moveItemStackTo(stack, PLAYER_INVENTORY_SLOT_COUNT, VANILLA_SLOT_COUNT, false) ) {
                    return ItemStack.EMPTY;
                }
            }
            else if ( slotIndex >= PLAYER_INVENTORY_SLOT_COUNT && slotIndex < VANILLA_SLOT_COUNT ) {
                if ( !moveItemStackTo(stack, VANILLA_FIRST_SLOT_INDEX, PLAYER_INVENTORY_SLOT_COUNT, false) ) {
                    return ItemStack.EMPTY;
                }
            }
        }
        else if ( slotIndex < CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT) {
            if ( !moveItemStackTo(stack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false) ) {
                return ItemStack.EMPTY;
            }
        }
        else {
            System.out.println("Invalid slotIndex:" + slotIndex);
            return ItemStack.EMPTY;
        }
        if ( stack.getCount() == 0 ) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();
        sourceSlot.onTake(playerIn, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, this.blockEntity.getBlockPos()), pPlayer, ModBlocks.ALEMBIC.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for ( int i = 0; i < 3; ++i ) {
            for ( int l = 0; l < 9; ++l ) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for ( int i = 0; i < 9; ++i ) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
