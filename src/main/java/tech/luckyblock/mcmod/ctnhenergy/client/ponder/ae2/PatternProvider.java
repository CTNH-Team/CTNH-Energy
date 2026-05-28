// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEItems;
import com.simibubi.create.AllItems;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class PatternProvider {

    private PatternProvider() {}

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("pattern_provider", PatternProviderHeader.translate().getContents().toString());
        scene.world().showSection(util.select().fromTo(0, 0, 0, 7, 0, 7), Direction.UP);
        scene.showBasePlate();
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(4, 1, 0, 4, 1, 2), Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(1, 1, 3, 4, 1, 3), Direction.UP);
        scene.idle(20);
        scene.world().showSection(util.select().position(2, 2, 3), Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(4, 2, 1, 4, 3, 3), Direction.UP);
        scene.idle(20);
        scene.overlay().showText(40)
                .text(PatternProviderText1.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP), Pointing.DOWN, 20)
                .rightClick()
                .withItem(AllItems.WRENCH.asItem().getDefaultInstance());
        scene.idle(20);
        scene.overlay().showText(40)
                .text(PatternProviderText2.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay().showText(40)
                .text(PatternProviderText3.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay().showText(40)
                .text(PatternProviderText4.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "output", util.select().fromTo(2, 1, 3, 4, 1, 3), 40);
        scene.idle(80);
        scene.markAsFinished();
        scene.overlay().showText(40)
                .text(PatternProviderText5.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.world().showSection(util.select().fromTo(5, 1, 3, 5, 1, 6), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(0, 1, 4, 6, 1, 7), Direction.DOWN);
        scene.world().showSection(util.select().position(2, 2, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40)
                .text(PatternProviderText6.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void parallel(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("crafting_parallel", CraftingParallelHeader.translate().getContents().toString());
        scene.world().showSection(util.select().fromTo(1, 0, 0, 8, 0, 8), Direction.UP);
        scene.idle(20);
        cables.showSectionAndConnect(1, 1, 0, 1, 1, 3, Direction.DOWN);
        scene.idle(10);
        for (int index = 0; index < 4; index++) {
            cables.showSectionAndConnect(0, index, 4, Direction.DOWN);
            scene.idle(5);
        }
        cables.showSectionAndConnect(1, 1, 4, 1, 1, 5, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(1, 3, 4, Direction.DOWN);
        scene.idle(40);
        scene.overlay().showText(60)
                .text(CraftingParallelText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(60)
                .text(CraftingParallelText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(60)
                .text(CraftingParallelText3.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(20);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(1, 1, 3), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.MEMORY_CARD.asItem().getDefaultInstance())
                .whileSneaking();
        scene.idle(60);
        scene.overlay().showText(60)
                .text(CraftingParallelText4.translate().getContents().toString());
        scene.idle(60);
        cables.showSectionAndConnect(2, 1, 3, 2, 1, 5, Direction.DOWN);
        scene.idle(20);
        cables.showSectionAndConnect(2, 3, 4, Direction.DOWN);
        scene.overlay().showText(60)
                .text(CraftingParallelText5.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(40);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.MEMORY_CARD.asItem().getDefaultInstance());
        scene.idle(20);
        scene.overlay().showText(40)
                .text(CraftingParallelText6.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay().showText(80)
                .text(CraftingParallelText7.translate().getContents().toString());
        scene.idle(20);
        for (int index = 1; index <= 5; index++) {
            cables.showSectionAndConnect(2 + index, 1, 3, 2 + index, 1, 5, Direction.DOWN);
            cables.showSectionAndConnect(2 + index, 3, 4, Direction.DOWN);
            scene.idle(5);
        }
        scene.idle(10);
        cables.showSectionAndConnect(2, 1, 0, 7, 1, 1, Direction.DOWN);
        scene.idle(10);
        cables.showSectionAndConnect(1, 1, 6, 8, 1, 6, Direction.DOWN);
        cables.showSectionAndConnect(8, 1, 0, 8, 1, 6, Direction.DOWN);
        scene.idle(10);
        scene.rotateCameraY(90);
        scene.idle(40);
        scene.rotateCameraY(90);
        scene.idle(40);
        cables.showSectionAndConnect(1, 2, 5, 7, 2, 6, Direction.DOWN);
    }

    public static void interaction(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("pattern_provider_interaction",
                PatternProviderInteractionHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(1, 1, 0, 4, 1, 1, Direction.DOWN);
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "contact", util.select().fromTo(1, 1, 1, 2, 1, 1), 40);
        scene.overlay().showText(40)
                .text(PatternProviderInteractionText1.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(1, 1, 1), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(40);
        scene.overlay().showText(40)
                .text(PatternProviderInteractionText2.translate().getContents().toString());
        scene.idle(60);
    }
}
