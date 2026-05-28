package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class MolecularAssembler {

    private MolecularAssembler() {}

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("molecular_assembler", MolecularAssemblerHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(0, 1, 0, 3, 1, 0), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(2, 1, 1, 2, 2, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(1, 1, 1), Direction.DOWN);
        scene.overlay().showText(60)
                .text(MolecularAssemblerText1.translate().getContents().toString());
        scene.idle(80);
        scene.world().showSection(util.select().position(1, 1, 2), Direction.DOWN);
        scene.overlay().showText(60)
                .text(MolecularAssemblerText2.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(60)
                .text(MolecularAssemblerText3.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(60)
                .text(MolecularAssemblerText4.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(0, 1, 1, 1, 2, 2), Direction.DOWN);
        scene.overlay().showText(60)
                .text(MolecularAssemblerText5.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
    }
}
