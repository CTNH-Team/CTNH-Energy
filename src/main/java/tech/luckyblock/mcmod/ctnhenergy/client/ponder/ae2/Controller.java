// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEBlocks;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class Controller {

    private Controller() {
    }

    public static void controller(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("controller", ControllerHeader.translate().getContents().toString());
        scene.world().showSection(util.select().fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.world().setBlocks(util.select().position(8, 1, 1), AEBlocks.CONTROLLER.block().defaultBlockState(), false);
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 1, 1, 1, 1, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40)
                .text(ControllerText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay().showText(60)
                .text(ControllerText2.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(0, 1, 1), Direction.WEST))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "energy", util.select().position(0, 1, 1), 60);
        scene.idle(80);
        for (int index = 0; index < 6; index++) {
            cables.showSectionAndConnect(index + 2, 1, 1, Direction.WEST);
            cables.showSectionAndConnect(1, 2 + index, 1, Direction.DOWN);
            cables.showSectionAndConnect(1, 1, 2 + index, Direction.NORTH);
            scene.idle(5);
        }
        scene.idle(10);
        scene.overlay().showText(60)
                .text(ControllerText3.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.idle(30);
        cables.showSectionAndConnect(8, 1, 1, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(ControllerText4.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.rotateCameraY(180);
        scene.idle(40);
        cables.showSectionAndConnect(3, 1, 3, 5, 1, 4, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(ControllerText5.translate().getContents().toString())
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.RED, "no_connect", util.select().fromTo(3, 1, 3, 5, 1, 4), 60);
        scene.idle(80);
        cables.showSectionAndConnect(3, 1, 5, 8, 3, 7, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(ControllerText6.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
    }
}
