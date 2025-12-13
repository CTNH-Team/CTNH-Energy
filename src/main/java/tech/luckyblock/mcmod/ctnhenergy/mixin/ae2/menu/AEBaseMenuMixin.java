package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.menu;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.AppEngSlot;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.lowdragmc.lowdraglib.core.mixins.accessor.AbstractContainerMenuAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = AEBaseMenu.class, remap = false)
public abstract class AEBaseMenuMixin extends AbstractContainerMenu {

    @Final
    @Shadow
    private Map<Slot, SlotSemantic> semanticBySlot;

    @Final
    @Shadow
    private ArrayListMultimap<SlotSemantic, Slot> slotsBySemantic;

    protected AEBaseMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(
            method = "addSlot(Lnet/minecraft/world/inventory/Slot;Lappeng/menu/SlotSemantic;)Lnet/minecraft/world/inventory/Slot;",
            at = @At(value = "HEAD"),
            cancellable = true)
    protected void redirectAddSlot(Slot slot, SlotSemantic semantic, CallbackInfoReturnable<Slot> cir){
        if(semantic == SlotSemantics.UPGRADE)
        {
            slot = this.CE$addSlot(slot);
            Preconditions.checkState(!semanticBySlot.containsKey(slot));
            semanticBySlot.put(slot, semantic);
            slotsBySemantic.put(semantic, slot);
            cir.setReturnValue(slot);
        }
    }


    @Unique
    protected Slot CE$addSlot(Slot slot) {
        if (slot instanceof AppEngSlot s) {
            s.setMenu((AEBaseMenu)(Object)this);
        }
        slot.index = -1;
        this.slots.add(0, slot);
        for(var s: slots){
            s.index += 1;
        }
        ((AbstractContainerMenuAccessor)this).getLastSlots().add(ItemStack.EMPTY);
        ((AbstractContainerMenuAccessor)this).getRemoteSlots().add(ItemStack.EMPTY);
        return slot;
    }
}
