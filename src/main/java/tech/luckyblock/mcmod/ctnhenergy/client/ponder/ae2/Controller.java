// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEBlocks;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

public class Controller {

    private Controller() {}

    public static void controller(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("controller", "ME Controller Placement", "ME控制器的摆放方式……");
        scene.world().showSection(util.select().fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.world().setBlocks(util.select().position(8, 1, 1), AEBlocks.CONTROLLER.block().defaultBlockState(),
                false);
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 1, 1, 1, 1, Direction.DOWN);
        scene.idle(20);
        scene.showText(40, "This is a controller...", "这是一个控制器……")
                .attachKeyFrame();
        scene.idle(60);
        scene.showText(60, "The controller needs power to work, other mods' power also works",
                "控制器需要接入能源才能工作，使用其他mod的能源也可")
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
        scene.showText(60, "The controller can extend up to 7 blocks in each dimension", "控制器每个维度都可延伸最多七格")
                .attachKeyFrame();
        scene.idle(60);
        scene.idle(30);
        cables.showSectionAndConnect(8, 1, 1, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "Exceeding 7 blocks will overload the network", "超过七格整个网络就会过载")
                .attachKeyFrame();
        scene.idle(60);
        scene.rotateCameraY(180);
        scene.idle(40);
        cables.showSectionAndConnect(3, 1, 3, 5, 1, 4, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "Controllers cannot pass channels between each other (cannot connect)", "控制器之间不能传递频道（即不能连接）")
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.RED, "no_connect", util.select().fromTo(3, 1, 3, 5, 1, 4), 60);
        scene.idle(80);
        cables.showSectionAndConnect(3, 1, 5, 8, 3, 7, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "Controllers also cannot be placed in a cross shape", "控制器也不能摆成十字的状态")
                .attachKeyFrame();
        scene.idle(80);
    }
}
