package com.mentality.pour_and_seap;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

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
 * Optional server-side integration for Homeostatic 1.20.1.
 *
 * Never hardcode hydration: give Homeostatic the exact vanilla source or
 * preserved wine ItemStack and let its own datapacks determine the effect.
 *
 * IMPORTANT: do not use Class.getMethod() on homeostatic.util.WaterHelper.
 * That common class also has method signatures referencing Minecraft's
 * client-only Gui and GuiGraphics. Reflection can resolve those signatures
 * eagerly, causing NoClassDefFoundError on a dedicated server.
 * MethodHandles.findStatic resolves only the requested hydration method.
 */
public final class HomeostaticCompat {
    private static final Logger LOGGER = LoggerFactory.getLogger("pour_and_seap/Homeostatic");
    private static boolean resolved;
    private static MethodHandle drinkHandle;

    private HomeostaticCompat() {}

    private static synchronized MethodHandle resolveDrinkHandle() {
        if (resolved) return drinkHandle;
        resolved = true;

        if (!FabricLoader.getInstance().isModLoaded("homeostatic")) {
            LOGGER.info("Homeostatic is not installed on this server; glassware hydration is disabled.");
            return null;
        }

        try {
            Class<?> waterHelper = Class.forName("homeostatic.util.WaterHelper", false,
                HomeostaticCompat.class.getClassLoader());
            drinkHandle = MethodHandles.publicLookup().findStatic(
                waterHelper, "drink",
                MethodType.methodType(void.class, ServerPlayerEntity.class, ItemStack.class, boolean.class)
            );
            LOGGER.info("Homeostatic hydration bridge initialized (server-side WaterHelper).");
        } catch (ReflectiveOperationException | LinkageError ex) {
            LOGGER.error("Could not connect to Homeostatic 1.20.1 WaterHelper on this server. " +
                         "Check that the server has the correct Fabric Homeostatic version.", ex);
        }
        return drinkHandle;
    }

    public static void onDrink(PlayerEntity player, int liquid, ItemStack originalWine) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        ItemStack originalDrink = switch (liquid) {
            case 1 -> PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
            case 2 -> new ItemStack(Items.MILK_BUCKET);
            case 3, 4 -> originalWine.isEmpty() ? ItemStack.EMPTY : originalWine.copyWithCount(1);
            default -> ItemStack.EMPTY;
        };
        if (originalDrink.isEmpty()) return;

        MethodHandle hydration = resolveDrinkHandle();
        if (hydration == null) return;

        try {
            // Homeostatic's own datapacks control amount, saturation and effects.
            hydration.invokeExact(serverPlayer, originalDrink, true);
        } catch (Throwable ex) {
            if (ex instanceof VirtualMachineError fatal) throw fatal;
            if (ex instanceof ThreadDeath fatal) throw fatal;
            LOGGER.error("Homeostatic refused hydration for {} on dedicated server.",
                originalDrink.getItem(), ex);
        }
    }
}
