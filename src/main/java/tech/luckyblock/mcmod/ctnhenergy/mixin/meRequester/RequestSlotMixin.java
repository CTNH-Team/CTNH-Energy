package tech.luckyblock.mcmod.ctnhenergy.mixin.meRequester;

import net.minecraft.world.item.ItemStack;

import appeng.api.inventories.InternalInventory;
import appeng.menu.slot.FakeSlot;
import com.almostreliable.merequester.client.RequestSlot;
import com.almostreliable.merequester.client.abstraction.RequesterReference;
import com.almostreliable.merequester.platform.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = RequestSlot.class, remap = false)
public abstract class RequestSlotMixin extends FakeSlot {

    @Shadow
    public abstract RequesterReference getRequesterReference();

    @Shadow
    public abstract int getSlot();

    public RequestSlotMixin(InternalInventory inv, int invSlot) {
        super(inv, invSlot);
    }

    @Override
    public void setFilterTo(ItemStack itemStack) {
        Platform.sendDragAndDrop(
                getRequesterReference().getRequesterId(),
                getSlot(),
                itemStack);
    }
}
