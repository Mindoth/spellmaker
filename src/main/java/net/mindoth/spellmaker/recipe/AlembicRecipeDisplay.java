package net.mindoth.spellmaker.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record AlembicRecipeDisplay(SlotDisplay middle, SlotDisplay top, SlotDisplay bottom, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

    public static final MapCodec<AlembicRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            SlotDisplay.CODEC.fieldOf("middle").forGetter(AlembicRecipeDisplay::middle),
                            SlotDisplay.CODEC.fieldOf("top").forGetter(AlembicRecipeDisplay::top),
                            SlotDisplay.CODEC.fieldOf("bottom").forGetter(AlembicRecipeDisplay::bottom),
                            SlotDisplay.CODEC.fieldOf("result").forGetter(AlembicRecipeDisplay::result),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(AlembicRecipeDisplay::craftingStation))
                    .apply(builder, AlembicRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlembicRecipeDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::middle,
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::top,
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::bottom,
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::result,
                    SlotDisplay.STREAM_CODEC, AlembicRecipeDisplay::craftingStation,
                    AlembicRecipeDisplay::new);

    public static final RecipeDisplay.Type<AlembicRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public Type<AlembicRecipeDisplay> type() {
        return TYPE;
    }
}
