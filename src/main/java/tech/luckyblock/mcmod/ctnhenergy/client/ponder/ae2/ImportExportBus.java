// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEItems;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class ImportExportBus {

    private ImportExportBus() {
    }

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("import_export_bus", ImportExportBusHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(4, 1, 3, Direction.DOWN);
        cables.showSectionAndConnect(2, 1, 4, 4, 1, 4, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(ImportExportBusText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(1, 1, 2, 2, 1, 2, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(1, 1, 3, 1, 1, 4, Direction.DOWN);
        scene.rotateCameraY(-90);
        scene.idle(40);
        scene.overlay().showText(60)
                .text(ImportExportBusText2.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "export_target", util.select().position(1, 1, 3), 60);
        scene.idle(80);
        scene.overlay().showText(60)
                .text(ImportExportBusText3.translate().getContents().toString());
        scene.idle(60);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(1, 1, 3), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(40);
        scene.rotateCameraY(90);
        scene.idle(30);
        cables.showSectionAndConnect(4, 2, 3, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(1, 1, 1, Direction.NORTH);
        scene.idle(30);
        scene.overlay().showText(60)
                .text(ImportExportBusText4.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "import_source", util.select().position(2, 1, 3), 60);
        scene.idle(80);
        scene.overlay().showText(60)
                .text(ImportExportBusText5.translate().getContents().toString());
        scene.idle(60);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(40);
        cables.showSectionAndConnect(4, 1, 2, Direction.NORTH);
        scene.idle(10);
        cables.showSectionAndConnect(2, 2, 2, Direction.DOWN);
        scene.rotateCameraY(90);
        scene.idle(40);
    }

    public static void transport(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("bus_transport", BusTransportHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 0, 3, 1, 4, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(BusTransportText1.translate().getContents().toString());
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(60);
        cables.showSectionAndConnect(4, 1, 0, 4, 1, 3, Direction.DOWN);
        cables.showSectionAndConnect(1, 2, 0, 1, 2, 3, Direction.DOWN);
        scene.idle(20);
    }
}
