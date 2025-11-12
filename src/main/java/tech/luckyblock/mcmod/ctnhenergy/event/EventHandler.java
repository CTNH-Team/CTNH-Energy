package tech.luckyblock.mcmod.ctnhenergy.event;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import net.minecraft.resources.ResourceLocation;
import tech.luckyblock.mcmod.ctnhenergy.registry.CERecipeTypes;

public class EventHandler {
    public static void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        CERecipeTypes.init();
    }
}
