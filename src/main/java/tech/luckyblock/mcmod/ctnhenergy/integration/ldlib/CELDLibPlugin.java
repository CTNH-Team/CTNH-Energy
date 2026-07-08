package tech.luckyblock.mcmod.ctnhenergy.integration.ldlib;

import com.lowdragmc.lowdraglib.plugin.ILDLibPlugin;
import com.lowdragmc.lowdraglib.plugin.LDLibPlugin;

import appeng.api.stacks.AEKey;
import tech.luckyblock.mcmod.ctnhenergy.network.syncdata.AEKeyPayLoad;

import static com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries.*;

@LDLibPlugin
public class CELDLibPlugin implements ILDLibPlugin {

    @Override
    public void onLoad() {
        registerSimple(AEKeyPayLoad.class, AEKeyPayLoad::new, AEKey.class, 100);
    }
}
