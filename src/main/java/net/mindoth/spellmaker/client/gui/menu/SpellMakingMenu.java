package net.mindoth.spellmaker.client.gui.menu;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.sigil.AbstractSigilItem;
import net.mindoth.spellmaker.network.DumpSpellPacket;
import net.mindoth.spellmaker.network.EditSpellFormPacket;
import net.mindoth.spellmaker.network.EditSpellStatsPacket;
import net.mindoth.spellmaker.network.MakeSpellPacket;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModData;
import net.mindoth.spellmaker.registries.ModMenus;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;
import java.util.List;

public class SpellMakingMenu extends AbstractContainerMenu {

    public class ParchmentSlot extends Slot {
        public ParchmentSlot(Container pContainer, int pSlot, int pXPosition, int pYPosition) {
            super(pContainer, pSlot, pXPosition, pYPosition);
        }
        @Override
        public boolean mayPlace(ItemStack stack) {
            return !this.hasItem() && stack.getItem() instanceof ParchmentItem;
        }
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    public class SigilSlot extends Slot {
        public boolean isOpen;
        public SigilSlot(Container pContainer, int pSlot, int pX, int pY, boolean isOpen) {
            super(pContainer, pSlot, pX, pY);
            this.isOpen = isOpen;
        }
        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.isOpen && stack.getItem() instanceof AbstractSigilItem;
        }
        @Override
        public void setByPlayer(ItemStack pStack) {
            resetStatsForSlot(getSlotIndex());
            super.setByPlayer(pStack);
        }
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    public void resetStatsForSlot(int i) {
        if ( getMagnitude().get(i - 1) != 0 ) {
            List<Integer> list = Lists.newArrayList();
            list.addAll(getMagnitude());
            list.set(i - 1, 0);
            this.magnitude = list;
        }
        if ( getDuration().get(i - 1) != 0 ) {
            List<Integer> list = Lists.newArrayList();
            list.addAll(getDuration());
            list.set(i - 1, 0);
            this.duration = list;
        }
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

    public SpellMakingMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.SPELL_MAKING_MENU.get(), containerId);
        this.access = access;
        this.player = inventory.player;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        //Parchment slot
        this.addSlot(new ParchmentSlot(getCraftSlots(), 0, 8, 44));

        //Rune slots
        for ( int i = 0; i < 3; ++i ) {
            this.addSlot(new SigilSlot(getCraftSlots(), 1 + i, 35, 62 + i * 18, !getCraftSlots().getItem(getParchmentSlot()).isEmpty()));
        }

        dataInit();
    }

