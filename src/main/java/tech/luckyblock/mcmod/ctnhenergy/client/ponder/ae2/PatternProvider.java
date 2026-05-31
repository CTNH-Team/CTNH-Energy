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


public class PatternProvider {

    private PatternProvider() {
    }

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("pattern_provider", "Using the Pattern Provider", "样板供应器的使用");
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
        scene.showText(40, "This is a pattern provider", "这是一个样板供应器")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP), Pointing.DOWN, 20)
                .rightClick()
                .withItem(AllItems.WRENCH.asItem().getDefaultInstance());
        scene.idle(20);
        scene.showText(40, "Use a wrench to change the pattern provider's direction", "使用扳手点击样板供应器可使其方向变为扳手点击的方向")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.showText(40, "When the network issues a crafting request...", "当网络下达合成请求时……")
                .attachKeyFrame();
        scene.idle(60);
        scene.showText(40, "The pattern provider outputs ingredients to adjacent containers", "样板供应器会将原料输出到临近的容器内")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, "output", util.select().fromTo(2, 1, 3, 4, 1, 3), 40);
        scene.idle(80);
        scene.markAsFinished();
        scene.showText(40, "Completing a craft requires returning items to the network", "完成一次合成需要将物品返回网络")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.world().showSection(util.select().fromTo(5, 1, 3, 5, 1, 6), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(0, 1, 4, 6, 1, 7), Direction.DOWN);
        scene.world().showSection(util.select().position(2, 2, 4), Direction.DOWN);
        scene.idle(20);
        scene.showText(40, "So you need to build a return mechanism", "因此你需要搭建一个回流装置")
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void parallel(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        AE2CablePonderHelper cables = new AE2CablePonderHelper(scene, util);
        scene.title("crafting_parallel", "Parallel Auto Crafting", "自动合成的并行");
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
        scene.showText(60, "Multiple pattern providers allow parallel crafting", "使用多个样板供应器可以做到多个机器的并行")
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "Memory cards can copy and apply configurations, including pattern providers", "内存卡可以复制机器的配置以及将其中配置应用于机器，当然样板供应器也不例外")
                .attachKeyFrame();
        scene.idle(80);
        scene.showText(60, "Sneak-right-click the pattern provider with a memory card...", "使用内存卡shift右击样板供应器……")
                .attachKeyFrame();
        scene.idle(20);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(1, 1, 3), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.MEMORY_CARD.asItem().getDefaultInstance())
                .whileSneaking();
        scene.idle(60);
        scene.showText(60, "The pattern provider saves its pattern data to the memory card", "样板供应器会将其中样板的信息也存入内存卡中");
        scene.idle(60);
        cables.showSectionAndConnect(2, 1, 3, 2, 1, 5, Direction.DOWN);
        scene.idle(20);
        cables.showSectionAndConnect(2, 3, 4, Direction.DOWN);
        scene.showText(60, "Then right-click the target pattern provider with the memory card...", "然后再使用内存卡右击目标的样板供应器……")
                .attachKeyFrame();
        scene.idle(40);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.MEMORY_CARD.asItem().getDefaultInstance());
        scene.idle(20);
        scene.showText(40, "The memory card consumes blank patterns and copies the saved patterns to the target", "内存卡会消耗物品栏中的空白样板，并依照其记录的样板复制到目标样板供应器中")
                .attachKeyFrame();
        scene.idle(60);
        scene.showText(80, "Repeat this process to build parallelization", "然后以此类推，建造并行");
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
        scene.title("pattern_provider_interaction", "Pattern Provider & ME Interface Interaction", "样板供应器与ME接口的联动");
        scene.showBasePlate();
        scene.idle(20);
        cables.showSectionAndConnect(1, 1, 0, 4, 1, 1, Direction.DOWN);
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "contact", util.select().fromTo(1, 1, 1, 2, 1, 1), 40);
        scene.showText(40, "When a pattern provider touches an ME interface...", "当样板供应器与ME接口接触时……")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(1, 1, 1), Direction.UP), Pointing.DOWN, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(40);
        scene.showText(40, "The pattern provider directly inputs ingredients into the interface's network", "样板供应器会直接将原料输入到ME接口所在的网络");
        scene.idle(60);
    }
}
