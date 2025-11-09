package tech.luckyblock.mcmod.ctnhenergy.mixin.patternprovider;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = PatternContainerGroup.class, remap = false)
public class PatternContainerGroupMixin {
    @Inject(
            method = "fromMachine",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private static void getMachineName(Level level, BlockPos pos, Direction side, CallbackInfoReturnable<PatternContainerGroup> cir){
        var target = level.getBlockEntity(pos);
        AEItemKey icon;
        Component groupName;
        List<Component> tooltip = new ArrayList<>();
        if(target instanceof MetaMachineBlockEntity blockEntity && blockEntity.getMetaMachine() instanceof MultiblockPartMachine machine && machine.isFormed()){
            IMultiController controller = machine.getControllers().first();
            MultiblockMachineDefinition controllerDefinition = controller.self().getDefinition();

            int circuitConfiguration = -1;
            if(machine instanceof IHasCircuitSlot circuitSlot){
                ItemStack circuitStack = circuitSlot.isCircuitSlotEnabled() ? circuitSlot.getCircuitInventory().storage.getStackInSlot(0) :
                        ItemStack.EMPTY;
                circuitConfiguration = circuitStack.isEmpty() ? -1 : IntCircuitBehaviour.getCircuitConfiguration(circuitStack);
            }
            icon = AEItemKey.of(controllerDefinition.asStack());

            groupName = circuitConfiguration != -1 ?
                    Component.translatable(controllerDefinition.getDescriptionId())
                            .append(" - " + circuitConfiguration) :
                    Component.translatable(controllerDefinition.getDescriptionId());

            tooltip.add(Component.translatable(machine.getDefinition().getDescriptionId()));
            cir.setReturnValue(
                    new PatternContainerGroup(icon, groupName, tooltip)
            );
        }
    }

}
