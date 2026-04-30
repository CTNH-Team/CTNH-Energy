package tech.luckyblock.mcmod.ctnhenergy.client.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

public class CTNHEnergyPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return CTNHEnergy.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CTNHEnergyPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CTNHEnergyPonderTags.register(helper);
    }
}
