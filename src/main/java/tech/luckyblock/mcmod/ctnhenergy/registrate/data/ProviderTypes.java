package tech.luckyblock.mcmod.ctnhenergy.registrate.data;

import com.tterrag.registrate.providers.ProviderType;
import tech.luckyblock.mcmod.ctnhenergy.registrate.lang.RegistrateCNLangProvider;

public class ProviderTypes {
    public static ProviderType<RegistrateCNLangProvider> CNLANG = ProviderType.register("cnlang", (p,e)->
        new RegistrateCNLangProvider(p, e.getGenerator().getPackOutput())
    ) ;
}
