package com.poursip;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class PourAndSipClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        register(PourAndSip.GLASS);
        register(PourAndSip.WINE_GLASS);
    }
    private static void register(Item vessel) {
        ModelPredicateProviderRegistry.register(vessel, new Identifier(PourAndSip.MOD_ID, "fill"),
                (stack, world, entity, seed) -> switch (VesselItem.getDrink(stack)) {
                    case "water" -> 0.5F;
                    case "milk" -> 1.0F;
                    default -> 0.0F;
                });
    }
}
