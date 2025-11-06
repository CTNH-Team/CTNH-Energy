package tech.luckyblock.mcmod.ctnhenergy.registry;

import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.vixhentx.mcmod.ctnhlib.registrate.CNRegistrate;

public class CERegistrate extends CNRegistrate {
    protected CERegistrate() {
        super(CTNHEnergy.MODID);
    }

    public static CERegistrate create(){
        return new CERegistrate();
    }


}
