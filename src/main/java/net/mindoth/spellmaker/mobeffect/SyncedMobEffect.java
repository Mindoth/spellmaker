package net.mindoth.spellmaker.mobeffect;

import net.minecraft.world.entity.LivingEntity;

public interface SyncedMobEffect {
    //Thanks Iron
    //https://github.com/iron431/irons-spells-n-spellbooks/blob/3f69fb620314304ca2980de793f2d10016cc116e/src/main/java/io/redspace/ironsspellbooks/effect/IMobEffectEndCallback.java#L7
    void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier);
}
