package tech.luckyblock.mcmod.ctnhenergy.mixin.omni;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wintercogs.ae2omnicells.common.init.OCItems;
import com.wintercogs.ae2omnicells.common.items.AEPortableUniversalCellItem;
import com.wintercogs.ae2omnicells.common.items.AEUniversalCellItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(value = OCItems.class,  remap = false)
public abstract class OCItemsMixin {

    @ModifyVariable(method = "registerComplexCell", at = @At(value = "HEAD"), ordinal = 1, argsOnly = true)
    private static int modifyComplexCell(int types) {
        return types * 10;
    }

    @WrapMethod(method = "registerQuantumCell")
    private static RegistryObject<AEUniversalCellItem> modifyQuantumCell(String name, int idlePower, int types, int kilobytes, Operation<RegistryObject<AEUniversalCellItem>> original) {
        int quantumTypes = 1 << (CE$logAofB(3, idlePower)- 1);
        return original.call(name, idlePower, quantumTypes, -1);
    }

    @WrapMethod(method = "registerPortableCell")
    private static RegistryObject<AEPortableUniversalCellItem> modifyPortableCell(String name, int idlePower, int types, int kilobytes, Operation<RegistryObject<AEPortableUniversalCellItem>> original) {
        if(name.startsWith("portable_complex")){
            return original.call(name, idlePower, types * 10, kilobytes);
        }
        else if(name.startsWith("portable_quantum")){
            int quantumTypes = 1 << (CE$logAofB(3, idlePower) - 1);
            return original.call(name, idlePower, quantumTypes, -1);
        }
        return original.call(name, idlePower, types, kilobytes);
    }

    @Unique
    private static int CE$logAofB(int a, int b) {
        int result = 0;
        while (b > 1) {
            b /= a;
            result++;
        }
        return result;
    }

}
