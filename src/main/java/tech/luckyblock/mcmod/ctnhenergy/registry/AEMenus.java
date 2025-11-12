package tech.luckyblock.mcmod.ctnhenergy.registry;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.gui.QuantumComputerMenu;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port.QuantumComputerMENetworkPortBlockEntity;

import java.util.function.Supplier;

public class AEMenus {
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, CTNHEnergy.MODID);

    public static final Supplier<MenuType<QuantumComputerMenu>> QUANTUM_COMPUTER =
            create("jiuzhang_quantum_computer", QuantumComputerMenu::new, QuantumComputerMENetworkPortBlockEntity.class);

    private static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return DR.register(
                id, () -> MenuTypeBuilder.create(factory, host).build(id));
    }
}
