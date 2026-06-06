// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEItems;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

public class StorageBus {

    private StorageBus() {}

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("storage_bus", "Using the Storage Bus", "存储总线的使用");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(1, 1, 1, 1, 1, 3, Direction.DOWN);
        scene.showText(60, "The storage bus can integrate external storage into the AE network", "存储总线可以将外界的存储并入到AE网络中")
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(2, 1, 2, 4, 1, 2, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(4, 1, 3, 4, 2, 3, Direction.DOWN);
        scene.markAsFinished();
        scene.idle(10);
        scene.rotateCameraY(-180);
        scene.idle(40);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(4, 1, 3), Direction.UP), Pointing.UP, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
    }

    public static void interfaceInteraction(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("storage_bus_interface", "Storage Bus & ME Interface Interaction", "存储总线与ME接口的互动");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(1, 1, 1, 1, 1, 3, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(5, 1, 1, 5, 1, 4, Direction.DOWN);
        scene.showText(60, "When you want one network to access another's contents...", "当你想要让一个网络能访问另一个的内容……")
                .attachKeyFrame();
        scene.idle(60);
        scene.rotateCameraY(90);
        scene.idle(30);
        cables.showSectionAndConnect(2, 1, 2, 3, 1, 2, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(4, 1, 2, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "interface_contact", util.select().fromTo(3, 1, 2, 4, 1, 2),
                60);
        scene.showText(60, "Place a storage bus against an ME interface", "将存储总线与ME接口相贴")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay().showOutline(PonderPalette.GREEN, "storage_bus_side", util.select().position(3, 1, 2), 60);
        scene.showText(60, "The network on the storage bus side...", "存储总线的一端的网络……")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showOutline(PonderPalette.GREEN, "interface_side", util.select().position(4, 1, 2), 60);
        scene.showText(60, "Can access the network on the ME interface side", "能够访问ME接口那一段的网络")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "But not vice versa", "反之则不可")
                .attachKeyFrame();
        scene.idle(60);
        scene.markAsFinished();
        scene.idle(30);
        cables.showSectionAndConnect(5, 2, 4, Direction.DOWN);
        scene.idle(20);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(5, 1, 4), Direction.UP), Pointing.DOWN, 60)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
    }
}
