package net.mindoth.spellmaker.item;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.weapon.StaffItem;
import net.mindoth.spellmaker.network.OpenSpellBookPacket;
import net.mindoth.spellmaker.registries.ModData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class SpellBookItem extends Item implements ModDyeableItem {

    public SpellBookItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    public static final int maxRows = 4;
    public static final int maxColumns = 1;
    public static int pageSize = maxRows * maxColumns * 2;

    public static final String NBT_KEY_BOOK_FORMS = "sm_book_forms";
    public static final String NBT_KEY_BOOK_SIGILS = "sm_book_sigils";
    public static final String NBT_KEY_BOOK_MAGNITUDES = "sm_book_magnitudes";
    public static final String NBT_KEY_BOOK_DURATIONS = "sm_book_durations";

    public static final String NBT_KEY_OWNER_NAME = "sm_book_owner_name";
    public static final String NBT_KEY_OWNER_UUID = "sm_book_owner_uuid";
    public static final String NBT_KEY_BOOK_SLOT = "sm_book_slot";
    public static final String NBT_KEY_NULL_NAME = "sm_spell_has_null_name";

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = ModData.getLegacyTag(stack);
        if ( tag != null ) {
            if ( tag.contains(NBT_KEY_OWNER_NAME) ) {
                String name = tag.getString(NBT_KEY_OWNER_NAME);
                tooltip.add(Component.translatable("tooltip.spellmaker.book_owner").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(name).withStyle(ChatFormatting.GRAY)));
            }
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand handIn) {
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.fail(player.getItemInHand(handIn));
        if ( !level.isClientSide && player instanceof ServerPlayer serverPlayer ) {
            if ( StaffItem.getHeldCastingItem(serverPlayer).isEmpty() ) {
                ItemStack book = serverPlayer.getItemInHand(handIn);
                openSpellBook(serverPlayer, book);
            }
        }
        return result;
    }

    public static void openSpellBook(ServerPlayer player, ItemStack book) {
        handleSignature(player, book);
        CompoundTag tag = ModData.getLegacyTag(book);
        if ( tag != null ) {
            int slot = tag.getInt(NBT_KEY_BOOK_SLOT);
            int page = 0;
            if ( slot >= pageSize ) {
                for ( int i = pageSize; i < getScrollListFromBook(tag).size(); i++ ) {
                    if ( i % pageSize == 0 ) page++;
                    if ( i == slot ) break;
                }
            }
            PacketDistributor.sendToPlayer(player, new OpenSpellBookPacket(book, page));
        }
    }

    public static void handleSignature(ServerPlayer serverPlayer, ItemStack stack) {
        CompoundTag tag = ModData.getOrCreateLegacyTag(stack);
        if ( !tag.contains(NBT_KEY_BOOK_SLOT) ) tag.putInt(NBT_KEY_BOOK_SLOT, -1);
        if ( !tag.contains(NBT_KEY_OWNER_UUID) ){
            tag.putUUID(NBT_KEY_OWNER_UUID, serverPlayer.getUUID());
            tag.putString(NBT_KEY_OWNER_NAME, serverPlayer.getDisplayName().getString());
        }
        if ( tag.contains(NBT_KEY_OWNER_UUID) && tag.contains(NBT_KEY_OWNER_NAME) ) {
            if ( tag.getUUID(NBT_KEY_OWNER_UUID) == serverPlayer.getUUID() && !tag.getString(NBT_KEY_OWNER_NAME).equals(serverPlayer.getDisplayName().getString())) {
                tag.putString(NBT_KEY_OWNER_NAME, serverPlayer.getDisplayName().getString());
            }
        }
    }

    public static int getNewSlotFromScrollRemoval(int oldSlot, int bookSlot) {
        if ( oldSlot == bookSlot ) return -1;
        else if ( oldSlot < bookSlot ) return bookSlot - 1;
        else return bookSlot;
    }

    public static ItemStack getActiveScrollFromBook(ItemStack book) {
        CompoundTag tag = ModData.getLegacyTag(book);
        if ( !tag.contains(NBT_KEY_BOOK_SLOT) ) return null;
        int slot = tag.getInt(NBT_KEY_BOOK_SLOT);
        List<ItemStack> scrollList = SpellBookItem.getScrollListFromBook(tag);
        if ( slot >= scrollList.size() ) return null;
        return scrollList.get(slot);
    }

    public static List<ItemStack> getScrollListFromBook(CompoundTag tag) {
        List<ItemStack> scrollList = Lists.newArrayList();

        String form = tag.getString(NBT_KEY_BOOK_FORMS);
        List<String> formList = List.of(form.split(";"));

        String sigil = tag.getString(NBT_KEY_BOOK_SIGILS);
        List<String> sigilList = List.of(sigil.split(";"));

        String magnitude = tag.getString(NBT_KEY_BOOK_MAGNITUDES);
        List<String> magList = List.of(magnitude.split(";"));

        String duration = tag.getString(NBT_KEY_BOOK_DURATIONS);
        List<String> durList = List.of(duration.split(";"));

        String name = tag.getString(ParchmentItem.NBT_KEY_SPELL_NAME);
        List<String> nameList = List.of(name.split(";"));

        String item = tag.getString(ParchmentItem.NBT_KEY_PAPER_TIER);
        List<String> itemList = List.of(item.split(";"));

        for ( int i = 0; i < sigilList.size(); i++ ) {
            ItemStack stack = constructSpellScroll(formList.get(i), sigilList.get(i), magList.get(i), durList.get(i), nameList.get(i),
                    BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemList.get(i))));
            scrollList.add(stack);
        }
        return scrollList;
    }

    public static ItemStack constructSpellScroll(String form, String sigils, String magnitudes, String durations, String name, Item item) {
        ItemStack stack = new ItemStack(item);
        CompoundTag tag = new CompoundTag();
        if ( !Objects.equals(name, NBT_KEY_NULL_NAME) ) stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, form);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_SIGILS, sigils);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES, magnitudes);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_DURATIONS, durations);
        ModData.setLegacyTag(stack, tag);
        return stack;
    }

    public static ItemStack constructBook(ItemStack ogBook, List<ItemStack> scrolls) {
        ItemStack book = ogBook.copy();
        CompoundTag tag = ModData.getLegacyTag(book);
        if ( tag != null ) {
            if ( tag.contains(NBT_KEY_BOOK_FORMS) ) tag.remove(NBT_KEY_BOOK_FORMS);
            if ( tag.contains(NBT_KEY_BOOK_SIGILS) ) tag.remove(NBT_KEY_BOOK_SIGILS);
            if ( tag.contains(NBT_KEY_BOOK_MAGNITUDES) ) tag.remove(NBT_KEY_BOOK_MAGNITUDES);
            if ( tag.contains(NBT_KEY_BOOK_DURATIONS) ) tag.remove(NBT_KEY_BOOK_DURATIONS);
            if ( tag.contains(ParchmentItem.NBT_KEY_SPELL_NAME) ) tag.remove(ParchmentItem.NBT_KEY_SPELL_NAME);
            if ( tag.contains(ParchmentItem.NBT_KEY_PAPER_TIER) ) tag.remove(ParchmentItem.NBT_KEY_PAPER_TIER);
        }
        for ( ItemStack scroll : scrolls ) addSpellToBook(book, scroll);
        return book;
    }

    public static void addSpellToBook(ItemStack book, ItemStack scroll) {
        CompoundTag bookTag = ModData.getOrCreateLegacyTag(book);
        CompoundTag scrollTag = ModData.getLegacyTag(scroll);

        String formString = scrollTag.getString(ParchmentItem.NBT_KEY_SPELL_FORM);
        addSpellTagsToBook(bookTag, formString, NBT_KEY_BOOK_FORMS);

        String runeString = scrollTag.getString(ParchmentItem.NBT_KEY_SPELL_SIGILS);
        addSpellTagsToBook(bookTag, runeString, NBT_KEY_BOOK_SIGILS);

        String magString = scrollTag.getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES);
        addSpellTagsToBook(bookTag, magString, NBT_KEY_BOOK_MAGNITUDES);

        String durString = scrollTag.getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS);
        addSpellTagsToBook(bookTag, durString, NBT_KEY_BOOK_DURATIONS);

        String name;
        if ( scroll.get(DataComponents.CUSTOM_NAME) != null ) name = scroll.getHoverName().getString();
        else name = NBT_KEY_NULL_NAME;
        addSpellTagsToBook(bookTag, name, ParchmentItem.NBT_KEY_SPELL_NAME);

        String item = BuiltInRegistries.ITEM.getKey(scroll.getItem()).toString();
        addSpellTagsToBook(bookTag, item, ParchmentItem.NBT_KEY_PAPER_TIER);
    }

    public static void addSpellTagsToBook(CompoundTag bookTag, String string, String key) {
        if ( bookTag.contains(key) ) {
            String spellList = bookTag.getString(key) + ";" + string;
            bookTag.remove(key);
            bookTag.putString(key, spellList);
        }
        else bookTag.putString(key, string);
    }

    public static ItemStack getSpellBookSlot(Player player) {
        ItemStack offHand = player.getOffhandItem();
        if ( offHand.getItem() instanceof SpellBookItem ) return offHand;
        for ( int i = 0; i <= player.getInventory().getContainerSize(); i++ ) {
            ItemStack slot = player.getInventory().getItem(i);
            if ( slot.getItem() instanceof SpellBookItem ) return slot;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getTaggedSpellBookSlot(Player player) {
        ItemStack offHand = player.getOffhandItem();
        CompoundTag offTag = ModData.getLegacyTag(offHand);
        if ( offHand.getItem() instanceof SpellBookItem && offTag != null && offTag.contains(NBT_KEY_BOOK_FORMS)
                && !offTag.getString(NBT_KEY_BOOK_FORMS).isEmpty() ) return offHand;
        for ( int i = 0; i <= player.getInventory().getContainerSize(); i++ ) {
            ItemStack slot = player.getInventory().getItem(i);
            CompoundTag slotTag = ModData.getLegacyTag(slot);
            if ( slot.getItem() instanceof SpellBookItem && ModData.getLegacyTag(slot) != null && slotTag.contains(NBT_KEY_BOOK_FORMS)
                    && !slotTag.getString(NBT_KEY_BOOK_FORMS).isEmpty() ) return slot;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
