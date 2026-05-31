// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;


public class CraftingProcessUnit {

    private CraftingProcessUnit() {
    }

    public static void unit(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("crafting_process_unit", "Building a CPU", "CPU的搭建");
        scene.world().showSection(util.select().fromTo(0, 0, 0, 8, 0, 8), Direction.DOWN);
        scene.idle(20);
        scene.showText(40, "Building a CPU has certain rules...", "CPU的搭建有一定的规则……")
                .attachKeyFrame();
        scene.idle(60);
        scene.world().showSection(util.select().position(1, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 1, 4, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(6, 1, 1, 7, 2, 1), Direction.DOWN);
        scene.idle(10);
        scene.showText(40, "The CPU must be a rectangular cuboid", "CPU的形体必须得是长方体")
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
        scene.showText(40, "A non-cuboid CPU will not form properly", "不为长方体的CPU则不会显示以成型的状态")
                .attachKeyFrame();
        scene.idle(60);
        scene.world().showSection(util.select().fromTo(1, 1, 6, 2, 2, 7), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.RED, "need_storage", util.select().fromTo(1, 1, 6, 2, 2, 7), 40);
        scene.showText(60, "The CPU needs at least one crafting storage", "CPU结构内至少需要有一个合成存储器")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 2, 7), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(4, 1, 6, 5, 2, 7), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "alternatives", util.select().fromTo(4, 1, 6, 5, 2, 7), 40);
        scene.showText(60, "Other parts can be crafting units, monitors, storages, or co-processors", "其余部分则可使用合成单元，合成监控室，合成存储器，并行处理单元代替")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 2, 7), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
    }
}
