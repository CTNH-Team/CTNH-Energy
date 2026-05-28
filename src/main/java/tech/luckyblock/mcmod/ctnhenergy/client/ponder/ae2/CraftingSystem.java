package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class CraftingSystem {

    private CraftingSystem() {}

    public static void system(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("crafting_system", CraftingSystemHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(2, 1, 1, 2, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40)
                .text(CraftingSystemText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.world().showSection(util.select().fromTo(0, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40)
                .text(CraftingSystemText2.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(0, 1, 1), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "cpu", util.select().fromTo(0, 1, 1, 0, 1, 3), 40);
        scene.idle(60);
        scene.world().showSection(util.select().fromTo(3, 1, 0, 4, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40)
                .text(CraftingSystemText3.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 0), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "provider", util.select().fromTo(4, 1, 0, 4, 1, 1), 40);
        scene.idle(60);
    }
}
