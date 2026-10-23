package com.mentality.pour_and_seap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Optional Homeostatic 1.20.1 compatibility.
 *
 * We never define hydration values ourselves. We pass the original consumed
 * item to Homeostatic's public WaterHelper.drink(ServerPlayer, ItemStack, boolean),
 * which reads the server's current datapack definitions, saturation, water
 * quality and effect chances. No runtime dependency when Homeostatic is absent.
 */
public final class HomeostaticCompat {
    private static final Logger LOGGER = LoggerFactory.getLogger("pour_and_seap/Homeostatic");
    private static boolean resolved;
    private static Method drinkMethod;

    private HomeostaticCompat() {}

    private static synchronized Method method() {
        if (resolved) return drinkMethod;
        resolved = true;
        if (!FabricLoader.getInstance().isModLoaded("homeostatic")) return null;

        try {
            Class<?> waterHelper = Class.forName("homeostatic.util.WaterHelper");
            drinkMethod = waterHelper.getMethod(
                "drink", ServerPlayerEntity.class, ItemStack.class, boolean.class);
        } catch (ReflectiveOperationException | LinkageError ex) {
            LOGGER.warn("Homeostatic is loaded but the 1.20.1 hydration API is incompatible; " +
                        "Pour & Seap will not change thirst.", ex);
        }
        return drinkMethod;
    }

    public static void onDrink(PlayerEntity player, int liquid, ItemStack originalWine) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        ItemStack source = switch (liquid) {
            case 1 -> PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
            case 2 -> new ItemStack(Items.MILK_BUCKET);
            case 3, 4 -> originalWine.isEmpty() ? ItemStack.EMPTY : originalWine.copyWithCount(1);
            default -> ItemStack.EMPTY;
        };
        if (source.isEmpty()) return;

        Method method = method();
        if (method == null) return;

        try {
            // Homeostatic defines the actual amount, saturation and thirst effects.
            method.invoke(null, serverPlayer, source, true);
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
            LOGGER.warn("Failed to apply Homeostatic hydration for {}.", source.getItem(), ex);
        }
    }
}
