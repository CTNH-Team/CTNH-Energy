package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import appeng.api.stacks.AEItemKey;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "appeng.integration.modules.emi.EmiItemStackConverter", remap = false)
public class EmiItemStackConverterMixin {
    // fix JemiStack is empty
    @Redirect(method = "toGenericStack", at = @At(value = "INVOKE", target = "Lappeng/api/stacks/AEItemKey;of(Lnet/minecraft/world/item/ItemStack;)Lappeng/api/stacks/AEItemKey;"))
    AEItemKey fixEmpty(ItemStack stack, @Local(name = "item") Item item){
        if(stack.isEmpty()){
            return AEItemKey.of(item);
        }
        return AEItemKey.of(stack);
    }
}
