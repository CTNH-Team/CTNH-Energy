// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

public class MolecularAssembler {

    private MolecularAssembler() {}

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("molecular_assembler", "Using the Molecular Assembler", "分子装配室的使用");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(0, 1, 0, 3, 1, 0, Direction.DOWN);
        cables.showSectionAndConnect(2, 1, 1, 2, 2, 2, Direction.DOWN);
        cables.showSectionAndConnect(1, 1, 1, Direction.DOWN);
        scene.showText(60, "To use the molecular assembler...", "要使用分子装配室……");
        scene.idle(80);
        cables.showSectionAndConnect(1, 1, 2, Direction.DOWN);
        scene.showText(60, "You need to place it next to a pattern provider", "你需要将其与样板供应器放在一起")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "It can automate crafting table, stonecutter, and smithing table recipes",
                "分子装配室可以自动化工作台，切石机，锻造台的配方，且会把产物自动送回样板供应器")
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "Both pattern providers and molecular assemblers can pass through the network...",
                "样板供应器和分子装配室都可以传递网络……")
                .attachKeyFrame();
        scene.idle(80);
        cables.showSectionAndConnect(0, 1, 1, 1, 2, 2, Direction.DOWN);
        scene.showText(60, "So you can build structures like this", "所以你可以搭建出类似这样的结构")
                .attachKeyFrame();
        scene.idle(80);
    }
}
