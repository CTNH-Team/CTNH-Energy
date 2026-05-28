// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class MolecularAssembler {

    private MolecularAssembler() {}

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("molecular_assembler", MolecularAssemblerHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 0, 3, 1, 0, Direction.DOWN);
        cables.showSectionAndConnect(2, 1, 1, 2, 2, 2, Direction.DOWN);
        cables.showSectionAndConnect(1, 1, 1, Direction.DOWN);
        scene.overlay().showText(60)
                .text(MolecularAssemblerText1.translate().getContents().toString());
        scene.idle(80);
        cables.showSectionAndConnect(1, 1, 2, Direction.DOWN);
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
        cables.showSectionAndConnect(0, 1, 1, 1, 2, 2, Direction.DOWN);
        scene.overlay().showText(60)
                .text(MolecularAssemblerText5.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
    }
}
