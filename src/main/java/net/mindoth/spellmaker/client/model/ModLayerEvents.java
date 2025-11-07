package net.mindoth.spellmaker.client.model;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.armor.ModArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.List;

@EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
public class ModLayerEvents {

    public static final ModelLayerLocation SIMPLE_ROBE_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "simple_robe"), "main");

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SIMPLE_ROBE_LAYER, SimpleRobeModel::createLayer);
    }



    public static SimpleRobeModel<HumanoidRenderState> SIMPLE_ROBE_MODEL = null;

    @SubscribeEvent
    public static void registerModels(EntityRenderersEvent.AddLayers event) {
        SIMPLE_ROBE_MODEL = new SimpleRobeModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(SIMPLE_ROBE_LAYER));
    }



    @SubscribeEvent
    public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
        List<Item> items = Lists.newArrayList();
        for ( Item item : BuiltInRegistries.ITEM ) if ( item instanceof ModArmorItem ) items.add(item);
        event.registerItem(ModItemExtension.INSTANCE, items.toArray(new Item[0]));
    }
}
