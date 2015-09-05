package translator.service.post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import translator.enums.ELanguage;
import translator.service.ITranslator;

/**
 *
 * @author pseudo555
 */
public abstract class AbstractPostTranslator implements ITranslator{
    
    @Override
    public String translate(String toTranslate, ELanguage src, ELanguage dst, String appId) {   
        try {
            HttpURLConnection conn = getConnection(appId);
            setPostContent(conn, toTranslate);
            
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("Failed : HTTP error code : "+ conn.getResponseCode());
                //throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
                return null;
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder answer = new StringBuilder();
            String output;
//            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
//                System.out.println(output);
                answer.append(output);
            }
            conn.disconnect();
            
            return parse(answer.toString());
        } catch (Exception e) {            
            e.printStackTrace();
        }
        return null;
    }
    
    protected abstract String getUrl(String appId);
    
    protected abstract HttpURLConnection getConnection(String appId) throws IOException;
    
    protected abstract void setPostContent(HttpURLConnection conn, String toTranslate) throws IOException;

    protected abstract String parse(String serverAnswer);
    
}
