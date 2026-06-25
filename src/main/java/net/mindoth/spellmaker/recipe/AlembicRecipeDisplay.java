package net.mindoth.spellmaker.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record AlembicRecipeDisplay(SlotDisplay ingredient0, SlotDisplay ingredient1, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

    public static final MapCodec<AlembicRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            SlotDisplay.CODEC.fieldOf("ingredient0").forGetter(AlembicRecipeDisplay::ingredient0),
                            SlotDisplay.CODEC.fieldOf("ingredient1").forGetter(AlembicRecipeDisplay::ingredient1),
                            SlotDisplay.CODEC.fieldOf("result").forGetter(AlembicRecipeDisplay::result),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(AlembicRecipeDisplay::craftingStation))
                    .apply(builder, AlembicRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlembicRecipeDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::ingredient0,
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::ingredient1,
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::result,
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::craftingStation,
                    AlembicRecipeDisplay::new);

    public static final RecipeDisplay.Type<AlembicRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public Type<AlembicRecipeDisplay> type() {
        return TYPE;
    }
}
