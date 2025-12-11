package tech.luckyblock.mcmod.ctnhenergy.common.me.cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.CEConfig;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;

public class EUCellInventory implements StorageCell {

    protected final ItemStack stack;
    protected final IElectricItem cell;
    @Nullable
    protected final ISaveProvider container;
    protected final boolean hasVoidUpgrade;

    public EUCellInventory(ItemStack stack, @Nullable ISaveProvider container){
        this.stack = stack;
        this.cell = GTCapabilityHelper.getElectricItem(stack);
        assert cell != null;
        this.container = container;
        hasVoidUpgrade = UpgradeInventories.forItem(stack, 3).isInstalled(AEItems.VOID_CARD);
    }


    @Override
    public CellState getStatus() {
        if(cell.getCharge() == 0)
            return CellState.EMPTY;
        else if (cell.getCharge() == cell.getMaxCharge())
            return CellState.FULL;
        return CellState.NOT_EMPTY;
    }


    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if(what != EUKey.EU) return 0;
        var inserted = cell.charge(amount, GTValues.MAX, true, mode.isSimulate());

        return hasVoidUpgrade ? amount : inserted;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if(what != EUKey.EU) return 0;

        return cell.discharge(amount, GTValues.MAX, true, true, mode.isSimulate());
    }

    @Override
    public double getIdleDrain() {
        return 0;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if(cell.getCharge() > 0){
            out.add(EUKey.EU, cell.getCharge());
        }
    }

    @Override
    public void persist() {
    }

    @Override
    public Component getDescription() {
        return stack.getHoverName();
    }

    public long getTotalBytes(){
        return cell.getMaxCharge() / CEConfig.INSTANCE.appeu.amountPerByte;
    }

    public long getUsedBytes(){
        long amountPerByte = CEConfig.INSTANCE.appeu.amountPerByte;
        return (cell.getCharge() + amountPerByte -1 ) / amountPerByte;
    }
}
