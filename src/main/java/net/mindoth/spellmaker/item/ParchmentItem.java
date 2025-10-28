package net.mindoth.spellmaker.item;

import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
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
    public static final String NBT_KEY_SPELL_SIGILS = "sm_spell_sigils";
    public static final String NBT_KEY_SPELL_MAGNITUDES = "sm_spell_magnitudes";
    public static final String NBT_KEY_SPELL_DURATIONS = "sm_spell_durations";

    public static final String NBT_KEY_SPELL_NAME = "sm_spell_name";
    public static final String NBT_KEY_PAPER_TIER = "sm_paper_tier";

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        if ( stack.hasTag() && stack.getTag().contains(NBT_KEY_SPELL_FORM) ) {
            AbstractSpellForm form = ModSpellForms.SPELL_FORM_REGISTRY.get().getValue(new ResourceLocation(stack.getTag().getString(NBT_KEY_SPELL_FORM)));
            int cost = calculateSpellCost(form, DataHelper.createMapFromTag(stack.getTag()));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tooltip.spellmaker.cost")
                    .append(Component.literal("" + cost)).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("spellform.spellmaker." + form.getName()).withStyle(ChatFormatting.GRAY));
            if ( stack.getTag().contains(NBT_KEY_SPELL_SIGILS) ) {
                List<SigilItem> list = DataHelper.getSigilListFromString(stack.getTag().getString(NBT_KEY_SPELL_SIGILS));
                for ( SigilItem sigil : list ) {
                    String name = new ItemStack(sigil).getHoverName().getString();
                    tooltip.add(Component.literal(" ").append(Component.literal(name).withStyle(ChatFormatting.GRAY)));
                }
            }
        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    public static int calculateSpellCost(AbstractSpellForm form, LinkedHashMap<SigilItem, List<Integer>> map) {
        int totalCost = 0;
        for ( SigilItem sigil : map.keySet() ) {
            int cost = sigil.getCost() + form.getCost();
            List<Integer> stats = map.get(sigil);
            if ( sigil.getMaxMagnitude() > 0 ) cost += stats.get(0) * sigil.getMagnitudeMultiplier();
            if ( sigil.getMaxDuration() > 0 ) cost += stats.get(1) * sigil.getDurationMultiplier();
            totalCost += cost;
        }
        return totalCost;
    }

    public static SigilItem getHighestCostSigil(LinkedHashMap<SigilItem, List<Integer>> map) {
        SigilItem state = null;
        int highestCost = 0;
        for ( SigilItem sigil : map.keySet() ) {
            int cost = sigil.getCost();
            List<Integer> stats = map.get(sigil);
            if ( sigil.getMaxMagnitude() > 0 ) cost += stats.get(0) * sigil.getMagnitudeMultiplier();
            if ( sigil.getMaxDuration() > 0 ) cost += stats.get(1) * sigil.getDurationMultiplier();
            if ( cost > highestCost ) {
                highestCost = cost;
                state = sigil;
            }
        }
        return state;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.isEnchanted() || (pStack.hasTag() && pStack.getTag().contains(NBT_KEY_SPELL_FORM));
    }

    //ONLY FOR TESTING
    /*@Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.fail(player.getItemInHand(hand));
        if ( !level.isClientSide ) {
            ItemStack stack = player.getItemInHand(hand);
            if ( stack.hasTag() && stack.getTag().contains(NBT_KEY_SPELL_FORM) ) {
                SpellForm form = DataHelper.getFormFromNbt(stack.getTag());
                form.castMagick(player, DataHelper.createMapFromTag(stack.getTag()));
            }
        }
        return result;
    }*/
}
