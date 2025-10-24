package net.mindoth.spellmaker.recipe;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class SpellBookAddRecipe extends CustomRecipe {

    public SpellBookAddRecipe(ResourceLocation pId, CraftingBookCategory category) {
        super(pId, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        List<ItemStack> bookList = Lists.newArrayList();
        List<ItemStack> paperList = Lists.newArrayList();
        List<ItemStack> restList = Lists.newArrayList();
        for ( int i = 0; i < container.getContainerSize(); i++ ) {
            ItemStack stack = container.getItem(i);
            if ( !stack.isEmpty() ) {
                boolean paperHasSpell = stack.hasTag() && stack.getTag().contains(ParchmentItem.NBT_KEY_SPELL_FORM);
                if ( stack.getItem() instanceof SpellBookItem) bookList.add(stack);
                else if ( stack.getItem() instanceof ParchmentItem && paperHasSpell ) paperList.add(stack);
                else restList.add(stack);
            }
        }
        return bookList.size() == 1 && paperList.size() == 1 && restList.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess regAcc) {
        List<ItemStack> bookList = Lists.newArrayList();
        List<ItemStack> paperList = Lists.newArrayList();
        List<ItemStack> restList = Lists.newArrayList();
        for ( int i = 0; i < container.getContainerSize(); i++ ) {
            ItemStack stack = container.getItem(i);
            if ( !stack.isEmpty() ) {
                boolean paperHasSpell = stack.hasTag() && stack.getTag().contains(ParchmentItem.NBT_KEY_SPELL_FORM);
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
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SPELL_BOOK_ADD_RECIPE.get();
    }
}
