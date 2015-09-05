package translator.service;

import java.io.IOException;
import java.nio.charset.Charset;

import translator.enums.ELanguage;

/**
 * Interface to be implemented by all tranlation service.
 * @author pseudo555
 */
public interface ITranslator {
    
    /**
     * Translate TXT from src language to dst language
     * @param txt Text to be translated
     * @param src Source language
     * @param dst Destination language
     * @param appId appId to be used by translator service
     * @return translated string
     * @throws IOException if service failed
     */
    String translate(String txt, ELanguage src, ELanguage dst, String appId) throws IOException;   
    
}
