// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

public class CraftingSystem {

    private CraftingSystem() {}

    public static void system(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("crafting_system", "AE Auto Crafting System", "AE的自动合成系统");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(2, 1, 1, 2, 1, 3, Direction.DOWN);
        scene.idle(20);
        scene.showText(40, "To build an auto-crafting system...", "要搭建一个自动合成系统……")
                .attachKeyFrame();
        scene.idle(60);
        cables.showSectionAndConnect(0, 1, 1, 1, 1, 3, Direction.DOWN);
        scene.idle(20);
        scene.showText(40, "You need a CPU multiblock", "你需要一个CPU多方块")
                .pointAt(util.vector().blockSurface(util.grid().at(0, 1, 1), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "cpu", util.select().fromTo(0, 1, 1, 0, 1, 3), 40);
        scene.idle(60);
        cables.showSectionAndConnect(3, 1, 0, 4, 1, 2, Direction.DOWN);
        scene.idle(20);
        scene.showText(40, "And a pattern provider with output target", "还有样板供应器与输出对象")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 0), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "provider", util.select().fromTo(4, 1, 0, 4, 1, 1), 40);
        scene.idle(60);
    }
}
