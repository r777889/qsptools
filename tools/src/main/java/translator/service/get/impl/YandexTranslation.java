package translator.service.get.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import hmi.log.QspLogger;
import translator.enums.ELanguage;
import translator.service.get.AbstractGetTranslator;

/**
 *
 * @author pseudo555
 */
public class YandexTranslation extends AbstractGetTranslator {
    
    @Override
    protected String getUrl(String byteString, ELanguage srcLg, ELanguage dstLg, String appId){
        return "https://translate.yandex.net/api/v1/tr.json/translate?lang="+srcLg.getCode()+"-"+dstLg.getCode()+"&text="+byteString+"&srv=tr-text&reason=paste&options=4";
    }
    
    @Override
    protected HttpURLConnection getConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Referer", "https://translate.yandex.com/");
        conn.setRequestProperty("Host", "translate.yandex.net");
        conn.setRequestProperty("User-Agent","Opera/9.80 (Windows NT 6.1; Win64; x64; Edition Next) Presto/2.12.388 Version/12.15");
        conn.setRequestProperty("Accept","text/html, application/xml;q=0.9, application/xhtml+xml");
                
        return conn;
    }

    @Override
    protected String parse(String serverAnswer) {
        try {
            JSONArray array = new JSONObject(serverAnswer).getJSONArray("text");
            if(array.length() != 1) {
                QspLogger.info("Yandex Translator send strange array: "+serverAnswer);
            }
            return array.getString(0);
        } catch (JSONException je){
            QspLogger.info("Yandex parsing failed: "+je.getMessage());
            return null;
        }
    }    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        YandexTranslation trans = new YandexTranslation();
        System.out.println("trans test: " +trans.translate("Что-бы не слушать Танины жалобы вы начали помогать парням выгружать вещи", ELanguage.RUSSIAN, ELanguage.ENGLISH, null));
    }
}
