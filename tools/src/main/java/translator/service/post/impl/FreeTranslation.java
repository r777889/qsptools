package translator.service.post.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import hmi.log.QspLogger;
import translator.service.ServiceUtil;
import translator.service.post.AbstractPostTranslator;

/**
 *
 * @author pseudo555
 */
public class FreeTranslation extends AbstractPostTranslator {
    
    @Override
    protected String getUrl(String appId){
        return "http://www.freetranslation.com/gw-mt-proxy-service-web/mt-translation";
    }
    
    @Override
    protected HttpURLConnection getConnection(String appId) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(getUrl(appId)).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("referer", "http://www.freetranslation.com/");
        conn.setRequestProperty("User-Agent","Opera/9.80 (Windows NT 6.1; Win64; x64; Edition Next) Presto/2.12.388 Version/12.15");
        conn.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        conn.setRequestProperty("Tracking","applicationKey=dlWbNAC2iLJWujbcIHiNMQ%3D%3D applicationInstance=freetranslation");
        conn.setRequestProperty("X-Requested-With","XMLHttpRequest");
        
        return conn;
    }
    
    @Override
    protected void setPostContent(HttpURLConnection conn, String toTranslate) throws IOException{
        String input = "{\"text\":\""+ServiceUtil.toByteString(toTranslate)+"\",\"from\":\"rus\",\"to\":\"eng\"}";
        OutputStream os = conn.getOutputStream();
        os.write(input.getBytes());
        os.flush();
    }

    @Override
    protected String parse(String serverAnswer) {
        try {
            JSONObject jo = new JSONObject(serverAnswer);
            return jo.getString("translation");
        } catch (JSONException je){
            QspLogger.info("FreeTranslator parsing failed: "+je.getMessage());
            return null;
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FreeTranslation trans = new FreeTranslation();
        System.out.println("trans test: " +trans.translate("Что-бы не слушать Танины жалобы вы начали помогать парням выгружать вещи", null, null, null));
    }
}
