package tech.luckyblock.mcmod.ctnhenergy.mixin.gtm;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMachines;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = MEPatternBufferPartMachine.class, remap = false)
public abstract class MEPatternBufferPartMachineMixin extends MetaMachine {
    @Shadow
    @Final
    private CustomItemStackHandler patternInventory;

    @Shadow
    @Final
    protected NotifiableItemStackHandler shareInventory;

    @Shadow
    @Final
    protected NotifiableFluidTank shareTank;

    public MEPatternBufferPartMachineMixin(IMachineBlockEntity holder) {
        super(holder);
    }

    @Inject(method = "onLoad", at = @At("TAIL"))
    void replace(CallbackInfo ci){
        List<ItemStack> patterns = new ArrayList<>();
        for(int i = 0; i < patternInventory.getSlots(); i++){
            patterns.add(patternInventory.extractItem(i, 1, false));
        }
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < shareInventory.getSlots(); i++){
            items.add(shareInventory.extractItem(i, Integer.MAX_VALUE, false));
        }
        List<FluidStack> fluids = new ArrayList<>();
        for(int i = 0; i < shareTank.getTanks(); i++){
            fluids.add(shareTank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE));
        }

        BlockPos blockPos = getHolder().pos();
        BlockState newBlockState = CEMachines.ME_PATTERN_BUFFER.getBlock().defaultBlockState();
        if(getLevel() == null) return;
        getLevel().setBlockAndUpdate(blockPos, newBlockState);
        if (getLevel().getBlockEntity(blockPos) instanceof IMachineBlockEntity newHolder) {
            if (newHolder.getMetaMachine() instanceof tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferPartMachine newMachine) {
                newMachine.setFrontFacing(this.getFrontFacing());
                newMachine.setUpwardsFacing(this.getUpwardsFacing());
                newMachine.setPaintingColor(this.getPaintingColor());
                for(int i = 0; i < patterns.size(); i++ ){
                    newMachine.getPatternInventory().insertItem(i, patterns.get(i), false);
                }
                for(int i = 0; i < items.size(); i++ ){
                    newMachine.getShareInventory().insertItem(i, items.get(i), false);
                }
                for (FluidStack fluid : fluids) {
                    newMachine.getShareTank().fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                }

            }
        }

    }
}
