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


public class FormationPlane {

    private FormationPlane() {
    }

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("formation_plane", "Using the Formation Plane", "成型面板的使用");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 0, 6, 1, 6, Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "The formation plane outputs network items as blocks or items into the world", "成型面板可以输入进网络的物料以掉落物或者方块形式输出到世界上")
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(3, 2, 1, Direction.DOWN);
        scene.showText(60, "When the formation plane is in block mode...", "当成型面板为方块模式时……")
                .attachKeyFrame();
        scene.idle(80);
        var item1 = scene.world().createItemEntity(
                util.vector().of(3.5, 5, 1.5),
                util.vector().of(0, 0, 0),
                AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(15);
        scene.world().modifyEntity(item1, e -> e.kill());
        scene.world().setBlocks(util.select().position(2, 1, 1), AEBlocks.QUARTZ_BLOCK.block().defaultBlockState(),
                false);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "placed", util.select().position(2, 1, 1), 60);
        scene.showText(60, "The formation plane places blocks in front of it", "成型面板会将方块放置在它的前面")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.world().hideSection(util.select().position(2, 1, 1), Direction.UP);
        scene.showText(60, "When the formation plane is in item mode...", "当成型面板为掉落物模式时……")
                .attachKeyFrame();
        scene.idle(80);
        scene.world().createItemEntity(
                util.vector().of(2.5, 1.5, 1.75),
                util.vector().of(0, 0, 0),
                AEBlocks.QUARTZ_BLOCK.asItem().getDefaultInstance());
        scene.overlay().showOutline(PonderPalette.GREEN, "drop", util.select().position(2, 1, 1), 60);
        scene.showText(60, "The formation plane drops items as entities in front of it", "成型面板会将物品以掉落物形式丢弃在它的前面")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "Configure the formation plane filter to only output specific items", "配置成型面板的过滤选项可使其只输出过滤内的物品")
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.UP), Pointing.RIGHT, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
    }
}
