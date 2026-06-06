// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEItems;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

public class Interface {

    private Interface() {}

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("interface_common", "Using the ME Interface", "ME接口的使用");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(1, 1, 1, 1, 1, 3, Direction.DOWN);
        cables.showSectionAndConnect(1, 1, 2, 3, 1, 2, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "The ME interface is the network's connection to the outside world", "ME接口是网络与外界的接口")
                .attachKeyFrame();
        scene.idle(60);
        cables.showSectionAndConnect(3, 2, 2, Direction.DOWN);
        scene.markAsFinished();
        scene.idle(20);
        scene.showText(60, "Items can be inserted directly into the network via the ME interface", "可以将物料直接从ME接口输入进网络")
                .attachKeyFrame();
        scene.idle(70);
        cables.hideSectionAndDisconnect(3, 2, 2, Direction.UP);
        scene.idle(20);
        scene.showText(40, "Configure the ME interface slots", "配置ME接口的槽位")
                .attachKeyFrame();
        scene.idle(40);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(3, 1, 2), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(40);
        cables.showSectionAndConnect(3, 1, 1, Direction.SOUTH);
        scene.idle(20);
        scene.showText(60, "It will request items from the network to fill its configured slots", "它会将自身填充物料至你所设定的值")
                .attachKeyFrame();
        scene.world().flapFunnel(util.grid().at(3, 1, 1), true);
        scene.idle(60);
    }
}
