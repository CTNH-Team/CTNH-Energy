// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class Cable {

    private Cable() {}

    public static void cable(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("cable", CableHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(util.grid().at(0, 1, 0), util.grid().at(4, 1, 1), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(CableText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(60)
                .text(CableText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(util.grid().at(0, 1, 2), util.grid().at(4, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(CableText3.translate().getContents().toString());
        scene.idle(80);
        cables.showSectionAndConnect(util.grid().at(0, 1, 3), util.grid().at(4, 1, 3), Direction.DOWN);
        scene.overlay().showText(60)
                .text(CableText4.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void smallCable(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("small_cable", SmallCableHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(util.grid().at(0, 1, 0), Direction.DOWN);
        cables.showSectionAndConnect(util.grid().at(0, 1, 1), util.grid().at(4, 1, 1), Direction.DOWN);
        cables.showSectionAndConnect(util.grid().at(4, 1, 1), util.grid().at(4, 1, 4), Direction.DOWN);
        cables.showSectionAndConnect(util.grid().at(4, 1, 4), util.grid().at(0, 1, 4), Direction.DOWN);
        scene.overlay().showText(60)
                .text(SmallCableText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        cables.showSectionAndConnect(util.grid().at(0, 1, 2), util.grid().at(3, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "channels", util.select().fromTo(0, 1, 2, 3, 1, 3), 60);
        scene.overlay().showText(60)
                .text(SmallCableText2.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(util.grid().at(3, 1, 0), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.RED, "overload", util.select().fromTo(0, 1, 2, 3, 1, 3), 60);
        scene.overlay().showOutline(PonderPalette.RED, "extra", util.select().position(3, 1, 0), 60);
        scene.overlay().showText(60)
                .text(SmallCableText3.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void denseCable(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("dense_cable", DenseCableHeader.translate().getContents().toString());
        scene.world().showSection(util.select().fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.idle(20);
        cables.showSectionAndConnect(util.grid().at(0, 1, 0), util.grid().at(9, 1, 9), Direction.DOWN);
        scene.idle(20);
        scene.rotateCameraY(180);
        scene.idle(60);
        scene.overlay().showText(60)
                .text(DenseCableText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(60)
                .text(DenseCableText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
    }
}
