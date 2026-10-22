package com.mentality.pour_and_seap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;
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

    public static final VesselItem DRINKING_GLASS = Registry.register(
        Registries.ITEM, id("drinking_glass"),
        new VesselItem(DRINKING_GLASS_BLOCK, new FabricItemSettings().maxCount(1)));
    public static final VesselItem WINE_GLASS = Registry.register(
        Registries.ITEM, id("wine_glass"),
        new VesselItem(WINE_GLASS_BLOCK, new FabricItemSettings().maxCount(1)));

    public static final ItemGroup GROUP = Registry.register(
        Registries.ITEM_GROUP, id("main"),
        FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.pour_and_seap.main"))
            .icon(() -> new ItemStack(DRINKING_GLASS))
            .entries((context, entries) -> {
                entries.add(DRINKING_GLASS);
                entries.add(WINE_GLASS);
            }).build());

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(DRINKING_GLASS);
            entries.add(WINE_GLASS);
        });
    }
}