    private void addPlayerInventory(Inventory inventory) {
        for ( int i = 0; i < 3; ++i ) {
            for ( int j = 0; j < 9; ++j ) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 129 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for ( int i = 0; i < 9; ++i ) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 187));
        }
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

    public List<AbstractSpellForm> initFormList() {
        List<AbstractSpellForm> list = Lists.newArrayList();
        for ( DeferredHolder<AbstractSpellForm, ? extends AbstractSpellForm> holder : ModSpellForms.SPELL_FORMS.getEntries() ) {
            list.add(holder.get());
        }
        return list;
    }

    public void dataInit() {
        this.formList = initFormList();
        this.spellForm = this.formList.getFirst();
        this.magnitude = Arrays.asList(0, 0, 0);
        this.duration = Arrays.asList(0, 0, 0);
    }

    public boolean isCleanParchment(ItemStack stack) {
        CompoundTag tag = ModData.getLegacyTag(stack);
        return !stack.isEmpty() && stack.getItem() instanceof ParchmentItem && (tag == null || !tag.contains(ParchmentItem.NBT_KEY_SPELL_FORM));
    }

    private void cleanScroll(Level level, ItemStack scroll) {
        ItemStack stack = scroll.copy();
        CompoundTag tag = ModData.getLegacyTag(stack);
        if ( stack.has(DataComponents.CUSTOM_NAME) ) stack.remove(DataComponents.CUSTOM_NAME);
        if ( tag != null ) {
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_FORM) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_FORM);
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_SIGILS) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_SIGILS);
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES);
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_DURATIONS) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_DURATIONS);
            if ( tag.isEmpty() ) stack.remove(ModData.LEGACY_TAG);
        }
        setSlotContent(level, getParchmentSlot(), stack);
    }

    public boolean isReadyToMake() {
        return isCleanParchment(getCraftSlots().getItem(getParchmentSlot())) && !assemble(getCraftSlots()).isEmpty();
    }

    public boolean makeSpell(String string) {
        if ( isReadyToMake() ) {
            ClientPacketDistributor.sendToServer(new MakeSpellPacket(getItemName(string)));
            return true;
        }
        else return false;
    }

    public void processMaking(String name) {
        this.access.execute((level, pos) -> {
            if ( !level.isClientSide() ) {
                if ( isReadyToMake() ) {
                    ItemStack stack = assemble(getCraftSlots());
                    if ( isEmpty(name) || isBlank(name) ) {
                        if ( stack.has(DataComponents.CUSTOM_NAME) ) stack.remove(DataComponents.CUSTOM_NAME);
                    }
                    else stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
                    setSlotContent(level, getParchmentSlot(), stack);
                }
            }
        });
    }

    //Server can't find TextUtils class, so I brought some methods over
    public static boolean isEmpty(final CharSequence s) {
        if ( s == null ) return true;
        return s.length() == 0;
    }

    public static boolean isBlank(final CharSequence s) {
        if ( s == null ) return true;
        for ( int i = 0; i < s.length(); i++ ) if ( !Character.isWhitespace(s.charAt(i)) ) return false;
        return true;
    }

    public boolean isReadyToDump() {
        ItemStack stack = getCraftSlots().getItem(getParchmentSlot());
        CompoundTag tag = ModData.getLegacyTag(stack);
        return !stack.isEmpty() && stack.getItem() instanceof ParchmentItem && tag != null && tag.contains(ParchmentItem.NBT_KEY_SPELL_FORM);
    }

    public boolean dumpSpell() {
        if ( isReadyToDump() ) {
            ItemStack stack = getCraftSlots().getItem(getParchmentSlot());
            CompoundTag tag = ModData.getLegacyTag(stack);
            this.spellForm = DataHelper.getFormFromNbt(tag);
            this.magnitude = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES).get());
            this.duration = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS).get());
            ClientPacketDistributor.sendToServer(new DumpSpellPacket());
            return true;
        }
        else return false;
    }

    public void processDumping() {
        this.access.execute((level, pos) -> {
            if ( !level.isClientSide() ) {
                if ( isReadyToDump() ) {
                    ItemStack stack = getCraftSlots().getItem(getParchmentSlot());
                    CompoundTag tag = ModData.getLegacyTag(stack);
                    List<ItemStack> list = DataHelper.getSpellStackFromTag(tag);
                    AbstractSpellForm form = DataHelper.getFormFromNbt(tag);
                    List<Integer> magnitude = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES).get());
                    List<Integer> duration = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS).get());
                    cleanScroll(level, stack);
                    for ( int i = 1; i < getCraftSlots().getContainerSize(); i++ ) {
                        ItemStack sigil = list.get(i - 1);
                        setSlotContent(level, i, sigil);
                    }
                    this.spellForm = form;
                    this.magnitude = magnitude;
                    this.duration = duration;
                }
            }
        });
    }

    public ItemStack assemble(Container container) {
        ItemStack scroll = getCraftSlots().getItem(getParchmentSlot()).copy();
        List<ItemStack> sigilStackList = Lists.newArrayList();
        List<ItemStack> restList = Lists.newArrayList();
        for ( int i = 1; i < container.getContainerSize(); i++ ) {
            ItemStack stack = container.getItem(i);
            if ( stack.getItem() instanceof AbstractSigilItem || stack.isEmpty() ) sigilStackList.add(stack);
            else restList.add(stack);
        }
        if ( restList.isEmpty() ) {
            CompoundTag tag = ModData.getOrCreateLegacyTag(scroll);
            tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, DataHelper.getStringFromForm(this.spellForm));
            tag.putString(ParchmentItem.NBT_KEY_SPELL_SIGILS, DataHelper.getStringFromSpellStack(sigilStackList));
            tag.putString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES, DataHelper.getStringFromStats(this.magnitude));
            tag.putString(ParchmentItem.NBT_KEY_SPELL_DURATIONS, DataHelper.getStringFromStats(this.duration));
            return scroll;
        }
        return ItemStack.EMPTY;
    }

    private String getItemName(String string) {
        return StringUtil.filterText(string).length() <= 50 ? StringUtil.filterText(string) : null;
    }

    private void setSlotContent(Level level, int slot, ItemStack stack) {
        if ( !level.isClientSide() ) {
            ServerPlayer serverplayer = (ServerPlayer)this.player;
            getCraftSlots().setItem(slot, stack);
            this.setRemoteSlot(slot, stack);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), slot, stack));
        }
    }

    public int howManySigilSlotsOpen() {
        int count = 0;
        for ( Slot slot : this.slots ) if ( slot instanceof SigilSlot sigilSlot && sigilSlot.isOpen ) count++;
        return count;
    }

    @Override
    public void slotsChanged(Container pInventory) {
        this.access.execute((level, pos) -> {
            ItemStack stack = getCraftSlots().getItem(getParchmentSlot());
            if ( isReadyToMake() ) {
                final int slotsToOpen = ((ParchmentItem)stack.getItem()).getSize();
                for ( Slot slot : this.slots ) {
                    if ( howManySigilSlotsOpen() >= slotsToOpen ) break;
                    if ( slot instanceof SigilSlot sigilSlot && !sigilSlot.isOpen ) sigilSlot.isOpen = true;
                }
            }
            else {
                if ( level.isClientSide() ) {
                    CompoundTag tag = new CompoundTag();
                    tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, DataHelper.getStringFromForm(initFormList().getFirst()));
                    editSpellForm(tag);
                }
                for ( Slot slot : this.slots ) {
                    if ( slot instanceof SigilSlot sigilSlot ) {
                        if ( !level.isClientSide() && !sigilSlot.getItem().isEmpty() ) {
                            if ( stack.isEmpty() ) quickMoveStack(this.player, sigilSlot.index);
                            else setSlotContent(level, sigilSlot.getSlotIndex(), ItemStack.EMPTY);
                        }
                        if ( sigilSlot.isOpen ) sigilSlot.isOpen = false;
                    }
                }
            }
        });
    }

    public void editSpellForm(CompoundTag tag) {
        this.spellForm = DataHelper.getFormFromNbt(tag);
        ClientPacketDistributor.sendToServer(new EditSpellFormPacket(tag));
    }

    public void processSpellFormEditing(CompoundTag tag) {
        this.access.execute((level, pos) -> {
            if ( !level.isClientSide() ) this.spellForm = DataHelper.getFormFromNbt(tag);
        });
    }

    public void editSpellStats(byte flag, List<Integer> list) {
        if ( flag == 0 ) this.magnitude = list;
        else if ( flag == 1 ) this.duration = list;
        ClientPacketDistributor.sendToServer(new EditSpellStatsPacket(flag, list));
    }

    public void processSpellStatEditing(byte flag, List<Integer> list) {
        this.access.execute((level, pos) -> {
            if ( !level.isClientSide() ) {
                if ( flag == 0 ) this.magnitude = list;
                else if ( flag == 1 ) this.duration = list;
            }
        });
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int CUSTOM_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int CUSTOM_INVENTORY_SLOT_COUNT = 4;
    private static final int CUSTOM_INVENTORY_LAST_SLOT_INDEX = CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT - 1;

    public int getParchmentSlot() {
        return 0;
    }

    public int getParchmentMenuSlot() {
        return CUSTOM_INVENTORY_FIRST_SLOT_INDEX;
    }

    //TODO: Fix shift-clicking parchment to not move whole stack
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if ( slot == null || !slot.hasItem() ) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if ( index == CUSTOM_INVENTORY_FIRST_SLOT_INDEX ) {
            if ( !moveItemStackTo(stack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, true) ) {
                return ItemStack.EMPTY;
            }
        }
        else if ( index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT ) {
            if ( stack.getItem() instanceof ParchmentItem ) {
                if ( !moveItemStackTo(stack, CUSTOM_INVENTORY_FIRST_SLOT_INDEX, CUSTOM_INVENTORY_FIRST_SLOT_INDEX + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            }
            else if ( isSigilItem(stack) ) {
                if ( !moveItemStackTo(stack, CUSTOM_INVENTORY_FIRST_SLOT_INDEX + 1, CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT, false) ) {
                    return ItemStack.EMPTY;
                }
            }
            else if ( index >= VANILLA_FIRST_SLOT_INDEX && index < PLAYER_INVENTORY_SLOT_COUNT ) {
                if ( !moveItemStackTo(stack, PLAYER_INVENTORY_SLOT_COUNT, VANILLA_SLOT_COUNT, false) ) {
                    return ItemStack.EMPTY;
                }
            }
            else if ( index >= PLAYER_INVENTORY_SLOT_COUNT && index < VANILLA_SLOT_COUNT ) {
                if ( !moveItemStackTo(stack, VANILLA_FIRST_SLOT_INDEX, PLAYER_INVENTORY_SLOT_COUNT, false) ) {
                    return ItemStack.EMPTY;
                }
            }
        }
        else if ( index < CUSTOM_INVENTORY_FIRST_SLOT_INDEX + CUSTOM_INVENTORY_SLOT_COUNT) {
            if ( !moveItemStackTo(stack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false) ) {
                return ItemStack.EMPTY;
            }
        }
        else {
            System.out.println("Invalid index:" + index);
            return ItemStack.EMPTY;
        }
        if ( stack.getCount() == 0 ) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return copy;
    }

    private boolean isSigilItem(ItemStack stack) {
        return stack.getItem() instanceof AbstractSigilItem;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((level, pos) -> this.clearContainer(pPlayer, getCraftSlots()));
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
