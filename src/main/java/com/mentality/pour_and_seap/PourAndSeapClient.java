package com.mentality.pour_and_seap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

public class PourAndSeapClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(PourAndSeap.DRINKING_GLASS_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PourAndSeap.WINE_GLASS_BLOCK, RenderLayer.getCutout());
        ModelPredicateProviderRegistry.register(PourAndSeap.DRINKING_GLASS, PourAndSeap.id("fill"),
            (stack, world, entity, seed) -> VesselItem.getFill(stack) / 2.0f);
        ModelPredicateProviderRegistry.register(PourAndSeap.WINE_GLASS, PourAndSeap.id("fill"),
            (stack, world, entity, seed) -> VesselItem.getFill(stack) / 2.0f);
    }
}
