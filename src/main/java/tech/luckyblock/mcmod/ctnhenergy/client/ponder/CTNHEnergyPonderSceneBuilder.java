package tech.luckyblock.mcmod.ctnhenergy.client.ponder;

import net.createmod.ponder.api.scene.SceneBuilder;

import com.gregtechceu.gtceu.GTCEu;
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
