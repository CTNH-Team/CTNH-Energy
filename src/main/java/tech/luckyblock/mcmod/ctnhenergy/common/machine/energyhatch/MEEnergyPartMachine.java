package tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.widget.LongInputWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.networking.IGridNodeListener;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.handler.MEStorageEUHandler;


import java.util.Arrays;

import static com.gregtechceu.gtceu.api.GTValues.*;

public class MEEnergyPartMachine extends MEPartMachine {

    @Persisted
    public final MEStorageEUHandler energyContainer;

    @Nullable
    protected TickableSubscription energySubs;

    private boolean checkBefore = true;

    public MEEnergyPartMachine(IMachineBlockEntity holder, IO io) {
        super(holder, UV, io, false, false);
        this.energyContainer = new MEStorageEUHandler(this, io, nodeSupplier);
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        updateEnergySubscription();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateEnergySubscription();
    }

    protected void updateEnergySubscription() {
        energySubs = subscribeServerTick(energySubs, this::updateEnergy);
    }

    protected void updateEnergy() {
        if (isMESyncTick()) {
            energyContainer.updateEnergyCapacity();
            boolean check = energyContainer.checkGridTier();
            if(checkBefore != check) {
                checkBefore = check;
                getControllers().forEach(IMultiController::onStructureFormed);
            }
        }
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup configGroup = new WidgetGroup(0, 0, 100, 80);
        configGroup.addWidgets(
                new LabelWidget(0, 2,
                        () -> isNodeActive() ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"),
                new LabelWidget(35, 16, "gtceu.creative.energy.voltage"),
                new SelectorWidget(25, 28, 50, 20, Arrays.stream(GTValues.VNF).limit(15).toList(), -1)
                        .setOnChanged(tier -> {
                            energyContainer.setTier(ArrayUtils.indexOf(GTValues.VNF, tier));
                        })
                        .setSupplier(() -> GTValues.VNF[energyContainer.getTier()])
                        .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                        .setBackground(ColorPattern.BLACK.rectTexture())
                        .setValue(GTValues.VNF[energyContainer.getTier()])
                        .setIsUp(true),
                new LabelWidget(35, 52, "gtceu.creative.energy.amperage"),
                new LongInputWidget(0, 64, 100, 20, energyContainer::getInputAmperage,
                        energyContainer::setInputAmperage)
                        .setMax(64L));
        return configGroup;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return io == IO.IN;
    }
}
