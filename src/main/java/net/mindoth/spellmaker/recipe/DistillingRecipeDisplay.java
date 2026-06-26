package net.mindoth.spellmaker.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record DistillingRecipeDisplay(SlotDisplay ingredient0, SlotDisplay ingredient1, SlotDisplay result0, SlotDisplay result1, SlotDisplay result2, SlotDisplay result3, SlotDisplay craftingStation) implements RecipeDisplay {

    public static final MapCodec<DistillingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            SlotDisplay.CODEC.fieldOf("ingredient0").forGetter(DistillingRecipeDisplay::ingredient0),
                            SlotDisplay.CODEC.fieldOf("ingredient1").forGetter(DistillingRecipeDisplay::ingredient1),
                            SlotDisplay.CODEC.fieldOf("result0").forGetter(DistillingRecipeDisplay::result0),
                            SlotDisplay.CODEC.fieldOf("result1").forGetter(DistillingRecipeDisplay::result1),
                            SlotDisplay.CODEC.fieldOf("result2").forGetter(DistillingRecipeDisplay::result2),
                            SlotDisplay.CODEC.fieldOf("result3").forGetter(DistillingRecipeDisplay::result3),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(DistillingRecipeDisplay::craftingStation))
                    .apply(builder, DistillingRecipeDisplay::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DistillingRecipeDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    SlotDisplay.STREAM_CODEC, DistillingRecipeDisplay::ingredient0,
                    SlotDisplay.STREAM_CODEC, DistillingRecipeDisplay::ingredient1,
                    SlotDisplay.STREAM_CODEC, DistillingRecipeDisplay::result0,
                    SlotDisplay.STREAM_CODEC, DistillingRecipeDisplay::result1,
                    SlotDisplay.STREAM_CODEC, DistillingRecipeDisplay::result2,
                    SlotDisplay.STREAM_CODEC, DistillingRecipeDisplay::result3,
                    SlotDisplay.STREAM_CODEC, DistillingRecipeDisplay::craftingStation,
                    DistillingRecipeDisplay::new
            );

    public static final RecipeDisplay.Type<DistillingRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public SlotDisplay result() {
        return this.result0;
    }

    @Override
    public Type<DistillingRecipeDisplay> type() {
        return TYPE;
    }
}
