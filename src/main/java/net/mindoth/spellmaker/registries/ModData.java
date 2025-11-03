package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModData {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SpellMaker.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>> LEGACY_TAG = register("legacy_tag",
            builder -> builder.persistent(CustomData.CODEC));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static CompoundTag getLegacyTag(ItemStack stack) {
        if ( !stack.has(LEGACY_TAG.get()) ) return null;
        else return stack.get(LEGACY_TAG.get()).getUnsafe();
    }

    public static void setLegacyTag(ItemStack stack, CompoundTag tag) {
        stack.set(ModData.LEGACY_TAG.get(), CustomData.of(tag));
    }

    public static CompoundTag getOrCreateLegacyTag(ItemStack stack) {
        CompoundTag tag;
        if ( ModData.getLegacyTag(stack) == null ) {
            tag = new CompoundTag();
            ModData.setLegacyTag(stack, tag);
        }
        else tag = ModData.getLegacyTag(stack);
        return tag;
    }
}
