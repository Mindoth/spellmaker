package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModMenus;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SpellMakingMenu extends AbstractContainerMenu {

    private static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 0;
    private static final int CRAFT_SLOT_END = 3;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;

    private static final int TOP_ROW_HEIGHT = 39;
    public int getTopRowHeight() {
        return TOP_ROW_HEIGHT;
    }
    private static final int BOTTOM_ROW_HEIGHT = 52 + 9;
    public int getBottomRowHeight() {
        return BOTTOM_ROW_HEIGHT;
    }

    private final Container craftSlots = new SimpleContainer(4) {
        @Override
        public void setChanged() {
            super.setChanged();
            SpellMakingMenu.this.slotsChanged(this);
            SpellMakingMenu.this.broadcastChanges();
        }
    };
    public Container getCraftSlots() {
        return this.craftSlots;
    }

    private final ContainerLevelAccess access;
    private final Player player;

    public SpellMakingMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(containerId, inventory, ContainerLevelAccess.create(inventory.player.level(), buf.readBlockPos()));
    }

    public SpellMakingMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.SPELL_MAKING_MENU.get(), containerId);
        this.access = access;
        this.player = playerInventory.player;

        //Parchment Slot
        this.addSlot(new ParchmentSlot(this.craftSlots, 0, 12, TOP_ROW_HEIGHT));

        //Rune slots
        for ( int i = 0; i < 3; ++i ) {
            this.addSlot(new RuneSlot(this.craftSlots, 1 + i, 72 + i * 36, TOP_ROW_HEIGHT, !this.craftSlots.getItem(0).isEmpty()));
        }

        //Player inventory
        for ( int i = 0; i < 3; ++i ) {
            for ( int j = 0; j < 9; ++j ) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 68 + BOTTOM_ROW_HEIGHT + i * 18));
            }
        }

        //Player hotbar
        for ( int i = 0; i < 9; ++i ) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 126 + BOTTOM_ROW_HEIGHT));
        }

        dataInit();
    }

    private ModSpellForms.SpellForm spellForm;
    public ModSpellForms.SpellForm getSpellForm() {
        return this.spellForm;
    }
    public void setSpellForm(ModSpellForms.SpellForm form) {
        this.spellForm = form;
    }
    private int magnitude;
    public int getMagnitude() {
        return this.magnitude;
    }
    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }
    private int duration;
    public int getDuration() {
        return this.duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    private int area;
    public int getArea() {
        return this.area;
    }
    public void setArea(int area) {
        this.area = area;
    }

    public void dataInit() {
        setSpellForm(ModSpellForms.CASTER_ONLY.get());
        setMagnitude(1);
        setDuration(1);
        setArea(1);
    }

    public boolean isCleanParchment(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ParchmentItem && !stack.hasTag();
        //return !stack.isEmpty() && stack.getItem() instanceof ParchmentItem && (!stack.hasTag() || !stack.getTag().contains(ParchmentItem.NBT_KEY_SPELL_STRING));
    }

    public boolean isReadyToCraft() {
        return isCleanParchment(this.craftSlots.getItem(0));
        //return isCleanParchment(this.craftSlots.getItem(0)) && !assemble(this.craftSlots).isEmpty();
    }

    public boolean craftSpell(String string) {
        return true;
        /*if ( isReadyToCraft() ) {
            ModNetwork.sendToServer(new PacketCraftSpell(getItemName(string)));
            return true;
        }
        else return false;*/
    }

    public boolean isReadyToDump() {
        return !isCleanParchment(this.craftSlots.getItem(0)) && !this.craftSlots.getItem(0).isEmpty();
        /*ItemStack stack = this.craftSlots.getItem(0);
        return !stack.isEmpty() && stack.getItem() instanceof ParchmentItem && stack.hasTag() && stack.getTag().contains(ParchmentItem.NBT_KEY_SPELL_STRING);*/
    }

    public boolean dumpSpell() {
        return true;
        /*if ( isReadyToDump() ) {
            ModNetwork.sendToServer(new PacketDumpSpell());
            return true;
        }
        else return false;*/
    }

    private void setSlotContent(int slot, ItemStack stack) {
        ServerPlayer serverplayer = (ServerPlayer)this.player;
        this.craftSlots.setItem(slot, stack);
        this.setRemoteSlot(slot, stack);
        serverplayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), slot, stack));
    }

    public int howManyRuneSlotsOpen() {
        int count = 0;
        for ( Slot slot : this.slots ) if ( slot instanceof RuneSlot runeSlot && runeSlot.isOpen ) count++;
        return count;
    }

    @Override
    public void slotsChanged(Container pInventory) {
        this.access.execute((level, pos) -> {
            ItemStack stack = this.craftSlots.getItem(0);
            //Placed clean parchment
            if ( isCleanParchment(stack) ) {
                final int slotsToOpen = ((ParchmentItem)stack.getItem()).getSize();
                for ( Slot slot : this.slots ) {
                    if ( howManyRuneSlotsOpen() >= slotsToOpen ) break;
                    if ( slot instanceof RuneSlot runeSlot && !runeSlot.isOpen ) runeSlot.isOpen = true;
                }
            }
            //Removed parchment
            else {
                for ( Slot slot : this.slots ) {
                    if ( slot instanceof RuneSlot runeSlot ) {
                        if ( !level.isClientSide && !runeSlot.getItem().isEmpty() ) {
                            if ( stack.isEmpty() ) quickMoveStack(this.player, runeSlot.index);
                            else setSlotContent(runeSlot.getSlotIndex(), ItemStack.EMPTY);
                        }
                        if ( runeSlot.isOpen ) runeSlot.isOpen = false;
                    }
                }
                dataInit();
            }
            /*if ( level.isClientSide && isReadyToDump() ) {
                List<Item> tempList = ParchmentItem.getScrollComboList(stack);
                for ( int i = 0; i < tempList.size(); i++ ) editColorCode(i, tempList.get(i));
            }*/
        });
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if ( slot != null && slot.hasItem() ) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();
            if ( pIndex >= CRAFT_SLOT_END && pIndex < USE_ROW_SLOT_END ) {
                if ( !this.moveItemStackTo(stack, RESULT_SLOT, CRAFT_SLOT_END, false) ) {
                    if ( pIndex < INV_SLOT_END ) {
                        if ( !this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false) ) return ItemStack.EMPTY;
                    }
                    else if ( !this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false) ) return ItemStack.EMPTY;
                }
            }
            else if ( !this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false) ) return ItemStack.EMPTY;

            if ( stack.isEmpty() ) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();

            if ( stack.getCount() == itemStack.getCount() ) return ItemStack.EMPTY;

            slot.onTake(pPlayer, stack);
            if ( pIndex == RESULT_SLOT ) pPlayer.drop(stack, false);
        }
        return itemStack;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((level, pos) -> this.clearContainer(pPlayer, this.craftSlots));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, ModBlocks.SPELL_MAKING_TABLE.get());
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return !(pSlot instanceof ParchmentSlot) && super.canTakeItemForPickAll(pStack, pSlot);
    }
}
