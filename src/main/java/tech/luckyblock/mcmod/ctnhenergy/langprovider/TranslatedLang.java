package tech.luckyblock.mcmod.ctnhenergy.langprovider;

public final class TranslatedLang extends Lang{

    public final String en_translation, cn_translation;

    public TranslatedLang(String en_translation, String cn_translation) {
        this.en_translation = en_translation;
        this.cn_translation = cn_translation;
    }

    Lang erase() {
        return new Lang(key);
    }
}
