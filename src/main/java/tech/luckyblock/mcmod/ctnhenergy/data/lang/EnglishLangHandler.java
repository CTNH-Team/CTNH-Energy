package tech.luckyblock.mcmod.ctnhenergy.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class EnglishLangHandler {

    public static void init(RegistrateLangProvider provider) {
        provider.add("ctnhenergy.copyright.info", "§6Added by CTNH Energy§r");

        provider.add("config.jade.plugin_ctnhenergy.ad_me_pattern_buffer_proxy", "Advanced ME Pattern Buffer");
        provider.add("config.jade.plugin_ctnhenergy.ad_me_pattern_buffer", "Advanced ME Pattern Buffer Proxy");
        provider.add("config.jade.plugin_ctnhenergy.ae_eu_info", "AE EU Info");
        provider.add("ctnhenergy.jade.ae_eu.network_voltage", "Network Voltage Tier: %s");
        provider.add("ctnhenergy.jade.ae_eu.dynamo_voltage", "Dynamo Output Voltage: %s");
    }
}
