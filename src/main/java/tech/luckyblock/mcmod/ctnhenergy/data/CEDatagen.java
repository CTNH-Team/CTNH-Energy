package tech.luckyblock.mcmod.ctnhenergy.data;

import com.tterrag.registrate.providers.ProviderType;
import tech.luckyblock.mcmod.ctnhenergy.data.lang.ChineseLangHandler;
import tech.luckyblock.mcmod.ctnhenergy.data.lang.EnglishLangHandler;

import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;
import static tech.vixhentx.mcmod.ctnhlib.registrate.data.ProviderTypes.CNLANG;

public class CEDatagen {
    public static void init(){
        REGISTRATE.addDataGenerator(ProviderType.LANG, EnglishLangHandler::init);
        REGISTRATE.addDataGenerator(CNLANG, ChineseLangHandler::init);
    }
}
