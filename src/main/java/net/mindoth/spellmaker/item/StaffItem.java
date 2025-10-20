package net.mindoth.spellmaker.item;

import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.server.level.ServerPlayer;
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

import javax.annotation.Nonnull;

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
            if ( !player.getCooldowns().isOnCooldown(staff.getItem()) ) player.startUsingItem(hand);
        }
        return result;
    }

    @Override
    public void onUseTick(Level level, LivingEntity caster, ItemStack staff, int timeLeft) {
        if ( level.isClientSide ) return;
        if ( !(caster instanceof ServerPlayer serverPlayer) ) return;
        if ( serverPlayer.getCooldowns().isOnCooldown(staff.getItem()) ) return;
        ItemStack book = SpellBookItem.getSpellBookSlot(serverPlayer);
        if ( !book.isEmpty() && book.hasTag() && book.getTag().contains(SpellBookItem.NBT_KEY_BOOK_SLOT)
                && SpellBookItem.getActiveScrollFromBook(book) != null && book.getTag().getInt(SpellBookItem.NBT_KEY_BOOK_SLOT) >= 0 ) {
            ItemStack scroll = SpellBookItem.getActiveScrollFromBook(book);
            if ( scroll != null && scroll.hasTag() ) {
                SpellForm form = DataHelper.getFormFromNbt(scroll.getTag());
                form.castMagick(serverPlayer, DataHelper.createMapFromTag(scroll.getTag()));
                handleCooldowns(caster, staff, 20);
                if ( !(caster instanceof Player player && player.isCreative()) ) addItemDamage(staff, 1, caster);
            }
        }
        else {
            handleCooldowns(caster, staff, 20);
            playWhiffSound(caster);
        }
    }

    private static void handleCooldowns(LivingEntity caster, ItemStack staff, int cooldown) {
        caster.stopUsingItem();
        addCastingCooldown(caster, staff.getItem(), cooldown);
    }

    public static void addItemDamage(ItemStack castingItem, int amount, LivingEntity living) {
        castingItem.hurtAndBreak(amount, living, (holder) -> holder.broadcastBreakEvent(living.getUsedItemHand()));
    }

    public static void playWhiffSound(Entity caster) {
        if ( caster instanceof Player player ) player.playNotifySound(SoundEvents.NOTE_BLOCK_SNARE.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
    }

    public static void addCastingCooldown(Entity entity, Item item, int cooldown) {
        if ( entity instanceof Player player ) player.getCooldowns().addCooldown(item, cooldown);
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
