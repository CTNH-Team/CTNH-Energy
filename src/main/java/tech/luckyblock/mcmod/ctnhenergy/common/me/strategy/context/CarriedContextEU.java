package tech.luckyblock.mcmod.ctnhenergy.common.me.strategy.context;


import appeng.helpers.WirelessTerminalMenuHost;
import com.gregtechceu.gtceu.api.GTValues;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tech.luckyblock.mcmod.ctnhenergy.api.EUItemContext;
import tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.menu.AEBaseMenuAccessor;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

public record CarriedContextEU(Player player, AbstractContainerMenu menu) implements EUItemContext {

    @Override
    public ItemStack getStack() {
        return this.menu.getCarried();
    }

    @Override
    public void setStack(ItemStack stack) {
        this.menu.setCarried(stack);
    }

    public void addOverflow(ItemStack stack) {
        if (this.menu.getCarried().isEmpty()) {
            this.menu.setCarried(stack);
        } else {
            this.player.getInventory().placeItemBackInInventory(stack);
        }
    }

    @Override
    public int getTier() {
        int tier = -1;
        if(menu instanceof AEBaseMenuAccessor aeBaseMenu){
            var part = aeBaseMenu.getPart();
            var item = aeBaseMenu.getItemMenuHost();
            if(part != null && part.getGridNode() != null){
                tier = CEUtil.getGridTier(part.getGridNode());
            }
            else if(item instanceof WirelessTerminalMenuHost host && host.getActionableNode()!= null){
                tier = CEUtil.getGridTier(host.getActionableNode());
            }
        }
        return tier == -1 ? GTValues.MAX : tier;
    }
}
