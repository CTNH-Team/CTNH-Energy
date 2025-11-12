package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

public class CERecipeTypes {
    public static GTRecipeType QUANTUM_COMPUTER;

    public static void init() {
        QUANTUM_COMPUTER = REGISTRATE.recipeType(CTNHEnergy.id("quantum_computer"), GTRecipeTypes.ELECTRIC)
                .cnlang("量子超算")
                .lang("Quantum Supercomputer")
                .setEUIO(IO.IN);
    }
}
