// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;


public class AnnihilationPlane {

    private AnnihilationPlane() {
    }

    public static void annihilationPlane(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("annihilation_plane", "Using the Annihilation Plane", "破坏面板的使用");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(2, 1, 1, 3, 1, 2, Direction.DOWN);
        cables.showSectionAndConnect(2, 2, 2, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "The annihilation plane will collect blocks or items in front of it into the network (if possible)", "破坏面板会将其前面的方块或者掉落物收集到网络中（如果能的话）")
                .attachKeyFrame();
        scene.idle(60);
        scene.world().setBlocks(util.select().position(2, 2, 1), AEBlocks.QUARTZ_CLUSTER.block().defaultBlockState(),
                false);
        scene.world().showSection(util.select().position(2, 2, 1), Direction.UP);
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 2, 1, 1, 2, Direction.DOWN);
        scene.idle(20);
        scene.world().destroyBlock(util.grid().at(2, 2, 1));
        scene.idle(20);
        scene.overlay().showControls(util.vector().of(0.5, 2, 2.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
        var item1 = scene.world().createItemEntity(
                util.vector().of(2.5, 5, 2.5),
                util.vector().of(0, 0, 0),
                AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(10);
        scene.world().modifyEntity(item1, e -> e.kill());
        scene.idle(20);
        scene.overlay().showControls(util.vector().of(0.5, 2, 2.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
    }

    public static void filter(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("annihilation_plane_filter", "Filtering with the Annihilation Plane", "使破坏面板破坏/收集特定的物品");
        scene.showBasePlate();
        cables.showSectionAndConnect(0, 1, 0, 3, 1, 2, Direction.DOWN);
        cables.showSectionAndConnect(2, 2, 2, Direction.DOWN);
        scene.idle(40);
        scene.world().setBlocks(util.select().position(2, 2, 1), AEBlocks.SMALL_QUARTZ_BUD.block().defaultBlockState(),
                false);
        scene.world().showSection(util.select().position(2, 2, 1), Direction.UP);
        scene.idle(20);
        scene.world().destroyBlock(util.grid().at(2, 2, 1));
        scene.idle(40);
        scene.overlay().showOutline(PonderPalette.RED, "collect_all", util.select().position(2, 2, 1), 60);
        scene.showText(60, "You'll find that the annihilation plane collects everything in front of it...", "你会发现破坏面板会收集它前方的所有物品……")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 1), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "You can configure your network to only accept certain items", "对此，你可以控制你的网络，使其只能容纳某些物品")
                .attachKeyFrame();
        scene.idle(80);
        cables.hideSectionAndDisconnect(0, 1, 2, 1, 1, 2, Direction.UP);
        scene.idle(20);
        cables.showSectionAndConnect(4, 1, 2, 5, 1, 2, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "Configure the storage bus filter...", "配置存储总线的过滤……")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "storage_bus", util.select().fromTo(4, 1, 2, 5, 1, 2), 60);
        scene.idle(80);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(4, 1, 2), Direction.DOWN), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(40);
        var item1 = scene.world().createItemEntity(
                util.vector().of(2.5, 5, 2.5),
                util.vector().of(0, 0, 0),
                AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(15);
        scene.world().modifyEntity(item1, e -> e.kill());
        scene.idle(40);
        scene.showText(60, "Note that blocks without drops will always be broken by the annihilation plane", "需要注意的是，没有掉落物的方块无论怎样都会被破坏面板破坏")
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "Enchanting the annihilation plane with Silk Touch may help solve this issue", "为破坏面板附魔精准采集可能可以为解决该问题提供思路")
                .attachKeyFrame();
        scene.idle(60);
    }
}
