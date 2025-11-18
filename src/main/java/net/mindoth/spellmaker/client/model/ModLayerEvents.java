package net.mindoth.spellmaker.client.model;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.armor.ArcaneRobeItem;
import net.mindoth.spellmaker.item.armor.ModArmorItem;
import net.mindoth.spellmaker.item.armor.WoolRobeItem;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

    public static final ArmorModelSet<ModelLayerLocation> WOOL_ROBE = registerArmorSet("wool_robe");
    public static final ArmorModelSet<ModelLayerLocation> ARCANE_ROBE = registerArmorSet("arcane_robe");

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WOOL_ROBE.head(), () -> WoolRobeModel.createHeadLayer(true));
        event.registerLayerDefinition(WOOL_ROBE.head(), () -> WoolRobeModel.createHeadLayer(false));
        event.registerLayerDefinition(WOOL_ROBE.chest(), WoolRobeModel::createBodyLayer);
        event.registerLayerDefinition(WOOL_ROBE.legs(), WoolRobeModel::createLegsLayer);
        event.registerLayerDefinition(WOOL_ROBE.feet(), WoolRobeModel::createBootsLayer);

        event.registerLayerDefinition(ARCANE_ROBE.head(), () -> ArcaneRobeModel.createHeadLayer(true));
        event.registerLayerDefinition(ARCANE_ROBE.head(), () -> ArcaneRobeModel.createHeadLayer(false));
        event.registerLayerDefinition(ARCANE_ROBE.chest(), ArcaneRobeModel::createBodyLayer);
        event.registerLayerDefinition(ARCANE_ROBE.legs(), ArcaneRobeModel::createLegsLayer);
        event.registerLayerDefinition(ARCANE_ROBE.feet(), ArcaneRobeModel::createBootsLayer);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions armor = new IClientItemExtensions() {
            //Hope they add back entity parameter, so I can check for things like if the entity wearing armor is a baby
            @Override
            @NotNull
            public Model getHumanoidArmorModel(@NotNull ItemStack stack, @NotNull EquipmentClientInfo.LayerType type, @NotNull Model original) {
                if ( original instanceof HumanoidModel humanoidModel ) {
                    if ( stack.getItem() instanceof WoolRobeItem item ) {
                        boolean isHood = item == ModItems.WOOL_ROBE_HOOD.get();
                        WoolRobeModel model = new WoolRobeModel(WoolRobeModel.createLayerByType(item.type, isHood).bakeRoot());
                        ClientHooks.copyModelProperties(humanoidModel, model);
                        return model;
                    }
                    if ( stack.getItem() instanceof ArcaneRobeItem item ) {
                        boolean isHood = item == ModItems.ARCANE_ROBE_HOOD.get();
                        ArcaneRobeModel model = new ArcaneRobeModel(ArcaneRobeModel.createLayerByType(item.type, isHood).bakeRoot());
                        ClientHooks.copyModelProperties(humanoidModel, model);
                        return model;
                    }
                }
                return original;
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
