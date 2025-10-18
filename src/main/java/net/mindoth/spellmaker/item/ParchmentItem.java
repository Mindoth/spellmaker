package net.mindoth.spellmaker.item;

import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ParchmentItem extends Item {
    private final int size;
    public int getSize() {
        return this.size;
    }
    public ParchmentItem(Properties pProperties, int size) {
        super(pProperties);
        this.size = size;
    }

    public static final String NBT_KEY_SPELL_FORM = "sm_spell_form";
    public static final String NBT_KEY_SPELL_RUNES = "sm_spell_runes";
    public static final String NBT_KEY_SPELL_MAGNITUDES = "sm_spell_magnitudes";
    public static final String NBT_KEY_SPELL_DURATIONS = "sm_spell_durations";

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        if ( stack.hasTag() && stack.getTag().contains(NBT_KEY_SPELL_FORM) ) {
            String form = ModSpellForms.SPELL_FORM_REGISTRY.get().getValue(new ResourceLocation(stack.getTag().getString(NBT_KEY_SPELL_FORM))).getName();
            tooltip.add(Component.translatable("spellform.spellmaker." + form).withStyle(ChatFormatting.GRAY));
            if ( stack.getTag().contains(NBT_KEY_SPELL_RUNES) ) {
                List<RuneItem> list = DataHelper.getRuneListFromString(stack.getTag().getString(NBT_KEY_SPELL_RUNES));
                for ( RuneItem rune : list ) {
                    String name = new ItemStack(rune).getHoverName().getString();
                    tooltip.add(Component.literal(name).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.isEnchanted() || (pStack.hasTag() && pStack.getTag().contains(NBT_KEY_SPELL_FORM));
    }

    //ONLY FOR TESTING
    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.fail(player.getItemInHand(hand));
        if ( !level.isClientSide ) {
            ItemStack stack = player.getItemInHand(hand);
            if ( stack.hasTag() && stack.getTag().contains(NBT_KEY_SPELL_FORM) ) {
                LinkedHashMap<RuneItem, List<Integer>> map = new LinkedHashMap<>();
                List<Integer> magnitudes = DataHelper.getStatsFromString(stack.getTag().getString(NBT_KEY_SPELL_MAGNITUDES));
                List<Integer> durations = DataHelper.getStatsFromString(stack.getTag().getString(NBT_KEY_SPELL_DURATIONS));
                for ( int i = 0; i < DataHelper.getSpellStackFromScroll(stack).size(); i++ ) {
                    ItemStack itemStack = DataHelper.getSpellStackFromScroll(stack).get(i);
                    if ( itemStack.getItem() instanceof RuneItem rune ) map.put(rune, Arrays.asList(magnitudes.get(i), durations.get(i)));
                }
                SpellForm form = DataHelper.getFormFromNbt(stack.getTag());
                form.castMagick(player, map);
            }
        }
        return result;
    }
}
