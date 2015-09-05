package translator.service;

import java.nio.charset.Charset;

/**
 *
 * @author pseudo555
 */
public final class ServiceUtil {
	
	private static final Charset UTF8 = Charset.forName("UTF-8");
    
    private ServiceUtil() {}
    
    /**
     * Convert given String to hexa char for web url
     * @param toTranslate String to convert
     * @return converted String
     */
    public static String toByteString(String toTranslate) {
        StringBuilder sb = new StringBuilder();
        for(byte b : toTranslate.getBytes(UTF8)){
            sb.append("%").append(String.format("%02X", b));
        }
        return sb.toString();
    }
    
    public static void main(String[] args){
        System.err.println( toByteString("ворчала"));
    }
}
