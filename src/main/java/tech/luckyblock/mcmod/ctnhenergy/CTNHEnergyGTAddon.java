package tech.luckyblock.mcmod.ctnhenergy;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.resources.ResourceLocation;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEBlocks;

import java.util.function.Consumer;

@GTAddon
public class CTNHEnergyGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return CTNHEnergy.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        CEBlocks.init();
    }

    @Override
    public String addonModId() {
        return CTNHEnergy.MODID;
    }
}
