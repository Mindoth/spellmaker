package net.mindoth.spellmaker.item.weapon;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.capability.ModCapabilities;
import net.mindoth.spellmaker.capability.playermagic.PlayerMagickProvider;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class StaffItem extends Item {
    public StaffItem(Properties pProperties) {
        super(pProperties);
    }

    @SubscribeEvent
    public static void disableStaffInteraction(PlayerInteractEvent.EntityInteract event) {
        if ( isValidCastingItem(event.getEntity().getItemInHand(event.getHand())) ) event.setCanceled(true);
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
                AbstractSpellForm form = DataHelper.getFormFromNbt(scroll.getTag());
                LinkedHashMap<SigilItem, List<Integer>> map = DataHelper.createMapFromTag(scroll.getTag());
                double baseCost = ParchmentItem.calculateSpellCost(form, map);
                double discount = player.getAttributeValue(ModAttributes.MANA_COST_MULTIPLIER.get()) - ModAttributes.MANA_COST_MULTIPLIER.get().getDefaultValue();
                int cost = Mth.ceil(baseCost * (1.0D - discount));
                player.getCapability(PlayerMagickProvider.PLAYER_MAGICK).ifPresent(magic -> {
                    if ( cost <= magic.getCurrentMana() || player.isCreative() ) {
                        form.castMagick(player, map);
                        handleCooldowns(player, staff, 20);
                        if ( !player.isCreative() ) {
                            addItemDamage(staff, 1, player);
                            ModCapabilities.changeMana(player, -cost);
                        }
                        playCastingSound(player);
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
            Vec3 start = caster.getEyePosition().add(caster.getLookAngle().multiply(1.0D, 1.0D, 1.0D));
            Vec3 dir = caster.getLookAngle();
            for ( int i = 0; i < 6; i++ ) {
                level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD.defaultBlockState()),
                        start.x, start.y, start.z, 0, dir.x, dir.y, dir.z, 0);
            }
        }
    }

    private static void playWhiffSound(Entity caster) {
        if ( caster instanceof Player player && !player.level().isClientSide && player.level() instanceof ServerLevel level ) {
            player.playNotifySound(SoundEvents.NOTE_BLOCK_SNARE.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
            level.playSound(player, player.getOnPos(), SoundEvents.NOTE_BLOCK_SNARE.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    private static void playCastingSound(Entity caster) {
        if ( caster instanceof Player player && !player.level().isClientSide && player.level() instanceof ServerLevel level ) {
            player.playNotifySound(SoundEvents.ENDER_PEARL_THROW, SoundSource.PLAYERS, 0.5F, 1.0F);
            level.playSound(player, player.getOnPos(), SoundEvents.ENDER_PEARL_THROW, SoundSource.PLAYERS, 0.5F, 1.0F);
            player.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 2.0F);
            level.playSound(player, player.getOnPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 2.0F);
        }
    }

    private static void addCastingCooldown(Entity entity, Item item, int cooldown) {
        if ( entity instanceof Player player ) player.getCooldowns().addCooldown(item, cooldown);
    }

    public static boolean isValidCastingItem(ItemStack staff) {
        return staff.getItem() instanceof StaffItem;
    }

    public static @Nonnull ItemStack getHeldCastingItem(LivingEntity playerEntity) {
        ItemStack staff = isValidCastingItem(playerEntity.getMainHandItem()) ? playerEntity.getMainHandItem() : null;
        return staff == null ? (isValidCastingItem(playerEntity.getOffhandItem()) ? playerEntity.getOffhandItem() : ItemStack.EMPTY) : staff;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
