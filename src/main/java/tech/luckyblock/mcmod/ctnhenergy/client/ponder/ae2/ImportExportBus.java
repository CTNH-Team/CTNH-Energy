// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEItems;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;


public class ImportExportBus {

    private ImportExportBus() {
    }

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("import_export_bus", "Using Import and Export Buses", "输入总线与输出总线的使用");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(4, 1, 3, Direction.DOWN);
        cables.showSectionAndConnect(2, 1, 4, 4, 1, 4, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "Import and export buses directly insert and extract from the network", "输入总线和输出总线可对网络进行直接的存入和取出")
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(1, 1, 2, 2, 1, 2, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(1, 1, 3, 1, 1, 4, Direction.DOWN);
        scene.rotateCameraY(-90);
        scene.idle(40);
        scene.showText(60, "The export bus outputs items from the network to a target container", "输出总线可将网络中的物品输入到目标容器中")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "export_target", util.select().position(1, 1, 3), 60);
        scene.idle(80);
        scene.showText(60, "Configure the export bus filter...", "设置输出总线的过滤……");
        scene.idle(60);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(1, 1, 3), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(40);
        scene.rotateCameraY(90);
        scene.idle(30);
        cables.showSectionAndConnect(4, 2, 3, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(1, 1, 1, Direction.NORTH);
        scene.idle(30);
        scene.showText(60, "The import bus inputs items from a container into the network", "输入总线可将容器中的物品输入网络")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "import_source", util.select().position(2, 1, 3), 60);
        scene.idle(80);
        scene.showText(60, "Configure the import bus filter...", "设置输入总线的过滤……");
        scene.idle(60);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP), Pointing.DOWN, 40)
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
        scene.title("bus_transport", "Bus Logistics with Storage Buses", "使用输入/输出总线配合存储总线进行物流");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 0, 3, 1, 4, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "Combine import/export buses with storage buses for simple logistics", "使用输入/输出总线配合存储总线可制作简易的物流");
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(60);
        cables.showSectionAndConnect(4, 1, 0, 4, 1, 3, Direction.DOWN);
        cables.showSectionAndConnect(1, 2, 0, 1, 2, 3, Direction.DOWN);
        scene.idle(20);
    }
}
