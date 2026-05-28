package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import appeng.core.definitions.AEItems;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.CTNHAE2PondersLang.*;

public class StorageBus {

    private StorageBus() {}

    public static void common(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("storage_bus", StorageBusHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(1, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.overlay().showText(60)
                .text(StorageBusText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(80);
        scene.world().showSection(util.select().fromTo(2, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(4, 1, 3, 4, 2, 3), Direction.DOWN);
        scene.markAsFinished();
        scene.idle(10);
        scene.rotateCameraY(-180);
        scene.idle(40);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(4, 1, 3), Direction.UP), Pointing.UP, 40)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
    }

    public static void interfaceInteraction(SceneBuilder builder, SceneBuildingUtil util) {
        CTNHEnergyPonderSceneBuilder scene = new CTNHEnergyPonderSceneBuilder(builder);
        scene.title("storage_bus_interface", StorageBusInterfaceHeader.translate().getContents().toString());
        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(1, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(5, 1, 1, 5, 1, 4), Direction.DOWN);
        scene.overlay().showText(60)
                .text(StorageBusInterfaceText1.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.rotateCameraY(90);
        scene.idle(30);
        scene.world().showSection(util.select().fromTo(2, 1, 2, 3, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(4, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "interface_contact", util.select().fromTo(3, 1, 2, 4, 1, 2), 60);
        scene.overlay().showText(60)
                .text(StorageBusInterfaceText2.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.idle(60);
        scene.overlay().showOutline(PonderPalette.GREEN, "storage_bus_side", util.select().position(3, 1, 2), 60);
        scene.overlay().showText(60)
                .text(StorageBusInterfaceText3.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(3, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showOutline(PonderPalette.GREEN, "interface_side", util.select().position(4, 1, 2), 60);
        scene.overlay().showText(60)
                .text(StorageBusInterfaceText4.translate().getContents().toString())
                .pointAt(util.vector().blockSurface(util.grid().at(4, 1, 2), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(60)
                .text(StorageBusInterfaceText5.translate().getContents().toString())
                .attachKeyFrame();
        scene.idle(60);
        scene.markAsFinished();
        scene.idle(30);
        scene.world().showSection(util.select().position(5, 2, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(5, 1, 4), Direction.UP), Pointing.DOWN, 60)
                .rightClick()
                .withItem(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().getDefaultInstance());
        scene.idle(60);
    }
}
