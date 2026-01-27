package tech.luckyblock.mcmod.ctnhenergy.mixin.gtm;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferProxyPartMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMachines;

@Mixin(value = MEPatternBufferProxyPartMachine.class, remap = false)
public class MEPatternBufferProxyPartMachineMixin extends MetaMachine {
    @Shadow
    private @Nullable BlockPos bufferPos;

    public MEPatternBufferProxyPartMachineMixin(IMachineBlockEntity holder) {
        super(holder);
    }

    @Inject(method = "onLoad", at = @At("TAIL"))
    void replace(CallbackInfo ci){
        BlockPos blockPos = getHolder().pos();
        BlockState newBlockState = CEMachines.ME_PATTERN_BUFFER_PROXY.getBlock().defaultBlockState();
        if(getLevel() == null) return;
        getLevel().setBlockAndUpdate(blockPos, newBlockState);
        if (getLevel().getBlockEntity(blockPos) instanceof IMachineBlockEntity newHolder) {
            if(newHolder.getMetaMachine() instanceof tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferProxyPartMachine newMachine){
                newMachine.setFrontFacing(this.getFrontFacing());
                newMachine.setUpwardsFacing(this.getUpwardsFacing());
                newMachine.setPaintingColor(this.getPaintingColor());
                newMachine.setBuffer(bufferPos);
            }
        }
    }
}
