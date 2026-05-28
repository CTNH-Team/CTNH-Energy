// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class CraftingProcessUnit {

    private CraftingProcessUnit() {}

    public static void unit(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("crafting_process_unit", CraftingProcessUnitHeader.translate().getContents().toString());
        scene.world().showSection(util.select().fromTo(0, 0, 0, 8, 0, 8), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40)
                .text(CraftingProcessUnitText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.world().showSection(util.select().position(1, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 1, 4, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(6, 1, 1, 7, 2, 1), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(40)
                .text(CraftingProcessUnitText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.rotateCameraY(90);
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(1, 1, 3, 2, 2, 4), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(4, 1, 3, 5, 1, 4), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(7, 1, 3, 8, 2, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40)
                .text(CraftingProcessUnitText3.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.world().showSection(util.select().fromTo(1, 1, 6, 2, 2, 7), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.RED, "need_storage", util.select().fromTo(1, 1, 6, 2, 2, 7), 40);
        scene.overlay().showText(60)
                .text(CraftingProcessUnitText4.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(1, 2, 7), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(4, 1, 6, 5, 2, 7), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "alternatives", util.select().fromTo(4, 1, 6, 5, 2, 7), 40);
        scene.overlay().showText(60)
                .text(CraftingProcessUnitText5.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(4, 2, 7), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
    }
}
