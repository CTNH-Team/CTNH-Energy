package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class BuddingQuartz {

    private BuddingQuartz() {}

    public static void obtain(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("budding_quartz_obtain", BuddingQuartzObtainHeader.translate().getContents().toString());
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(20);
        scene.world().setBlocks(util.select().position(2, 1, 2), AEBlocks.FLAWLESS_BUDDING_QUARTZ.block().defaultBlockState(), false);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(BuddingQuartzObtainText1.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.UP));
        scene.idle(60);

        var blocks = new BlockState[]{
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
        var positions = new BlockPos[]{
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
        scene.overlay().showText(60)
                .text(BuddingQuartzObtainText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);

        for (var pos : positions) {
            scene.world().hideSection(util.select().position(pos), Direction.UP);
            scene.idle(5);
        }
    }

    public static void grow(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("budding_quartz_grow", BuddingQuartzGrowHeader.translate().getContents().toString());
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.world().setBlocks(util.select().position(2, 1, 2), AEBlocks.FLAWED_BUDDING_QUARTZ.block().defaultBlockState(), false);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(100)
                .text(BuddingQuartzGrowText1.translate().getContents().toString());
        scene.idle(20);

        var quartzStages = new BlockState[]{
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
        scene.overlay().showText(100)
                .text(BuddingQuartzGrowText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.world().showSection(util.select().position(2, 1, 1), Direction.NORTH);

        var degradedStates = new BlockState[]{
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
        scene.world().setBlocks(util.select().position(2, 1, 2), AEBlocks.FLAWLESS_BUDDING_QUARTZ.block().defaultBlockState(), false);

        BlockPos[] acceleratorPositions = {
                util.grid().at(3, 1, 2),
                util.grid().at(1, 1, 2),
                util.grid().at(2, 1, 3)
        };
        for (var pos : acceleratorPositions) {
            scene.world().setBlocks(util.select().position(pos), AEBlocks.GROWTH_ACCELERATOR.block().defaultBlockState(), false);
            scene.world().showSection(util.select().position(pos), Direction.DOWN);
        }
        scene.overlay().showText(60)
                .text(BuddingQuartzGrowText3.translate().getContents().toString());
        scene.idle(20);
    }

    public static void repair(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("budding_quartz_repair", BuddingQuartzRepairHeader.translate().getContents().toString());
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.world().setBlocks(util.select().position(2, 1, 2), AEBlocks.QUARTZ_BLOCK.block().defaultBlockState(), false);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(100)
                .text(BuddingQuartzRepairText1.translate().getContents().toString());
        scene.idle(20);
        scene.markAsFinished();
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.UP), Pointing.DOWN, 20)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
        scene.overlay().showText(60)
                .text(BuddingQuartzRepairText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay().showText(60)
                .text(BuddingQuartzRepairText3.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
    }
}
