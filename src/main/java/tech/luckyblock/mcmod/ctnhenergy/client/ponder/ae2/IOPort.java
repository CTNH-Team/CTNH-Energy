package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class IOPort {

    private IOPort() {}

    public static void ioPort(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("io_port", IOPortHeader.translate().getContents().toString());
        scene.world().showSection(util.select().fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(3, 1, 4, 5, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "io_port", util.select().position(4, 1, 4), 60);
        scene.overlay().showText(60)
                .text(IOPortText1.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 4), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(0, 1, 0, 4, 1, 3), Direction.DOWN);
        scene.overlay().showText(60)
                .text(IOPortText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.world().hideSection(util.select().fromTo(0, 1, 0, 4, 1, 3), Direction.UP);
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(4, 1, 5, 6, 1, 8), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(IOPortText3.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void output(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("io_port_output", IOPortOutputHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(1, 1, 4, 3, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(60)
                .text(IOPortOutputText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(2, 1, 3, 2, 1, 2), Direction.DOWN);
        scene.overlay().showText(60)
                .text(IOPortOutputText2.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(20);
        scene.rotateCameraY(180);
        scene.idle(40);
    }
}
