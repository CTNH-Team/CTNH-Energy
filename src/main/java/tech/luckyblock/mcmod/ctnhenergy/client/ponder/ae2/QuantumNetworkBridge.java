// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEItems;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;


public class QuantumNetworkBridge {

    private QuantumNetworkBridge() {
    }

    public static void bridge(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("quantum_network_bridge", "Extending Networks with Quantum Bridges", "使用量子网桥将网络传递到远端");
        scene.world().showSection(util.select().fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.idle(20);
        scene.showText(60, "Quantum network bridges can extend your network", "量子网桥可用于网络的传输");
        scene.idle(80);
        scene.showText(60, "A quantum ring requires 8 quantum rings and 1 quantum link chamber", "为了搭建一个量子环结构，你需要八个量子环和一个量子链接仓")
                .attachKeyFrame();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != 1 || j != 1) {
                    cables.showSectionAndConnect(i, j + 1, 0, Direction.DOWN);
                    scene.idle(5);
                }
            }
        }
        scene.idle(10);
        cables.showSectionAndConnect(1, 2, 0, Direction.SOUTH);
        scene.idle(20);
        scene.idle(30);
        scene.rotateCameraY(180);
        scene.idle(30);
        cables.showSectionAndConnect(0, 1, 1, 3, 1, 3, Direction.DOWN);
        scene.overlay().showOutline(PonderPalette.GREEN, "connect1", util.select().position(1, 1, 0), 60);
        scene.overlay().showOutline(PonderPalette.GREEN, "connect2", util.select().position(0, 2, 0), 60);
        scene.overlay().showOutline(PonderPalette.GREEN, "connect3", util.select().position(1, 3, 0), 60);
        scene.overlay().showOutline(PonderPalette.GREEN, "connect4", util.select().position(2, 2, 0), 60);
        scene.showText(60, "Connect the ring to your network via the 4 ring blocks around the link chamber", "连接量子环和网络只能通过围绕量子连接仓的四个量子环方块")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 2, 0), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != 1 || j != 1) {
                    cables.showSectionAndConnect(i + 6, j + 1, 8, Direction.DOWN);
                    scene.idle(5);
                }
            }
        }
        cables.showSectionAndConnect(7, 2, 8, Direction.DOWN);
        scene.showText(60, "Rings are used in pairs - place another ring structure at the remote location", "量子环是成双成对使用的，你需要在你需要的远端也放置一个量子环结构")
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "Insert quantum entangled singularities (from the same pair) into both rings", "使用量子环，你需要为两个量子环分别放入同时产生的量子缠绕态奇点");
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(7, 2, 8), Direction.WEST), Pointing.LEFT, 40)
                .rightClick()
                .withItem(AEItems.QUANTUM_ENTANGLED_SINGULARITY.asItem().getDefaultInstance());
        scene.idle(40);
        scene.rotateCameraY(180);
        scene.idle(30);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(1, 2, 0), Direction.WEST), Pointing.LEFT, 40)
                .rightClick()
                .withItem(AEItems.QUANTUM_ENTANGLED_SINGULARITY.asItem().getDefaultInstance());
        scene.idle(60);
        cables.showSectionAndConnect(4, 1, 5, 7, 1, 7, Direction.DOWN);
        scene.idle(20);
        cables.showSectionAndConnect(3, 2, 2, Direction.DOWN);
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(30);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(5, 1, 6), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
        scene.showText(60, "A pair of quantum rings consumes a fixed 400 AE/t regardless of distance", "一对量子环固定消耗400AE/t，且不随距离变化")
                .attachKeyFrame();
        scene.idle(60);
    }
}
