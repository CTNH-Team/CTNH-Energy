package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import appeng.api.networking.IGrid;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.common.me.taglist.TagPriorityList;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputBusPartMachine;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.ITagFilter;
import tech.vixhentx.mcmod.ctnhlib.client.gui.RightConfiguratorPanel;

public class METagStockingBusPartMachine extends MEStockingBusPartMachine implements ITagFilter {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            METagStockingBusPartMachine.class, MEInputBusPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    @Getter
    private String whiteList = "";

    @Persisted
    @DescSynced
    @Getter
    private String blackList = "";

    private IPartitionList filter;

    public METagStockingBusPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    protected NotifiableItemStackHandler createInventory(Object... args) {
        this.aeItemHandler = new ExportOnlyAEStockingItemList(this, CONFIG_SIZE);
        return aeItemHandler;
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void attachRightConfigurators(@NotNull RightConfiguratorPanel configuratorPanel) {
        super.attachRightConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new TagFilterConfigurator(this));
    }

    public void setBlackList(String blackList) {
        if(!blackList.equals(this.blackList)){
            this.blackList = blackList;
            filter = null;
            if(isAutoPull()) setAutoPull(true);
        }
    }

    public void setWhiteList(String whiteList) {
        if(!whiteList.equals(this.whiteList)){
            this.whiteList = whiteList;
            filter = null;
            if(isAutoPull()) setAutoPull(true);
        }
    }

    private IPartitionList getFilter(){
        if(filter == null){
            filter = new TagPriorityList(whiteList, blackList);
        }
        return filter;
    }

    @Override
    public boolean testConfiguredInOtherPart(@Nullable GenericStack config) {
        if(config != null && isAutoPull() && !getFilter().isListed(config.what())){
            return true;
        }
        return super.testConfiguredInOtherPart(config);
    }
}
