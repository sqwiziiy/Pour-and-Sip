package com.poursip;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PourAndSip implements ModInitializer {
    public static final String MOD_ID = "poursip";

    // Own registered items. No Candlelight item/block IDs are touched.
    public static final Item GLASS = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "drinking_glass"),
            new VesselItem(new FabricItemSettings().maxCount(1), true));
    public static final Item WINE_GLASS = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "wine_glass"),
            new VesselItem(new FabricItemSettings().maxCount(1), false));

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(GLASS);
            entries.add(WINE_GLASS);
        });
    }
}
