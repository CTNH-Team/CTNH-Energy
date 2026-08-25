package tech.luckyblock.mcmod.ctnhenergy.common.stats;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.ctnhlang.Key;

import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

public final class CEStats {

    public static final ResourceLocation ENCODED_PATTERNS = register("encoded_patterns");
    public static final ResourceLocation ME_CRAFT_REQUESTS = register("me_craft_requests");

    @Key("stat.ctnhenergy.encoded_patterns")
    @CN("编码样板数")
    @EN("Patterns Encoded")
    static Lang encodedPatterns;

    @Key("stat.ctnhenergy.me_craft_requests")
    @CN("ME合成请求数")
    @EN("ME Crafting Requests")
    static Lang meCraftRequests;

    private CEStats() {}

    public static void init() {
    }

    private static ResourceLocation register(String path) {
        ResourceLocation id = CTNHEnergy.id(path);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        return id;
    }

    public static void awardEncodedPattern(Player player) {
        player.awardStat(ENCODED_PATTERNS);
    }

    public static void awardMECraftRequest(Player player) {
        player.awardStat(ME_CRAFT_REQUESTS);
    }
}
