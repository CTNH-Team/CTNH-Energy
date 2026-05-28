// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class FormationPlane {

    private FormationPlane() {
    }

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("formation_plane", FormationPlaneHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 0, 6, 1, 6, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(FormationPlaneText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(3, 2, 1, Direction.DOWN);
        scene.overlay().showText(60)
                .text(FormationPlaneText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        var item1 = scene.world().createItemEntity(
                util.vector().of(3.5, 5, 1.5),
                util.vector().of(0, 0, 0),
                AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance()
        );
        scene.idle(15);
        scene.world().modifyEntity(item1, e -> e.kill());
        scene.world().setBlocks(util.select().position(2, 1, 1), AEBlocks.QUARTZ_BLOCK.block().defaultBlockState(), false);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "placed", util.select().position(2, 1, 1), 60);
        scene.overlay().showText(60)
                .text(FormationPlaneText3.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.world().hideSection(util.select().position(2, 1, 1), Direction.UP);
        scene.overlay().showText(60)
                .text(FormationPlaneText4.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.world().createItemEntity(
                util.vector().of(2.5, 1.5, 1.75),
                util.vector().of(0, 0, 0),
                AEBlocks.QUARTZ_BLOCK.asItem().getDefaultInstance()
        );
        scene.overlay().showOutline(PonderPalette.GREEN, "drop", util.select().position(2, 1, 1), 60);
        scene.overlay().showText(60)
                .text(FormationPlaneText5.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(60)
                .text(FormationPlaneText6.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.UP), Pointing.RIGHT, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
    }
}
