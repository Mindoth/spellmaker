package net.mindoth.spellmaker.item.castingitem;

import net.mindoth.spellmaker.capability.ModCapabilities;
import net.mindoth.spellmaker.capability.playermagic.PlayerMagickProvider;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class StaffItem extends Item {
    public StaffItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.fail(player.getItemInHand(hand));
        if ( !level.isClientSide ) {
            ItemStack staff = player.getItemInHand(hand);
            if ( !player.getCooldowns().isOnCooldown(staff.getItem()) ) {
                castMagick(player, staff);
                result = InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }
        return result;
    }

    private static void castMagick(Player player, ItemStack staff) {
        ItemStack book = SpellBookItem.getSpellBookSlot(player);
        if ( !book.isEmpty() && book.hasTag() && book.getTag().contains(SpellBookItem.NBT_KEY_BOOK_SLOT)
                && book.getTag().getInt(SpellBookItem.NBT_KEY_BOOK_SLOT) >= 0 && SpellBookItem.getActiveScrollFromBook(book) != null ) {
            ItemStack scroll = SpellBookItem.getActiveScrollFromBook(book);
            if ( scroll != null && scroll.hasTag() ) {
                SpellForm form = DataHelper.getFormFromNbt(scroll.getTag());
                LinkedHashMap<RuneItem, List<Integer>> map = DataHelper.createMapFromTag(scroll.getTag());
                int cost = ParchmentItem.calculateSpellCost(form, map);
                player.getCapability(PlayerMagickProvider.PLAYER_MAGICK).ifPresent(magic -> {
                    if ( cost <= magic.getCurrentMana() || player.isCreative() ) {
                        form.castMagick(player, map);
                        handleCooldowns(player, staff, 20);
                        if ( !player.isCreative() ) {
                            addItemDamage(staff, 1, player);
                            ModCapabilities.changeMana(player, -cost);
                        }
                    }
                    else if ( !player.isCreative() ) {
                        handleCooldowns(player, staff, 20);
                        whiffSpell(player);
                    }
                });
            }
        }
        else {
            handleCooldowns(player, staff, 20);
            whiffSpell(player);
        }
    }

    /*@Override
    public void onUseTick(Level level, LivingEntity caster, ItemStack staff, int timeLeft) {
        if ( level.isClientSide ) return;
        if ( !(caster instanceof ServerPlayer player) ) return;
        if ( player.getCooldowns().isOnCooldown(staff.getItem()) ) return;
        castMagick(player, staff);
    }*/

    private static void handleCooldowns(LivingEntity caster, ItemStack staff, int cooldown) {
        caster.stopUsingItem();
        addCastingCooldown(caster, staff.getItem(), cooldown);
    }

    private static void addItemDamage(ItemStack castingItem, int amount, LivingEntity living) {
        castingItem.hurtAndBreak(amount, living, (holder) -> holder.broadcastBreakEvent(living.getUsedItemHand()));
    }

    public static void whiffSpell(Entity caster) {
        addWhiffParticles(caster);
        playWhiffSound(caster);
    }

    private static void addWhiffParticles(Entity caster) {
        if ( !caster.level().isClientSide && caster.level() instanceof ServerLevel level ) {
            Vec3 start = caster.getEyePosition().add(caster.getLookAngle().multiply(0.5D, 0.5D, 0.5D));
            Vec3 dir = caster.getLookAngle().multiply(2, 2, 2);
            for ( int i = 0; i < 6; i++ ) {
                double variable = 0.5D;
                double randX = new Random().nextDouble(variable - -variable) + -variable;
                double randY = new Random().nextDouble(variable - -variable) + -variable;
                double randZ = new Random().nextDouble(variable - -variable) + -variable;
                level.sendParticles(ParticleTypes.ASH, start.x, start.y, start.z, 0, dir.x + randX, dir.y + randY, dir.z + randZ, 0.1D);
            }
        }
    }

    private static void playWhiffSound(Entity caster) {
        if ( caster instanceof Player player ) player.playNotifySound(SoundEvents.NOTE_BLOCK_SNARE.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
    }

    private static void addCastingCooldown(Entity entity, Item item, int cooldown) {
        if ( entity instanceof Player player ) player.getCooldowns().addCooldown(item, cooldown);
    }

    public static boolean isValidCastingItem(ItemStack staff) {
        return staff.getItem() instanceof StaffItem;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
