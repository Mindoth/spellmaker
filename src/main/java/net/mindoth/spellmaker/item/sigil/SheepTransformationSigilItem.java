package net.mindoth.spellmaker.item.sigil;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class SheepTransformationSigilItem extends PolymorphSigilItem {
    public SheepTransformationSigilItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier, UUID uuid, EntityType entityType) {
        super(pProperties, color, cost, maxMagnitude, magnitudeMultiplier, maxDuration, durationMultiplier, uuid, entityType);
    }

    @Override
    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(POLYMORPH_SPEED_MODIFIER_UUID, "Polymorph Speed", -0.05D, AttributeModifier.Operation.ADDITION);
    }
}
