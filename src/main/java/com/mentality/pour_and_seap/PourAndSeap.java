package com.mentality.pour_and_seap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PourAndSeap implements ModInitializer {
    public static final String MOD_ID = "pour_and_seap";

    public static final VesselBlock DRINKING_GLASS_BLOCK = Registry.register(
        Registries.BLOCK, id("drinking_glass"),
        new VesselBlock(AbstractBlock.Settings.create().strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque(), false));
    public static final VesselBlock WINE_GLASS_BLOCK = Registry.register(
        Registries.BLOCK, id("wine_glass"),
        new VesselBlock(AbstractBlock.Settings.create().strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque(), true));

    // Empty glasses are vanilla-stacking items. Filled glasses are separate, hidden
    // registered variants with maxCount(1): no ItemStack/Inventory core mixin needed.
    public static final VesselItem DRINKING_GLASS = register("drinking_glass", DRINKING_GLASS_BLOCK, 0);
    public static final VesselItem WINE_GLASS = register("wine_glass", WINE_GLASS_BLOCK, 0);
    public static final VesselItem DRINKING_GLASS_WATER = register("drinking_glass_water", DRINKING_GLASS_BLOCK, 1);
    public static final VesselItem DRINKING_GLASS_MILK = register("drinking_glass_milk", DRINKING_GLASS_BLOCK, 2);
    public static final VesselItem WINE_GLASS_WATER = register("wine_glass_water", WINE_GLASS_BLOCK, 1);
    public static final VesselItem WINE_GLASS_MILK = register("wine_glass_milk", WINE_GLASS_BLOCK, 2);

    private static VesselItem register(String path, VesselBlock block, int fill) {
        return Registry.register(Registries.ITEM, id(path),
            new VesselItem(block, new FabricItemSettings().maxCount(fill == 0 ? 16 : 1), fill));
    }

    public static VesselItem vesselItem(VesselBlock block, int fill) {
        boolean wine = block == WINE_GLASS_BLOCK;
        return switch (fill) {
            case 1 -> wine ? WINE_GLASS_WATER : DRINKING_GLASS_WATER;
            case 2 -> wine ? WINE_GLASS_MILK : DRINKING_GLASS_MILK;
            default -> wine ? WINE_GLASS : DRINKING_GLASS;
        };
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static final ItemGroup GROUP = Registry.register(
        Registries.ITEM_GROUP, id("main"),
        FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.pour_and_seap.main"))
            .icon(() -> new ItemStack(DRINKING_GLASS))
            .entries((context, entries) -> {
                entries.add(DRINKING_GLASS);
                entries.add(WINE_GLASS);
            }).build());

    @Override
    public void onInitialize() {
        // The four filled variants intentionally remain hidden from Creative tabs.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(DRINKING_GLASS);
            entries.add(WINE_GLASS);
        });
    }
}
