package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfiguratorButton;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.utils.Position;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import appeng.api.networking.IGridNode;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.common.me.taglist.TagPriorityList;
import lombok.Getter;
import lombok.Setter;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.ITagFilter;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.gui.AutoPullAmountConfigurator;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.gui.ConfigWidget;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.gui.TagFilterConfigurator;
import tech.luckyblock.mcmod.ctnhenergy.utils.MEConfigUtil;

import java.util.List;
import java.util.function.Predicate;

public class MEStokingInputMachine extends MEInputMachine implements ITagFilter {

    @Persisted
    @DescSynced
    @Getter
    protected boolean autoPull;

    @Getter
    @Setter
    @Persisted
    private int minStackSize = 0;

    @Getter
    @Setter
    @Persisted
    private int maxStackSize = 0;

    @Persisted
    @Getter
    private String whiteList = "";

    @Persisted
    @Getter
    private String blackList = "";

    private IPartitionList filter;

    public MEStokingInputMachine(IMachineBlockEntity holder, int tier, IO io, int configSize,
                                 Predicate<AEKey> predicate) {
        super(holder, tier, io, configSize, predicate);
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(new Position(0, 0));
        // ME Network status
        group.addWidget(new LabelWidget(3, 0, () -> isNodeActive() ?
                "gtceu.gui.me_network.online" :
                "gtceu.gui.me_network.offline"));

        group.addWidget(new ConfigWidget(3, 10, stackHandler, keyPredicate, this::isAutoPull));
        return group;
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel left, ConfiguratorPanel right) {
        right.attachConfigurators(new IFancyConfiguratorButton.Toggle(
                GuiTextures.BUTTON_AUTO_PULL.getSubTexture(0, 0, 1, 0.5),
                GuiTextures.BUTTON_AUTO_PULL.getSubTexture(0, 0.5, 1, 0.5),
                this::isAutoPull,
                (clickData, pressed) -> setAutoPull(pressed))
                .setTooltipsSupplier(pressed -> List.of(Component.translatable("gtceu.gui.me_bus.auto_pull_button"))));

        right.attachConfigurators(new AutoPullAmountConfigurator(this));
        right.attachConfigurators(new TagFilterConfigurator(this));
        super.attachConfigurators(left, right);
    }

    public void setAutoPull(boolean autoPull) {
        if (this.autoPull != autoPull) {
            this.autoPull = autoPull;
            stackHandler.clear();
        }
    }

    @Override
    protected void autoIO() {
        if (isMESyncTick()) {
            IGridNode node = getActionableNode();
            var keyCounter = new KeyCounter();
            if (isWorkingEnabled() && node != null) {
                keyCounter = node.getGrid().getStorageService().getCachedInventory();
            }
            if (isAutoPull()) {
                stackHandler.autoPull(keyCounter, maxStackSize == 0 ? Integer.MAX_VALUE : maxStackSize,
                        (key, amount) -> keyPredicate.test(key) && getFilter().isListed(key) && amount >= minStackSize);
            } else {
                stackHandler.updateStacks(keyCounter);
            }
            shouldSubscribe = isWorkingEnabled() && isNodeActive();
            updateIOSubscription();
        }
    }

    private IPartitionList getFilter() {
        if (filter == null) {
            filter = new TagPriorityList(whiteList, blackList);
        }
        return filter;
    }

    public void setBlackList(String blackList) {
        if (!blackList.equals(this.blackList)) {
            this.blackList = blackList;
            filter = null;
        }
    }

    public void setWhiteList(String whiteList) {
        if (!whiteList.equals(this.whiteList)) {
            this.whiteList = whiteList;
            filter = null;
        }
    }

    @Override
    public void writeConfig(CompoundTag tag) {
        super.writeConfig(tag);
        MEConfigUtil.writeAutoPull(tag, isAutoPull());
        MEConfigUtil.writeMinStackSize(tag, minStackSize);
        MEConfigUtil.writeMaxStackSize(tag, maxStackSize);
        MEConfigUtil.writeWhiteList(tag, whiteList);
        MEConfigUtil.writeBlackList(tag, blackList);
    }

    @Override
    public void readConfig(CompoundTag tag) {
        super.readConfig(tag);
        MEConfigUtil.readAutoPull(tag, this::setAutoPull);
        MEConfigUtil.readMinStackSize(tag, this::setMinStackSize);
        MEConfigUtil.readMaxStackSize(tag, this::setMaxStackSize);
        MEConfigUtil.readWhiteList(tag, this::setWhiteList);
        MEConfigUtil.readBlackList(tag, this::setBlackList);
    }
}
