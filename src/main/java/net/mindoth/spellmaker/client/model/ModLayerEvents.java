package net.mindoth.spellmaker.client.model;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.armor.ModArmorItem;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
public class ModLayerEvents {

    public static final ArmorModelSet<ModelLayerLocation> SIMPLE_ROBE = registerArmorSet("simple_robe");

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SIMPLE_ROBE.head(), SimpleRobeModel::createHeadLayer);
        event.registerLayerDefinition(SIMPLE_ROBE.chest(), SimpleRobeModel::createBodyLayer);
        event.registerLayerDefinition(SIMPLE_ROBE.legs(), SimpleRobeModel::createLegsLayer);
        event.registerLayerDefinition(SIMPLE_ROBE.feet(), SimpleRobeModel::createBootsLayer);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions armor = new IClientItemExtensions() {
            @Override
            @NotNull
            public Model getHumanoidArmorModel(@NotNull ItemStack stack, @NotNull EquipmentClientInfo.LayerType type, @NotNull Model original) {
                if ( original instanceof HumanoidModel humanoidModel && stack.getItem() instanceof ModArmorItem item ) {
                    SimpleRobeModel modArmor = new SimpleRobeModel(SimpleRobeModel.createLayerByType(item.type).bakeRoot());
                    ClientHooks.copyModelProperties(humanoidModel, modArmor);
                    return modArmor;
                }
                else return original;
            }
        };
        List<Item> items = Lists.newArrayList();
        for ( Item item : BuiltInRegistries.ITEM ) if ( item instanceof ModArmorItem ) items.add(item);
        event.registerItem(armor, items.toArray(new Item[0]));
    }

    public static ArmorModelSet<ModelLayerLocation> registerArmorSet(String path) {
        return new ArmorModelSet(registerModel(path, "helmet"), registerModel(path, "chestplate"), registerModel(path, "leggings"), registerModel(path, "boots"));
    }

    public static ModelLayerLocation registerModel(String path, String model) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, path), model);
    }
}
