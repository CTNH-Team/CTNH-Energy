package tech.luckyblock.mcmod.ctnhenergy.common.me.cell;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.item.IEUCell;

public class EuCellHandler implements ICellHandler {

    public static EuCellHandler HANDLER = new EuCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return !is.isEmpty() && is.getItem() instanceof IEUCell;
    }

    @Override
    public @Nullable EUCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
        if (isCell(is)) {
            return new EUCellInventory(is, host);
        }
        return null;
    }


}
