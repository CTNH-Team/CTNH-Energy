package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.utils.Position;
import net.minecraft.world.entity.player.Player;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.gui.ConfigWidget;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.handler.MEStorageFluidHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.handler.MEStorageItemHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.utils.GenericStackHandler;

import java.util.function.Predicate;

public class MEStokingMachine extends MEPartMachine {

    @Persisted
    protected final GenericStackHandler stackHandler;
    protected TickableSubscription autoIOSubs;
    protected boolean shouldSubscribe = true;
    protected final Predicate<AEKey> keyPredicate;

    public MEStokingMachine(IMachineBlockEntity holder, int tier, IO io, int configSize, Predicate<AEKey> predicate) {
        super(holder, tier, io);
        stackHandler = new GenericStackHandler(configSize);
        new MEStorageItemHandler(this, IO.IN, nodeSupplier, stackHandler);
        new MEStorageFluidHandler(this, IO.IN, nodeSupplier, stackHandler);
        keyPredicate = predicate;
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(new Position(0, 0));
        // ME Network status
        group.addWidget(new LabelWidget(3, 0, () -> isNodeActive() ?
                "gtceu.gui.me_network.online" :
                "gtceu.gui.me_network.offline"));

        group.addWidget(new ConfigWidget(3, 10, stackHandler, keyPredicate));
        return group;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        shouldSubscribe = true;
        updateIOSubscription();
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        super.setWorkingEnabled(workingEnabled);
        shouldSubscribe = true;
        updateIOSubscription();
    }

    protected void updateIOSubscription() {
        if (shouldSubscribe) {
            autoIOSubs = subscribeServerTick(autoIOSubs, this::autoIO);
        } else if (autoIOSubs != null) {
            autoIOSubs.unsubscribe();
            autoIOSubs = null;
        }
    }

    public void autoIO() {
        if(isMESyncTick()) {
            IGridNode node = getActionableNode();
            var keyCounter = new KeyCounter();
            if(isWorkingEnabled() && node != null) {
                keyCounter = node.getGrid().getStorageService().getCachedInventory();
            }
            stackHandler.updateStacks(keyCounter);
            shouldSubscribe = isWorkingEnabled() && isNodeActive();
            updateIOSubscription();
        }
    }
}
