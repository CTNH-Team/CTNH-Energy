package tech.luckyblock.mcmod.ctnhenergy.integration.emi;

import appeng.api.integrations.emi.EmiStackConverter;
import appeng.api.stacks.GenericStack;
import dev.emi.emi.api.stack.EmiStack;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;

public class EUStackConverter implements EmiStackConverter {

    @Override
    public Class<?> getKeyType() {
        return EUKey.class;
    }

    @Override
    public @Nullable EmiStack toEmiStack(GenericStack genericStack) {
        if (genericStack.what() == EUKey.EU) {
            return EUEmiStack.of(genericStack.amount());
        }
        return null;
    }

    @Override
    public @Nullable GenericStack toGenericStack(EmiStack emiStack) {
        if (emiStack instanceof EUEmiStack euEmiStack) {
            return new GenericStack(EUKey.EU, euEmiStack.getAmount());
        }
        return null;
    }
}
