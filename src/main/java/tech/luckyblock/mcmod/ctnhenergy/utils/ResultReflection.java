package tech.luckyblock.mcmod.ctnhenergy.utils;

import appeng.menu.me.items.CraftingTermMenu;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

public final class ResultReflection {

    private static final String BASE =
            "appeng.integration.modules.emi.AbstractRecipeHandler$Result";

    private static final Class<?> RESULT_CLASS;
    private static final Class<?> PARTIALLY_CRAFTABLE_CLASS;

    private static final Method CREATE_FAILED;
    private static final Method CREATE_SUCCESSFUL;
    private static final Constructor<?> PARTIALLY_CRAFTABLE_CTOR;

    static {
        try {
            RESULT_CLASS = Class.forName(BASE);
            PARTIALLY_CRAFTABLE_CLASS = Class.forName(BASE + "$PartiallyCraftable");

            CREATE_FAILED = RESULT_CLASS.getDeclaredMethod(
                    "createFailed",
                    Component.class,
                    Set.class
            );
            CREATE_FAILED.setAccessible(true);

            CREATE_SUCCESSFUL = RESULT_CLASS.getDeclaredMethod(
                    "createSuccessful"
            );
            CREATE_SUCCESSFUL.setAccessible(true);

            PARTIALLY_CRAFTABLE_CTOR =
                    PARTIALLY_CRAFTABLE_CLASS.getDeclaredConstructor(
                            CraftingTermMenu.MissingIngredientSlots.class
                    );
            PARTIALLY_CRAFTABLE_CTOR.setAccessible(true);

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                    "Failed to initialize AbstractRecipeHandler.Result reflection helper",
                    e
            );
        }
    }

    private ResultReflection() {
    }

    /** Result.createFailed(Component, Set<Integer>) */
    public static Object createFailed(Component message, Set<Integer> missingSlots) {
        try {
            return CREATE_FAILED.invoke(null, message, missingSlots);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /** Result.createSuccessful() */
    public static Object createSuccessful() {
        try {
            return CREATE_SUCCESSFUL.invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /** new Result.PartiallyCraftable(missingSlots) */
    public static Object createPartiallyCraftable(CraftingTermMenu.MissingIngredientSlots missingSlots) {
        try {
            return PARTIALLY_CRAFTABLE_CTOR.newInstance(missingSlots);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}

