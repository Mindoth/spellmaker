package net.mindoth.spellmaker.util.spellform;

import net.mindoth.shadowizardlib.client.particle.ember.ParticleColor;
import net.mindoth.shadowizardlib.network.PacketSendCustomParticles;
import net.mindoth.shadowizardlib.network.ShadowNetwork;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.DimVec3;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static net.mindoth.shadowizardlib.event.ShadowEvents.defaultStats;
import static net.mindoth.shadowizardlib.event.ShadowEvents.getParticleColor;

public class CasterOnlyForm extends SpellForm {
    public CasterOnlyForm(String name) {
        super(name);
    }

    @Override
    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        for ( RuneItem rune : map.keySet() ) {
            rune.effectOnEntity(map.get(rune), new MultiEntityHitResult(caster, Collections.singletonList(caster), new DimVec3(caster.position(), caster.level())));
            addEnchantParticles(caster, 0.15F, getColorStats());
        }
    }

    private HashMap<String, Float> getColorStats() {
        return defaultStats();
    }

    private int getRenderType() {
        return 1;
    }

    protected void addEnchantParticles(Entity target, float size, HashMap<String, Float> stats) {
        double var = 0.15D;
        double maxX = target.getBoundingBox().maxX + var;
        double minX = target.getBoundingBox().minX - var;
        double maxZ = target.getBoundingBox().maxZ + var;
        double minZ = target.getBoundingBox().minZ - var;
        double vecX = 0.0D;
        double minVecY = 0.10D;
        double maxVecY = 0.60D;
        double vecZ = 0.0D;
        for ( int i = 0; i < 4; i++ ) {
            double vecY = minVecY + (maxVecY - minVecY) * new Random().nextDouble();
            int age = 11 - (int)((vecY - 0.1D) * 20);
            double randX = maxX;
            double randY = target.getY() + ((target.getY() + (target.getBbHeight() / 2)) - target.getY()) * new Random().nextDouble();
            double randZ = minZ + (maxZ - minZ) * new Random().nextDouble();
            Vec3 pos = new Vec3(randX, randY, randZ);
            ParticleColor.IntWrapper color = new ParticleColor.IntWrapper(getParticleColor(stats));
            ShadowNetwork.sendToPlayersTrackingEntity(new PacketSendCustomParticles(color.r, color.g, color.b, size, age, false, getRenderType(),
                    pos.x, pos.y, pos.z, vecX, vecY, vecZ), target, true);
        }
        for ( int i = 0; i < 4; i++ ) {
            double vecY = minVecY + (maxVecY - minVecY) * new Random().nextDouble();
            int age = 11 - (int)((vecY - 0.1D) * 20);
            double randX = minX;
            double randY = target.getY() + ((target.getY() + (target.getBbHeight() / 2)) - target.getY()) * new Random().nextDouble();
            double randZ = minZ + (maxZ - minZ) * new Random().nextDouble();
            Vec3 pos = new Vec3(randX, randY, randZ);
            ParticleColor.IntWrapper color = new ParticleColor.IntWrapper(getParticleColor(stats));
            ShadowNetwork.sendToPlayersTrackingEntity(new PacketSendCustomParticles(color.r, color.g, color.b, size, age, false, getRenderType(),
                    pos.x, pos.y, pos.z, vecX, vecY, vecZ), target, true);
        }
        for ( int i = 0; i < 4; i++ ) {
            double vecY = minVecY + (maxVecY - minVecY) * new Random().nextDouble();
            int age = 11 - (int)((vecY - 0.1D) * 20);
            double randX = minX + (maxX - minX) * new Random().nextDouble();
            double randY = target.getY() + ((target.getY() + (target.getBbHeight() / 2)) - target.getY()) * new Random().nextDouble();
            double randZ = minZ;
            Vec3 pos = new Vec3(randX, randY, randZ);
            ParticleColor.IntWrapper color = new ParticleColor.IntWrapper(getParticleColor(stats));
            ShadowNetwork.sendToPlayersTrackingEntity(new PacketSendCustomParticles(color.r, color.g, color.b, size, age, false, getRenderType(),
                    pos.x, pos.y, pos.z, vecX, vecY, vecZ), target, true);
        }
        for ( int i = 0; i < 4; i++ ) {
            double vecY = minVecY + (maxVecY - minVecY) * new Random().nextDouble();
            int age = 11 - (int)((vecY - 0.1D) * 20);
            double randX = minX + (maxX - minX) * new Random().nextDouble();
            double randY = target.getY() + ((target.getY() + (target.getBbHeight() / 2)) - target.getY()) * new Random().nextDouble();
            double randZ = maxZ;
            Vec3 pos = new Vec3(randX, randY, randZ);
            ParticleColor.IntWrapper color = new ParticleColor.IntWrapper(getParticleColor(stats));
            ShadowNetwork.sendToPlayersTrackingEntity(new PacketSendCustomParticles(color.r, color.g, color.b, size, age, false, getRenderType(),
                    pos.x, pos.y, pos.z, vecX, vecY, vecZ), target, true);
        }
    }
}
