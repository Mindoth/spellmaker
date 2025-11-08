package net.mindoth.spellmaker.item;

import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.registries.ModData;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> components, TooltipFlag tooltipFlag) {
        if ( ModData.getLegacyTag(stack) != null ) {
            CompoundTag tag = ModData.getLegacyTag(stack);
            if ( tag.contains(NBT_KEY_SPELL_FORM) ) {
                AbstractSpellForm form = ModSpellForms.SPELL_FORM_REGISTRY.getValue(ResourceLocation.parse(tag.getString(NBT_KEY_SPELL_FORM).get()));
                int cost = calculateSpellCost(form, DataHelper.createMapFromTag(tag));
                components.accept(Component.literal(""));
                components.accept(Component.translatable("tooltip.spellmaker.cost")
                        .append(Component.literal("" + cost)).withStyle(ChatFormatting.GRAY));
                components.accept(Component.translatable("spellform.spellmaker." + form.getName()).withStyle(ChatFormatting.GRAY));
                if ( tag.contains(NBT_KEY_SPELL_SIGILS) ) {
                    List<SigilItem> list = DataHelper.getSigilListFromString(tag.getString(NBT_KEY_SPELL_SIGILS).get());
                    for ( SigilItem sigil : list ) {
                        String name = new ItemStack(sigil).getHoverName().getString();
                        components.accept(Component.literal(" ").append(Component.literal(name).withStyle(ChatFormatting.GRAY)));
                    }
                }
            }
        }
        super.appendHoverText(stack, context, tooltipDisplay, components, tooltipFlag);
    }

    public static int calculateSpellCost(AbstractSpellForm form, LinkedHashMap<SigilItem, List<Integer>> map) {
        int totalCost = 0;
        for ( SigilItem sigil : map.keySet() ) {
            int cost = sigil.getCost();
            List<Integer> stats = map.get(sigil);
            if ( sigil.canModifyMagnitude() ) cost += Math.abs(stats.get(0)) * sigil.getMagnitudeMultiplier();
            if ( sigil.canModifyDuration() ) cost += Math.abs(stats.get(1)) * sigil.getDurationMultiplier();
            totalCost += cost;
        }
        return Mth.ceil(totalCost * form.getCost());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = ModData.getLegacyTag(stack);
        return stack.isEnchanted() || (tag != null && tag.contains(NBT_KEY_SPELL_FORM));
    }
}
