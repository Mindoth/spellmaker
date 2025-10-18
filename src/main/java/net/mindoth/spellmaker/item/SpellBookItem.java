package net.mindoth.spellmaker.item;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.network.ModNetwork;
import net.mindoth.spellmaker.network.PacketOpenSpellBook;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.ChatFormatting;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class SpellBookItem extends Item implements DyeableMagickItem {
    public SpellBookItem(Properties pProperties) {
        super(pProperties);
    }

    public static final String NBT_KEY_BOOK_FORMS = "sm_book_forms";
    public static final String NBT_KEY_BOOK_RUNES = "sm_book_runes";
    public static final String NBT_KEY_BOOK_MAGNITUDES = "sm_book_magnitudes";
    public static final String NBT_KEY_BOOK_DURATIONS = "sm_book_durations";

    public static final String NBT_KEY_OWNER_NAME = "am_book_owner_name";
    public static final String NBT_KEY_OWNER_UUID = "am_book_owner_uuid";
    public static final String NBT_KEY_BOOK_SLOT = "sm_book_slot";
    public static final String NBT_KEY_NULL_NAME = "sm_spell_has_null_name";

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        if ( stack.hasTag() && stack.getTag().contains(NBT_KEY_OWNER_NAME) ) {
            String name = stack.getTag().getString(NBT_KEY_OWNER_NAME);
            tooltip.add(Component.translatable("tooltip.spellmaker.book_owner").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(name).withStyle(ChatFormatting.GRAY)));
        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand handIn) {
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.fail(player.getItemInHand(handIn));
        if ( !level.isClientSide && player instanceof ServerPlayer serverPlayer ) {
            ItemStack book = player.getItemInHand(handIn);
            if ( player.isCrouching() ) {
                handleSignature(serverPlayer, book);
                ModNetwork.sendToPlayer(new PacketOpenSpellBook(book, 0), serverPlayer);
            }
            else {
                if ( book.hasTag() && book.getTag().contains(NBT_KEY_BOOK_SLOT) && book.getTag().getInt(NBT_KEY_BOOK_SLOT) >= 0 ) {
                    ItemStack scroll = getActiveScrollFromBook(book);
                    if ( scroll != null ) {
                        SpellForm form = DataHelper.getFormFromNbt(scroll.getTag());
                        form.castMagick(player, DataHelper.createMapFromTag(scroll.getTag()));
                    }
                }
            }
        }
        return result;
    }

    public static ItemStack getActiveScrollFromBook(ItemStack book) {
        CompoundTag tag = book.getTag();
        if ( !tag.contains(NBT_KEY_BOOK_SLOT) ) return null;
        int slot = tag.getInt(NBT_KEY_BOOK_SLOT);
        List<ItemStack> scrollList = SpellBookItem.getScrollListFromBook(tag);
        if ( slot >= scrollList.size() ) return null;
        return scrollList.get(slot);
    }

    public static void handleSignature(ServerPlayer serverPlayer, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
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

    public static List<ItemStack> getScrollListFromBook(CompoundTag tag) {
        List<ItemStack> scrollList = Lists.newArrayList();

        String form = tag.getString(NBT_KEY_BOOK_FORMS);
        List<String> formList = List.of(form.split(";"));

        String rune = tag.getString(NBT_KEY_BOOK_RUNES);
        List<String> runeList = List.of(rune.split(";"));

        String magnitude = tag.getString(NBT_KEY_BOOK_MAGNITUDES);
        List<String> magList = List.of(magnitude.split(";"));

        String duration = tag.getString(NBT_KEY_BOOK_DURATIONS);
        List<String> durList = List.of(duration.split(";"));

        String name = tag.getString(ParchmentItem.NBT_KEY_SPELL_NAME);
        List<String> nameList = List.of(name.split(";"));

        String item = tag.getString(ParchmentItem.NBT_KEY_PAPER_TIER);
        List<String> itemList = List.of(item.split(";"));

        for ( int i = 0; i < runeList.size(); i++ ) {
            ItemStack stack = constructSpellScroll(formList.get(i), runeList.get(i), magList.get(i), durList.get(i), nameList.get(i), ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemList.get(i))));
            scrollList.add(stack);
        }
        return scrollList;
    }

    public static ItemStack constructSpellScroll(String form, String runes, String magnitudes, String durations, String name, Item item) {
        ItemStack stack = new ItemStack(item);
        if ( !Objects.equals(name, NBT_KEY_NULL_NAME) ) stack.setHoverName(Component.literal(name));
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, form);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_RUNES, runes);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES, magnitudes);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_DURATIONS, durations);
        return stack;
    }

    public static ItemStack constructBook(ItemStack ogBook, List<ItemStack> scrolls) {
        ItemStack book = ogBook.copy();
        if ( book.hasTag() ) {
            if ( book.getTag().contains(NBT_KEY_BOOK_FORMS) ) book.getTag().remove(NBT_KEY_BOOK_FORMS);
            if ( book.getTag().contains(NBT_KEY_BOOK_RUNES) ) book.getTag().remove(NBT_KEY_BOOK_RUNES);
            if ( book.getTag().contains(NBT_KEY_BOOK_MAGNITUDES) ) book.getTag().remove(NBT_KEY_BOOK_MAGNITUDES);
            if ( book.getTag().contains(NBT_KEY_BOOK_DURATIONS) ) book.getTag().remove(NBT_KEY_BOOK_DURATIONS);
            if ( book.getTag().contains(ParchmentItem.NBT_KEY_SPELL_NAME) ) book.getTag().remove(ParchmentItem.NBT_KEY_SPELL_NAME);
            if ( book.getTag().contains(ParchmentItem.NBT_KEY_PAPER_TIER) ) book.getTag().remove(ParchmentItem.NBT_KEY_PAPER_TIER);
        }
        for ( ItemStack scroll : scrolls ) addSpellToBook(book, scroll);
        return book;
    }

    public static void addSpellToBook(ItemStack book, ItemStack scroll) {
        CompoundTag bookTag = book.getOrCreateTag();

        String formString = scroll.getTag().getString(ParchmentItem.NBT_KEY_SPELL_FORM);
        addSpellTagsToBook(bookTag, formString, NBT_KEY_BOOK_FORMS);

        String runeString = scroll.getTag().getString(ParchmentItem.NBT_KEY_SPELL_RUNES);
        addSpellTagsToBook(bookTag, runeString, NBT_KEY_BOOK_RUNES);

        String magString = scroll.getTag().getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES);
        addSpellTagsToBook(bookTag, magString, NBT_KEY_BOOK_MAGNITUDES);

        String durString = scroll.getTag().getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS);
        addSpellTagsToBook(bookTag, durString, NBT_KEY_BOOK_DURATIONS);

        String name;
        if ( scroll.hasCustomHoverName() ) name = scroll.getHoverName().getString();
        else name = NBT_KEY_NULL_NAME;
        addSpellTagsToBook(bookTag, name, ParchmentItem.NBT_KEY_SPELL_NAME);

        String item = ForgeRegistries.ITEMS.getKey(scroll.getItem()).toString();
        addSpellTagsToBook(bookTag, item, ParchmentItem.NBT_KEY_PAPER_TIER);
    }

    public static void addSpellTagsToBook(CompoundTag bookTag, String string, String key) {
        if ( !bookTag.contains(key) ) bookTag.putString(key, string);
        else {
            String spellList = bookTag.getString(key) + ";" + string;
            bookTag.remove(key);
            bookTag.putString(key, spellList);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
