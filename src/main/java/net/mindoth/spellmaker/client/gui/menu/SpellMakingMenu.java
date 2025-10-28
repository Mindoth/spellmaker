package net.mindoth.spellmaker.client.gui.menu;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.network.*;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModMenus;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class SpellMakingMenu extends AbstractContainerMenu {

    private static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 0;
    private static final int CRAFT_SLOT_END = 4;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;

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

        //Parchment slot
        this.addSlot(new ParchmentSlot(this.craftSlots, 0, 8, 44));

        //Rune slots
        for ( int i = 0; i < 3; ++i ) {
            this.addSlot(new SigilSlot(this.craftSlots, 1 + i, 35, 62 + i * 18, !this.craftSlots.getItem(0).isEmpty()));
        }

        //Player inventory
        for ( int i = 0; i < 3; ++i ) {
            for ( int j = 0; j < 9; ++j ) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 129 + i * 18));
            }
        }

        //Player hotbar
        for ( int i = 0; i < 9; ++i ) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 187));
        }
        dataInit();
    }

    private List<AbstractSpellForm> formList;
    public List<AbstractSpellForm> getFormList() {
        return this.formList;
    }
    private AbstractSpellForm spellForm;
    public AbstractSpellForm getSpellForm() {
        return this.spellForm;
    }
    private List<Integer> magnitude;
    public List<Integer> getMagnitude() {
        return this.magnitude;
    }
    private List<Integer> duration;
    public List<Integer> getDuration() {
        return this.duration;
    }

    public void dataInit() {
        this.formList = Arrays.asList(ModSpellForms.CASTER_ONLY.get(), ModSpellForms.BY_TOUCH.get(), ModSpellForms.SINGLE_TARGET_AT_RANGE.get(), ModSpellForms.AREA_AROUND_CASTER.get(), ModSpellForms.AREA_AT_RANGE.get());
        this.spellForm = this.formList.get(0);
        this.magnitude = Arrays.asList(0, 0, 0);
        this.duration = Arrays.asList(0, 0, 0);
    }

    public boolean isCleanParchment(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ParchmentItem && (!stack.hasTag() || !stack.getTag().contains(ParchmentItem.NBT_KEY_SPELL_FORM));
    }

    private void cleanScroll(Level level, ItemStack scroll) {
        ItemStack stack = scroll.copy();
        if ( stack.hasCustomHoverName() ) stack.resetHoverName();
        if ( stack.hasTag() ) {
            CompoundTag tag = stack.getTag();
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_FORM) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_FORM);
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_SIGILS) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_SIGILS);
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES);
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_DURATIONS) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_DURATIONS);
            if ( stack.getTag().isEmpty() ) stack.setTag(null);
        }
        setSlotContent(level, 0, stack);
    }

    public boolean isReadyToMake() {
        return isCleanParchment(this.craftSlots.getItem(0)) && !assemble(this.craftSlots).isEmpty();
    }

    public boolean makeSpell(String string) {
        if ( isReadyToMake() ) {
            ModNetwork.sendToServer(new PacketMakeSpell(getItemName(string)));
            return true;
        }
        else return false;
    }

    public void processMaking(String name) {
        this.access.execute((level, pos) -> {
            if ( !level.isClientSide ) {
                if ( isReadyToMake() ) {
                    ItemStack stack = assemble(this.craftSlots);
                    if ( name == null || Util.isBlank(name) ) stack.resetHoverName();
                    else stack.setHoverName(Component.literal(name));
                    setSlotContent(level, 0, stack);
                }
            }
        });
    }

    public boolean isReadyToDump() {
        ItemStack stack = this.craftSlots.getItem(0);
        return !stack.isEmpty() && stack.getItem() instanceof ParchmentItem && stack.hasTag() && stack.getTag().contains(ParchmentItem.NBT_KEY_SPELL_FORM);
    }

    public boolean dumpSpell() {
        if ( isReadyToDump() ) {
            ModNetwork.sendToServer(new PacketDumpSpell());
            return true;
        }
        else return false;
    }

    public void processDumping() {
        this.access.execute((level, pos) -> {
            if ( !level.isClientSide ) {
                if ( isReadyToDump() ) {
                    ItemStack stack = this.craftSlots.getItem(0);
                    List<ItemStack> list = DataHelper.getSpellStackFromTag(stack.getTag());
                    for ( int i = 0; i < this.slots.size(); i++ ) {
                        if ( i == 0 ) {
                            this.spellForm = DataHelper.getFormFromNbt(stack.getTag());
                            this.magnitude = DataHelper.getStatsFromString(stack.getTag().getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES));
                            this.duration = DataHelper.getStatsFromString(stack.getTag().getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS));
                            cleanScroll(level, stack);
                        }
                        else {
                            Slot slot = this.slots.get(i);
                            if ( slot instanceof SigilSlot) {
                                ItemStack sigil = list.get(i - 1);
                                setSlotContent(level, i, sigil);
                            }
                        }
                    }
                }
            }
        });
    }

    public ItemStack assemble(Container container) {
        ItemStack scroll = this.craftSlots.getItem(0).copy();
        List<ItemStack> sigilStackList = Lists.newArrayList();
        List<ItemStack> restList = Lists.newArrayList();
        for ( int i = 1; i < container.getContainerSize(); i++ ) {
            ItemStack stack = container.getItem(i);
            if ( stack.getItem() instanceof SigilItem || stack.isEmpty() ) sigilStackList.add(stack);
            else restList.add(stack);
        }
        if ( restList.isEmpty() ) {
            CompoundTag tag = scroll.getOrCreateTag();
            tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, DataHelper.getStringFromForm(this.spellForm));
            tag.putString(ParchmentItem.NBT_KEY_SPELL_SIGILS, DataHelper.getStringFromSpellStack(sigilStackList));
            tag.putString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES, DataHelper.getStringFromStats(this.magnitude));
            tag.putString(ParchmentItem.NBT_KEY_SPELL_DURATIONS, DataHelper.getStringFromStats(this.duration));
            return scroll;
        }
        return ItemStack.EMPTY;
    }

    private String getItemName(String string) {
        return SharedConstants.filterText(string).length() <= 50 ? SharedConstants.filterText(string) : null;
    }

    private void setSlotContent(Level level, int slot, ItemStack stack) {
        if ( !level.isClientSide ) {
            ServerPlayer serverplayer = (ServerPlayer)this.player;
            this.craftSlots.setItem(slot, stack);
            this.setRemoteSlot(slot, stack);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), slot, stack));
        }
    }

    public int howManySigilSlotsOpen() {
        int count = 0;
        for ( Slot slot : this.slots ) if ( slot instanceof SigilSlot sigilSlot && sigilSlot.isOpen ) count++;
        return count;
    }

    //TODO: Make stats reset for a row when a rune is removed
    @Override
    public void slotsChanged(Container pInventory) {
        this.access.execute((level, pos) -> {
            ItemStack stack = getCraftSlots().getItem(0);
            if ( isCleanParchment(stack) ) {
                final int slotsToOpen = ((ParchmentItem)stack.getItem()).getSize();
                for ( Slot slot : this.slots ) {
                    if ( howManySigilSlotsOpen() >= slotsToOpen ) break;
                    if ( slot instanceof SigilSlot sigilSlot && !sigilSlot.isOpen ) sigilSlot.isOpen = true;
                }
            }
            else {
                for ( Slot slot : this.slots ) {
                    if ( slot instanceof SigilSlot sigilSlot) {
                        if ( !level.isClientSide && !sigilSlot.getItem().isEmpty() ) {
                            if ( stack.isEmpty() ) quickMoveStack(this.player, sigilSlot.index);
                            else setSlotContent(level, sigilSlot.getSlotIndex(), ItemStack.EMPTY);
                        }
                        if ( sigilSlot.isOpen ) sigilSlot.isOpen = false;
                    }
                }
            }
            if ( level.isClientSide ) {
                if ( isReadyToDump() ) {
                    editSpellForm(stack.getTag());
                    editSpellStats((byte)0, DataHelper.getStatsFromString(stack.getTag().getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES)));
                    editSpellStats((byte)1, DataHelper.getStatsFromString(stack.getTag().getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS)));
                }
            }
        });
    }

    public void editSpellForm(CompoundTag tag) {
        this.spellForm = DataHelper.getFormFromNbt(tag);
        ModNetwork.sendToServer(new PacketEditSpellForm(tag));
    }

    public void processSpellFormEditing(CompoundTag tag) {
        this.access.execute((level, pos) -> {
            if ( !level.isClientSide ) this.spellForm = DataHelper.getFormFromNbt(tag);
        });
    }

    public void editSpellStats(byte flag, List<Integer> list) {
        if ( flag == 0 ) this.magnitude = list;
        else if ( flag == 1 ) this.duration = list;
        ModNetwork.sendToServer(new PacketEditSpellStats(flag, list));
    }

    public void processSpellStatEditing(byte flag, List<Integer> list) {
        this.access.execute((level, pos) -> {
            if ( !level.isClientSide ) {
                if ( flag == 0 ) this.magnitude = list;
                else if ( flag == 1 ) this.duration = list;
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if ( slot != null && slot.hasItem() ) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();
            if ( index == 0 ) {
                if ( !this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true) ) return ItemStack.EMPTY;
            }
            else if ( index >= INV_SLOT_START && index < USE_ROW_SLOT_END ) {
                if ( !this.moveItemStackTo(stack, CRAFT_SLOT_START, CRAFT_SLOT_END, false) ) {
                    if ( index < INV_SLOT_END ) {
                        if ( !this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false) ) return ItemStack.EMPTY;
                    }
                    else if ( !this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false) ) return ItemStack.EMPTY;
                }
            }
            else if ( !this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false) ) return ItemStack.EMPTY;

            if ( stack.isEmpty() ) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();

            if ( stack.getCount() == itemStack.getCount() ) return ItemStack.EMPTY;

            slot.onTake(player, stack);
            if ( index == RESULT_SLOT ) player.drop(stack, false);
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
