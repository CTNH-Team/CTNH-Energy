// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

public class IOPort {

    private IOPort() {}

    public static void ioPort(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("io_port", "Using the IO Port", "使用IO端口整理存储");
        scene.world().showSection(util.select().fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.idle(20);
        cables.showSectionAndConnect(3, 1, 4, 5, 1, 4, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "io_port", util.select().position(4, 1, 4), 60);
        scene.showText(60, "The IO port can transfer items between storage cells and the network",
                "ME IO端口能够将其中的存储元件内的东西导入到网络，或者将网络内的存储内容导入元件")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 4), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(0, 1, 0, 4, 1, 3, Direction.DOWN);
        scene.showText(60, "You can export network items into storage cells", "你可以使用它将网络内的物品导出到存储元件")
                .attachKeyFrame();
        scene.idle(60);
        cables.hideSectionAndDisconnect(0, 1, 0, 4, 1, 3, Direction.UP);
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(20);
        cables.showSectionAndConnect(4, 1, 5, 6, 1, 8, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "Use the IO port to organize storage cell contents in drives",
                "使用ME IO端口来整理驱动器中存储元件的内容似乎也是一个不错的选择")
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void output(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("io_port_output", "Mass Output with IO Port", "使用IO端口输出大量物品");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(1, 1, 4, 3, 1, 4, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "The IO port transfers items very quickly...", "IO端口的输入与输出速度非常的快……")
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(2, 1, 3, 2, 1, 2, Direction.DOWN);
        scene.showText(60, "Making it ideal for producing matter balls and singularities", "因此你可以使用它来生产物质球和奇点")
                .attachKeyFrame();
        scene.idle(20);
        scene.rotateCameraY(180);
        scene.idle(40);
    }
}
