package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.block.CalcinatorBlock;
import net.mindoth.spellmaker.block.SpellMakingTableBlock;
import net.mindoth.spellmaker.block.entity.CalcinatorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpellMaker.MOD_ID);

    public static final DeferredBlock<Block> CALCINATOR = registerBlock("calcinator",
            () -> new CalcinatorBlock(BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13))
            ));

    private static ToIntFunction<BlockState> litBlockEmission(int pLightValue) {
        return p_50763_ -> p_50763_.getValue(BlockStateProperties.LIT) ? pLightValue : 0;
    }

    public static final DeferredBlock<Block> SPELL_MAKING_TABLE = registerBlock("spell_making_table",
            () -> new SpellMakingTableBlock(BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
            ));


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpellMaker.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CalcinatorBlockEntity>> CALCINATOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("calcinator_block_entity",
                    () -> BlockEntityType.Builder.of(CalcinatorBlockEntity::new, ModBlocks.CALCINATOR.get()).build(null));
}
