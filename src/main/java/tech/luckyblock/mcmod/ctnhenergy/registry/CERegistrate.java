package tech.luckyblock.mcmod.ctnhenergy.registry;

import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.vixhentx.mcmod.ctnhlib.registrate.CNRegistrate;

public class CERegistrate extends CNRegistrate {
    protected CERegistrate() {
        super(CTNHEnergy.MODID);
    }

    public static CERegistrate create(){
        return new CERegistrate();
    }

    public MutableComponent addLang(String type, String id, String en, String cn) {

        return addLang(type, CTNHEnergy.id(id), en, cn);
    }
}
