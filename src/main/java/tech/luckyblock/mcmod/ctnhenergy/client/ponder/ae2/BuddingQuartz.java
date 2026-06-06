// 代码来源于Create Delights's PonderJs，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

public class BuddingQuartz {

    private BuddingQuartz() {}

    public static void obtain(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("budding_quartz_obtain", "Budding Quartz Generation", "赛特斯石英母岩的生成");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(20);
        scene.world().setBlocks(util.select().position(2, 1, 2),
                AEBlocks.FLAWLESS_BUDDING_QUARTZ.block().defaultBlockState(), false);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.showText(60, "This is a budding certus quartz block", "这是一个赛特斯石英母岩")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.UP));
        scene.idle(60);

        var blocks = new BlockState[] {
                AEBlocks.DAMAGED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.FLAWED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.QUARTZ_BLOCK.block().defaultBlockState(),
                AEBlocks.QUARTZ_BLOCK.block().defaultBlockState(),
                AEBlocks.CHIPPED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.FLAWED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.DAMAGED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.CHIPPED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.MYSTERIOUS_CUBE.block().defaultBlockState()
        };
        var positions = new BlockPos[] {
                util.grid().at(1, 1, 1),
                util.grid().at(1, 1, 2),
                util.grid().at(1, 1, 3),
                util.grid().at(2, 1, 1),
                util.grid().at(2, 1, 3),
                util.grid().at(3, 1, 1),
                util.grid().at(3, 1, 2),
                util.grid().at(3, 1, 3),
                util.grid().at(2, 2, 2)
        };

        for (int i = 0; i < blocks.length; i++) {
            scene.world().setBlocks(util.select().position(positions[i]), blocks[i], false);
            scene.world().showSection(util.select().position(positions[i]), Direction.DOWN);
            scene.idle(5);
        }
        scene.showText(60, "Budding quartz appears with mysterious cubes in meteorites", "母岩会和神秘方块一起出现在陨石中")
                .attachKeyFrame();
        scene.idle(60);

        for (var pos : positions) {
            scene.world().hideSection(util.select().position(pos), Direction.UP);
            scene.idle(5);
        }
    }

    public static void grow(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("budding_quartz_grow", "Budding Quartz Growth", "赛特斯石英母岩的生长");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.world().setBlocks(util.select().position(2, 1, 2),
                AEBlocks.FLAWED_BUDDING_QUARTZ.block().defaultBlockState(), false);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.showText(100, "Budding quartz grows crystals over time", "赛特斯石英母岩会随着时间生长");
        scene.idle(20);

        var quartzStages = new BlockState[] {
                AEBlocks.SMALL_QUARTZ_BUD.block().defaultBlockState(),
                AEBlocks.MEDIUM_QUARTZ_BUD.block().defaultBlockState(),
                AEBlocks.LARGE_QUARTZ_BUD.block().defaultBlockState(),
                AEBlocks.QUARTZ_CLUSTER.block().defaultBlockState()
        };
        scene.world().showSection(util.select().position(2, 2, 2), Direction.UP);
        for (var stage : quartzStages) {
            scene.world().setBlocks(util.select().position(2, 2, 2), stage, false);
            scene.idle(20);
        }
        scene.showText(100, "During growth, the budding quartz may degrade one tier (except flawless)",
                "在生长过程中，赛特斯石英母岩有概率降低一个等级（除了无瑕母岩）")
                .attachKeyFrame();
        scene.world().showSection(util.select().position(2, 1, 1), Direction.NORTH);

        var degradedStates = new BlockState[] {
                AEBlocks.FLAWLESS_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.FLAWED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.CHIPPED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.DAMAGED_BUDDING_QUARTZ.block().defaultBlockState(),
                AEBlocks.QUARTZ_BLOCK.block().defaultBlockState()
        };
        for (int i = 1; i < degradedStates.length; i++) {
            scene.world().setBlocks(util.select().position(2, 1, 1), quartzStages[i - 1], false);
            scene.world().setBlocks(util.select().position(2, 1, 2), degradedStates[i], false);
            scene.idle(20);
        }
        scene.idle(20);
        scene.markAsFinished();
        scene.world().setBlocks(util.select().position(2, 1, 2),
                AEBlocks.FLAWLESS_BUDDING_QUARTZ.block().defaultBlockState(), false);

        BlockPos[] acceleratorPositions = {
                util.grid().at(3, 1, 2),
                util.grid().at(1, 1, 2),
                util.grid().at(2, 1, 3)
        };
        for (var pos : acceleratorPositions) {
            scene.world().setBlocks(util.select().position(pos),
                    AEBlocks.GROWTH_ACCELERATOR.block().defaultBlockState(), false);
            scene.world().showSection(util.select().position(pos), Direction.DOWN);
        }
        scene.showText(60, "Place growth accelerators to speed up growth (they stack)", "放置催生器可加速母岩生长（催生器可叠加）");
        scene.idle(20);
    }

    public static void repair(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("budding_quartz_repair", "Repairing Budding Quartz", "赛特斯石英母岩的修复");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.world().setBlocks(util.select().position(2, 1, 2), AEBlocks.QUARTZ_BLOCK.block().defaultBlockState(),
                false);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.showText(100, "Your budding quartz degraded to a quartz block?", "母岩退化到石英块了？");
        scene.idle(20);
        scene.markAsFinished();
        scene.idle(20);
        scene.overlay()
                .showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.UP), Pointing.DOWN, 20)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
        scene.showText(60, "Right-click with a budding quartz of the same tier for a 50% chance to upgrade it!",
                "手持与放置的相同等级的母岩右键右击它就有50%的概率生成一个更高级的母岩！")
                .attachKeyFrame();
        scene.idle(60);
        scene.showText(60, "You can obtain flawless budding quartz this way!", "使用该方法修复可以获取无瑕母岩！")
                .attachKeyFrame();
        scene.idle(60);
    }
}
