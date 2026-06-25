package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.block.entity.AlembicBlockEntity;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

@SuppressWarnings("removal")
public class AlembicMenu extends AbstractContainerMenu {
    
    public final AlembicBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public AlembicMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public AlembicMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenus.ALEMBIC_MENU.get(), pContainerId);
        this.blockEntity = ((AlembicBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        int INPUT_Y = 17;
        int OUTPUT_ROW_UPPER = 26;
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 47, INPUT_Y));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 1, 65, INPUT_Y));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 2, 106, OUTPUT_ROW_UPPER));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 3, 56, 53)); //Change index to 6.

        addDataSlots(data);
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
    private static final int CUSTOM_INVENTORY_SLOT_COUNT = 4;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if ( sourceSlot == null || !sourceSlot.hasItem() ) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if ( pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT ) {
            if ( !moveItemStackTo(sourceStack, CUSTOM_INVENTORY_FIRST_SLOT_INDEX, CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT, false) ) {
                return ItemStack.EMPTY;
            }
        }
        else if ( pIndex < CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT) {
            if ( !moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false) ) {
                return ItemStack.EMPTY;
            }
        }
        else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        if ( sourceStack.getCount() == 0 ) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
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
