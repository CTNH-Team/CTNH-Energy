package tech.luckyblock.mcmod.ctnhenergy.integration.emi;

import com.mojang.logging.LogUtils;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import org.slf4j.Logger;
import tech.luckyblock.mcmod.ctnhenergy.common.item.DynamoCardItem;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;

@EmiEntrypoint
public class CEEMIPlugin implements EmiPlugin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void register(EmiRegistry registry) {
        // Treat Dynamo Card NBT variants as distinct stacks in EMI.
        registry.setDefaultComparison(CEItems.DYNAMO_CARD.asItem(), Comparison.compareData(stack -> {
            var nbt = stack.getNbt();
            if (nbt != null && nbt.contains(DynamoCardItem.VOLTAGE)) {
                return nbt.getInt(DynamoCardItem.VOLTAGE);
            }
            return -1;
        }));
        registry.removeEmiStacks(EmiStack.of(CEItems.DYNAMO_CARD.asStack()));
    }
}
