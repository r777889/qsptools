package translator.service;

import java.io.IOException;
import java.nio.charset.Charset;

import translator.enums.ELanguage;

/**
 * Do nothing implementation.
 */
public class VoidTranslator implements ITranslator {

    @Override
    public String translate(String txt, ELanguage src, ELanguage dst, String appId) throws IOException {
        throw new IOException("Void translator translate nothing.");
    }
    
}
