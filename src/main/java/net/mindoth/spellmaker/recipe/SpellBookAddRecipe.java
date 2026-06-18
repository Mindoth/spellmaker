package net.mindoth.spellmaker.recipe;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.registries.ModData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class SpellBookAddRecipe extends CustomRecipe {

    @Override
    public boolean matches(CraftingInput input, Level level) {
        List<ItemStack> bookList = Lists.newArrayList();
        List<ItemStack> paperList = Lists.newArrayList();
        List<ItemStack> restList = Lists.newArrayList();
        for ( int i = 0; i < input.size(); i++ ) {
            ItemStack stack = input.getItem(i);
            if ( !stack.isEmpty() ) {
                CompoundTag tag = ModData.getLegacyTag(stack);
                boolean paperHasSpell = tag != null && tag.contains(ParchmentItem.NBT_KEY_SPELL_FORM);
                if ( stack.getItem() instanceof SpellBookItem) bookList.add(stack);
                else if ( stack.getItem() instanceof ParchmentItem && paperHasSpell ) paperList.add(stack);
                else restList.add(stack);
            }
        }
        return bookList.size() == 1 && paperList.size() == 1 && restList.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        List<ItemStack> bookList = Lists.newArrayList();
        List<ItemStack> paperList = Lists.newArrayList();
        List<ItemStack> restList = Lists.newArrayList();
        for ( int i = 0; i < input.size(); i++ ) {
            ItemStack stack = input.getItem(i);
            if ( !stack.isEmpty() ) {
                CompoundTag tag = ModData.getLegacyTag(stack);
                boolean paperHasSpell = tag != null && tag.contains(ParchmentItem.NBT_KEY_SPELL_FORM);
                if ( stack.getItem() instanceof SpellBookItem) bookList.add(stack);
                else if ( stack.getItem() instanceof ParchmentItem && paperHasSpell ) paperList.add(stack);
                else restList.add(stack);
            }
        }
        if ( bookList.size() == 1 && paperList.size() == 1 && restList.isEmpty() ) {
            ItemStack book = bookList.get(0).copy();
            ItemStack scroll = paperList.get(0);

            SpellBookItem.addSpellToBook(book, scroll);

            return book;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static final SpellBookAddRecipe INSTANCE = new SpellBookAddRecipe();
    public static final MapCodec<SpellBookAddRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellBookAddRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<SpellBookAddRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }
}
