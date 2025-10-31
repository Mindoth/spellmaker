package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.block.entity.CalcinatorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SpellMaker.MOD_ID);

    public static final RegistryObject<BlockEntityType<CalcinatorBlockEntity>> CALCINATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register("calcinator_block_entity",
            () -> BlockEntityType.Builder.of(CalcinatorBlockEntity::new, ModBlocks.CALCINATOR.get()).build(null));
}
