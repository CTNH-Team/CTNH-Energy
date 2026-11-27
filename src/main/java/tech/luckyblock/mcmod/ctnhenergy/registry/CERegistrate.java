package tech.luckyblock.mcmod.ctnhenergy.registry;

import net.minecraft.network.chat.MutableComponent;

import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.vixhentx.mcmod.ctnhlib.registrate.CNRegistrate;
import tech.vixhentx.mcmod.ctnhlib.registrate.CTNHCopyright;

public class CERegistrate extends CNRegistrate {

    protected CERegistrate() {
        super(CTNHEnergy.MODID,
                new CTNHCopyright("Energy", 0xA789CF, 0xAE8FD3, 0xB596D7, 0xBC9CDB, 0xC3A3DF, 0xCAA9E3));
    }

    public static CERegistrate create() {
        return new CERegistrate();
    }

    public MutableComponent addLang(String type, String id, String en, String cn) {
        return addLang(type, CTNHEnergy.id(id), en, cn);
    }
}
