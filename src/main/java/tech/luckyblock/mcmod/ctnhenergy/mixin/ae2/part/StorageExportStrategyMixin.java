package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.part;

import net.minecraft.core.Direction;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.stacks.AEKey;
import appeng.parts.automation.HandlerStrategy;
import appeng.parts.automation.StorageExportStrategy;
import appeng.util.BlockApiCache;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.luckyblock.mcmod.ctnhenergy.api.IMaintainingContext;

@Mixin(value = StorageExportStrategy.class, remap = false)
public class StorageExportStrategyMixin<C, S> {

    @Shadow
    @Final
    private BlockApiCache<C> apiCache;

    @Shadow
    @Final
    private Direction fromSide;

    @Shadow
    @Final
    private HandlerStrategy<C, S> handlerStrategy;

    @WrapMethod(method = "transfer")
    private long checkMaintainingCount(StackTransferContext context, AEKey what, long amount,
                                       Operation<Long> original) {
        if (context instanceof IMaintainingContext maintainingContext) {
            long maintainingAmount = maintainingContext.getMaintainingAmount();
            if (maintainingAmount > 0) {
                var adjacentHandler = apiCache.find(fromSide);
                if (adjacentHandler != null) {
                    var adjacentStorage = handlerStrategy.getFacade(adjacentHandler);
                    for (int slot = 0; slot < adjacentStorage.getSlots(); slot++) {
                        var stack = adjacentStorage.getStackInSlot(slot);
                        if (stack != null && stack.what().equals(what)) {
                            maintainingAmount -= stack.amount();
                            if (maintainingAmount <= 0) break;
                        }
                    }
                    if (maintainingAmount <= 0) {
                        return 0;
                    } else {
                        amount = maintainingAmount;
                    }
                }
            }
        }
        return original.call(context, what, amount);
    }
}
