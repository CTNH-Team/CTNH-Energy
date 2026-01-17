package tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.advanced;

import appeng.api.config.Actionable;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.*;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.AETextInputButtonWidget;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEPatternViewSlotWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;
import com.gregtechceu.gtceu.utils.GTMath;
import com.gregtechceu.gtceu.utils.ItemStackHashStrategy;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.*;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.crafting.pattern.ProcessingPatternItem;
import appeng.helpers.patternprovider.PatternContainer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.MEDualOutputConfigurator;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.MEDualOutputHatchPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.ProgrammableSlotRecipeHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferPartMachine;
import yuuki1293.pccard.impl.PatternProviderLogicImpl;
import yuuki1293.pccard.wrapper.IAEPattern;
import yuuki1293.pccard.wrapper.IPatternProviderLogicMixin;

import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEAdvancedPatternBufferPartMachine extends MEPatternBufferPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEAdvancedPatternBufferPartMachine.class, MEPatternBufferPartMachine.MANAGED_FIELD_HOLDER);
    public static final int MAX_PATTERN_COUNT = 54;

    @Persisted
    private final KeyStorage internalBuffer;

    @Persisted
    @Getter
    private final NotifiableItemStackHandler outputInventory;

    @Persisted
    @Getter
    private final NotifiableFluidTank outputTank;

    @Nullable
    protected ISubscription tankSubs;

    private final List<Runnable> changeListeners = new ArrayList<>();

    public MEAdvancedPatternBufferPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        internalBuffer = new KeyStorage();
        outputInventory = new MEDualOutputHatchPartMachine.InaccessibleInfiniteHandler(this, changeListeners, internalBuffer);
        outputTank = new MEDualOutputHatchPartMachine.InaccessibleInfiniteTank(this, changeListeners, internalBuffer);
        internalBuffer.setOnContentsChanged(()-> changeListeners.forEach(Runnable::run));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        tankSubs = outputTank.addChangedListener(this::updateInventorySubscription);
    }

    @Override
    public void onUnload() {
        super.onUnload();
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
        return 6;
    }

    @Override
    public int getMaxPatternCount() {
        return MAX_PATTERN_COUNT;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected boolean shouldSubscribe() {
        return super.shouldSubscribe() && !internalBuffer.storage.isEmpty();
    }

    @Override
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
