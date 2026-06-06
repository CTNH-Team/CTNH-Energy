// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

public class Cable {

    private Cable() {}

    public static void cable(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("cable", "Cables in AE", "AE中的线缆");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(util.grid().at(0, 1, 0), util.grid().at(4, 1, 1), Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "These are AE cables...", "这些是AE的线缆……")
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "All cables of the same color can connect to each other", "所有同色的线缆都可以互相连接")
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(util.grid().at(0, 1, 2), util.grid().at(4, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "Cables of any color can connect to fluix cables", "所有颜色的线缆都可以和福鲁伊克斯线缆相连接");
        scene.idle(80);
        cables.showSectionAndConnect(util.grid().at(0, 1, 3), util.grid().at(4, 1, 3), Direction.DOWN);
        scene.showText(60, "Cables of different colors will not connect to each other", "染色后不同颜色的线缆都不会连接")
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void smallCable(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("small_cable", "Glass and Covered Cables", "玻璃线缆与包层线缆");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(util.grid().at(0, 1, 0), Direction.DOWN);
        cables.showSectionAndConnect(util.grid().at(0, 1, 1), util.grid().at(4, 1, 1), Direction.DOWN);
        cables.showSectionAndConnect(util.grid().at(4, 1, 1), util.grid().at(4, 1, 4), Direction.DOWN);
        cables.showSectionAndConnect(util.grid().at(4, 1, 4), util.grid().at(0, 1, 4), Direction.DOWN);
        scene.showText(60, "Glass and fluix cables only differ in appearance", "玻璃线缆与福鲁伊克斯线缆使用上仅有外观上的差距")
                .attachKeyFrame();
        scene.idle(60);
        cables.showSectionAndConnect(util.grid().at(0, 1, 2), util.grid().at(3, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "channels", util.select().fromTo(0, 1, 2, 3, 1, 3), 60);
        scene.showText(60, "These cables can carry 8 channels", "该线缆能够传递八个频道")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(util.grid().at(3, 1, 0), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.RED, "overload", util.select().fromTo(0, 1, 2, 3, 1, 3), 60);
        scene.overlay().showOutline(PonderPalette.RED, "extra", util.select().position(3, 1, 0), 60);
        scene.showText(60, "When the network has more than 8 channel-consuming devices, it will overload",
                "当网络中拥有超过八个消耗频道的机器时网络便会过载")
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void denseCable(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("dense_cable", "Dense Covered Cable", "致密包层线缆");
        scene.world().showSection(util.select().fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.idle(20);
        cables.showSectionAndConnect(util.grid().at(0, 1, 0), util.grid().at(9, 1, 9), Direction.DOWN);
        scene.idle(20);
        scene.rotateCameraY(180);
        scene.idle(60);
        scene.showText(60, "Dense cables can carry 32 channels", "致密线缆能够传递32个频道")
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "However, it cannot connect to single-block machines (planes, buses, cable-type machines)",
                "但它并不能接入单格类机器（破坏/成型面板，输入/输出/存储总线，线缆内形式的机器）")
                .attachKeyFrame();
        scene.idle(60);
    }
}
