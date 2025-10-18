package net.mindoth.spellmaker.util.spellform;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.client.particle.ember.ParticleColor;
import net.mindoth.shadowizardlib.network.PacketSendCustomParticles;
import net.mindoth.shadowizardlib.network.ShadowNetwork;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.DimVec3;
import net.mindoth.spellmaker.util.MultiBlockHitResult;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static net.mindoth.shadowizardlib.event.ShadowEvents.*;

public class AreaAroundCasterForm extends SpellForm {
    public AreaAroundCasterForm(String name, int cost) {
        super(name, cost);
    }

    @Override
    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        Level level = caster.level();
        AABB box = caster.getBoundingBox().inflate(1.0D, 0.0D, 1.0D);
        for ( Entity entity : level.getEntities(caster, box).stream().filter((entity -> entity instanceof LivingEntity)).toList() ) {
            for ( RuneItem rune : map.keySet() ) {
                rune.effectOnEntity(map.get(rune), new MultiEntityHitResult(caster, Collections.singletonList(entity), new DimVec3(entity.position(), entity.level())));
            }
        }
        List<BlockPos> blocks = Lists.newArrayList();
        for ( int x = caster.getBlockX() -1; x < caster.getBlockX() + 2; x++ ) {
            for ( int y = caster.getBlockY(); y < caster.getBlockY() + 2; y++ ) {
                for ( int z = caster.getBlockZ() - 1; z < caster.getBlockZ() + 2; z++ ) {
                    BlockPos newPos = new BlockPos(x, y, z);
                    if ( !newPos.equals(caster.getOnPos()) && !newPos.equals(caster.getOnPos().above()) ) blocks.add(newPos);
                }
            }
        }
        MultiBlockHitResult mResult = new MultiBlockHitResult(Direction.UP, false, blocks, new DimVec3(caster.position(), level));
        for ( RuneItem rune : map.keySet() ) rune.effectOnBlock(map.get(rune), mResult);
        addAoeParticles(true, level, box, 0.15F, 8, getColorStats());
    }

    private HashMap<String, Float> getColorStats() {
        return defaultStats();
    }

    private int getRenderType() {
        return 1;
    }

    protected void addAoeParticles(boolean targetBlocks, Level level, AABB box, float size, int age, HashMap<String, Float> stats) {
        Vec3 center = box.getCenter();
        double maxX = box.maxX;
        double minX = box.minX;
        double maxY = box.maxY;
        double minY = box.minY;
        double maxZ = box.maxZ;
        double minZ = box.minZ;

        if ( targetBlocks ) {
            int amountX = 4 * (int)box.getXsize();
            int amountY = 4 * (int)box.getYsize();
            int amountZ = 4 * (int)box.getZsize();

            //VectorPos for each corner
            Vec3 pos0 = new Vec3(minX, minY, minZ);
            Vec3 pos1 = new Vec3(maxX, minY, minZ);
            Vec3 pos2 = new Vec3(minX, minY, maxZ);
            Vec3 pos3 = new Vec3(maxX, minY, maxZ);
            Vec3 pos4 = new Vec3(minX, maxY, minZ);
            Vec3 pos5 = new Vec3(maxX, maxY, minZ);
            Vec3 pos6 = new Vec3(minX, maxY, maxZ);
            Vec3 pos7 = new Vec3(maxX, maxY, maxZ);
            //Bottom corners
            generateParticles(pos0, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos1, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos2, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos3, center, level, size, age, 0, 0, 0, stats);
            //Top corners
            generateParticles(pos4, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos5, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos6, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos7, center, level, size, age, 0, 0, 0, stats);
            //Bottom edges
            summonParticleLine(pos0, pos1, amountX, center, level, size, age, stats);
            summonParticleLine(pos0, pos2, amountZ, center, level, size, age, stats);
            summonParticleLine(pos3, pos1, amountZ, center, level, size, age, stats);
            summonParticleLine(pos3, pos2, amountX, center, level, size, age, stats);
            //Middle edges
            summonParticleLine(pos0, pos4, amountY, center, level, size, age, stats);
            summonParticleLine(pos1, pos5, amountY, center, level, size, age, stats);
            summonParticleLine(pos2, pos6, amountY, center, level, size, age, stats);
            summonParticleLine(pos3, pos7, amountY, center, level, size, age, stats);
            //Top edges
            summonParticleLine(pos4, pos5, amountX, center, level, size, age, stats);
            summonParticleLine(pos4, pos6, amountZ, center, level, size, age, stats);
            summonParticleLine(pos7, pos5, amountZ, center, level, size, age, stats);
            summonParticleLine(pos7, pos6, amountX, center, level, size, age, stats);
        }
        else {
            int amount = 4 * Math.max((int)box.getYsize(), (int)box.getXsize());
            for ( int i = 0; i < amount; i++ ) {
                double vec = 0.05D + (0.25D - 0.05D) * new Random().nextDouble();
                //double vec = 0.15D;
                generateParticles(new Vec3(maxX, center.y - 0.5D + new Random().nextDouble(), minZ + (maxZ - minZ) * new Random().nextDouble()), center, level, size, age, 0, vec, 0, stats);
                generateParticles(new Vec3(minX, center.y - 0.5D + new Random().nextDouble(), minZ + (maxZ - minZ) * new Random().nextDouble()), center, level, size, age, 0, vec, 0, stats);
                generateParticles(new Vec3(minX + (maxX - minX) * new Random().nextDouble(), center.y - 0.5D + new Random().nextDouble(), minZ), center, level, size, age, 0, vec, 0, stats);
                generateParticles(new Vec3(minX + (maxX - minX) * new Random().nextDouble(), center.y - 0.5D + new Random().nextDouble(), maxZ), center, level, size, age, 0, vec, 0, stats);
            }
        }
    }

    public void generateParticles(Vec3 pos, Vec3 center, Level level, float size, int age, double vecX, double vecY, double vecZ, HashMap<String, Float> stats) {
        ParticleColor.IntWrapper color = new ParticleColor.IntWrapper(getParticleColor(stats));
        ShadowNetwork.sendToNearby(new PacketSendCustomParticles(color.r, color.g, color.b, size, age, false, getRenderType(), pos.x, pos.y, pos.z, vecX, vecY, vecZ), level, center);
    }
}
