package translator.enums;

import translator.service.ITranslator;
import translator.service.VoidTranslator;
import translator.service.get.impl.GoogleTranslation;
import translator.service.get.impl.YandexTranslation;
import translator.service.post.impl.FreeTranslation;

/**
 * All translation engine available
 */
public enum EEngine {
    GOOGLE_TRANSLATION("Google translate (translate.google.fr, ru -> en only", new GoogleTranslation(), false, false),
//    BING_TRANSLATION("Bing translator (www.bing.com/translator, ru -> en only", new BingTranslation(), false, true),
    YANDEX_TRANSLATION("Yandex translate (translate.yandex.com, ru -> en only", new YandexTranslation(), false, false),
    FREE_TRANSLATION("Free Translation (www.freetranslation.com, ru -> en only)", new FreeTranslation(), false, false),
//    ONLINE_TRANSLATION("Online Translation (www.online-translator.com, ru -> en only)", new OnlineTranslator(), false, false),
    VOID("Dictionnary only", new VoidTranslator(), false, false)
    ;
    
    private final String name;
    private final ITranslator translator;
    private final boolean isAllDst;
    private final boolean requiredAppId;
    private String appId;
    
    private EEngine(final String name, ITranslator translator, boolean enableDstSelection, boolean requiredAppId) {
        this.name = name;
        this.translator = translator;
        this.isAllDst = enableDstSelection;
        this.requiredAppId=requiredAppId;
    }

    public String getName() {
        return name;
    }

    public ITranslator getTranslator() {
        return translator;
    }

    public boolean isIsAllDst() {
        return isAllDst;
    }

    public boolean isRequiredAppId() {
        return requiredAppId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
