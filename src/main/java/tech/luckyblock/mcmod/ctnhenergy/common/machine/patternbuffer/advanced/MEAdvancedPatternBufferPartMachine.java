package tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.advanced;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.*;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.MethodsReturnNonnullByDefault;

import appeng.api.stacks.*;
import it.unimi.dsi.fastutil.objects.*;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.MEDualOutputConfigurator;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.MEDualOutputHatchPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferPartMachine;

import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEAdvancedPatternBufferPartMachine extends MEPatternBufferPartMachine {

    public static final int MAX_PATTERN_COUNT = 72;

    @Nullable
    protected TickableSubscription autoIOSubs;

    @Persisted
    private final KeyStorage internalBuffer;

    @Persisted
    @Getter
    private final NotifiableItemStackHandler outputInventory;

    @Persisted
    @Getter
    private final NotifiableFluidTank outputTank;

    @Nullable
    protected ISubscription inventorySubs;

    @Nullable
    protected ISubscription tankSubs;

    private final List<Runnable> changeListeners = new ArrayList<>();

    public MEAdvancedPatternBufferPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        internalBuffer = new KeyStorage();
        outputInventory = new MEDualOutputHatchPartMachine.InaccessibleInfiniteHandler(this, changeListeners,
                internalBuffer);
        outputTank = new MEDualOutputHatchPartMachine.InaccessibleInfiniteTank(this, changeListeners, internalBuffer);
        internalBuffer.setOnContentsChanged(() -> changeListeners.forEach(Runnable::run));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        inventorySubs = outputInventory.addChangedListener(this::updateInventorySubscription);
        tankSubs = outputTank.addChangedListener(this::updateInventorySubscription);
    }

    protected void updateInventorySubscription() {
        if (shouldSubscribe()) {
            autoIOSubs = subscribeServerTick(autoIOSubs, this::autoIO);
        } else if (autoIOSubs != null) {
            autoIOSubs.unsubscribe();
            autoIOSubs = null;
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (inventorySubs != null) {
            inventorySubs.unsubscribe();
            inventorySubs = null;
        }
        if (tankSubs != null) {
            tankSubs.unsubscribe();
            tankSubs = null;
        }
    }

    @Override
    public List<RecipeHandlerList> getRecipeHandlers() {
        List<IRecipeHandler<?>> handlers = List.of(outputInventory, outputTank);
        var outList = RecipeHandlerList.of(IO.OUT, getPaintingColor(), handlers);
        var all = super.getRecipeHandlers();
        all.add(outList);
        return all;
    }

    //////////////////////////////////////
    // ********** GUI ***********//
    //////////////////////////////////////

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);

        sideTabs.attachSubTab(new MEDualOutputConfigurator(this, internalBuffer));
    }

    @Override
    public int getColSize() {
        return 8;
    }

    @Override
    public int getMaxPatternCount() {
        return MAX_PATTERN_COUNT;
    }

    @Override
    protected boolean shouldSubscribe() {
        return super.shouldSubscribe() && !internalBuffer.storage.isEmpty();
    }

    protected void autoIO() {
        if (!this.shouldSyncME()) return;
        if (this.updateMEStatus()) {
            var grid = getMainNode().getGrid();
            if (grid != null && !internalBuffer.isEmpty()) {
                internalBuffer.insertInventory(grid.getStorageService().getInventory(), actionSource);
            }
            this.updateInventorySubscription();
        }
    }
}
