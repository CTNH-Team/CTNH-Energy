package tech.luckyblock.mcmod.ctnhenergy.integration.emi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import appeng.client.gui.style.Blitter;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;

import java.util.ArrayList;
import java.util.List;

/**
 * EMI representation of the singleton AE2 EU key.
 */
public final class EUEmiStack extends EmiStack {

    public static final ResourceLocation ID = CTNHEnergy.id("eu");

    private EUEmiStack(long amount) {
        this.amount = amount;
    }

    public static EUEmiStack of(long amount) {
        return new EUEmiStack(amount);
    }

    @Override
    public EmiStack copy() {
        var copy = of(amount);
        copy.setChance(chance);
        copy.setRemainder(getRemainder().copy());
        copy.comparison(comparison);
        return copy;
    }

    @Override
    public boolean isEmpty() {
        return amount <= 0;
    }

    @Override
    public CompoundTag getNbt() {
        return null;
    }

    @Override
    public Object getKey() {
        return EUKey.EU;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float delta, int flags) {
        if ((flags & EmiIngredient.RENDER_ICON) != 0) {
            Blitter.sprite(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(CTNHEnergy.id("block/eu")))
                    .blending(false)
                    .dest(x, y, 16, 16)
                    .blit(graphics);
        }
    }

    @Override
    public List<Component> getTooltipText() {
        return List.of(EUKey.EU_NAME);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        var tooltip = new ArrayList<ClientTooltipComponent>();
        tooltip.add(ClientTooltipComponent.create(EUKey.EU_NAME.getVisualOrderText()));
        tooltip.addAll(super.getTooltip());
        return tooltip;
    }

    @Override
    public Component getName() {
        return EUKey.EU_NAME;
    }
}
