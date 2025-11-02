package net.mindoth.spellmaker.item.sigil;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.mindoth.spellmaker.registries.ModItems;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class FishTransformationSigilItem extends PolymorphSigilItem {

    public FishTransformationSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier, UUID uuid, EntityType entityType) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier, uuid, entityType);
    }

    @Override
    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(POLYMORPH_SPEED_MODIFIER_UUID, "Polymorph Speed", -0.1D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected AttributeModifier getSwimSpeedModifier() {
        return new AttributeModifier(getUUID(), "Polymorph Swim Speed", 5.0D, AttributeModifier.Operation.ADDITION);
    }

    public static boolean isFish(LivingEntity living) {
        if ( !(living instanceof Player player) ) return false;
        return PolymorphEffect.isPolymorphed(player) && PolymorphEffect.getTransformationSigil(player) == ModItems.FISH_FORM_SIGIL.get();
    }

    @SubscribeEvent
    public static void fishPolymorphBreathe(LivingBreatheEvent event) {
        if ( !(event.getEntity() instanceof Player player) || player.isCreative() ) return;
        if ( !isFish(player) ) return;
        event.setCanBreathe(player.isInWater());
        event.setCanRefillAir(player.isInWater());
    }
}
