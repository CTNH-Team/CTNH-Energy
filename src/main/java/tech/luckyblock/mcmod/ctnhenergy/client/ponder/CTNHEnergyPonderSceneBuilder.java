package tech.luckyblock.mcmod.ctnhenergy.client.ponder;

import com.gregtechceu.gtceu.GTCEu;

import net.createmod.ponder.api.scene.SceneBuilder;

import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.vixhentx.mcmod.ctnhlib.client.ponder.CTNHPonderSceneBuilder;

public class CTNHEnergyPonderSceneBuilder extends CTNHPonderSceneBuilder {

    public CTNHEnergyPonderSceneBuilder(SceneBuilder builder) {
        super(builder, CTNHEnergy.MODID, CTNHEnergyPonderSceneBuilder::registerLang);
    }

    private static void registerLang(String key, String en, String cn) {
        if (GTCEu.isDataGen()) {
            CTNHEnergy.REGISTRATE.genLang(key, en, cn);
        }
    }
}
