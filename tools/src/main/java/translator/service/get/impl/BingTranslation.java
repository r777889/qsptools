package translator.service.get.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import hmi.log.QspLogger;
import translator.enums.ELanguage;
import translator.service.get.AbstractGetTranslator;

/**
 *
 * @author pseudo555
 */
public class BingTranslation extends AbstractGetTranslator {

	private static final String API_KEY = "TVQwvqNY85U-bkaodMKUOu2G12CcACH4FDo0rjt9pYOww1U-uBX6fjwZyJzSwI_0l";
	// "TxwBp_cvcWNlZGSOshsw-1nzgXVopC42reN2dRaCnK4TGCGvd73as59nl8yvOJXpk"
	// "TBXKzv8Zeodg1q-d9Ji5AuWpuhvTfHh0b8PEaY1Nz4iTcpxcrWXtqs5N2y_UwmA_Y"

	@Override
	protected String getUrl(String byteString, ELanguage srcLg, ELanguage dstLg, String appId) {
		return "https://api.microsofttranslator.com/v2/ajax.svc/TranslateArray2?appId=%22" + API_KEY
				+ "%22&texts=%5B%22" + byteString + "%22%5D&from=%22" + srcLg.getCode() + "%22&to=%22" + dstLg.getCode()
				+ "%22";
	}

	@Override
	protected HttpURLConnection getConnection(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Referer", "https://www.bing.com/translator");
		conn.setRequestProperty("User-Agent",
				"Opera/9.80 (Windows NT 6.1; Win64; x64; Edition Next) Presto/2.12.388 Version/12.15");
		conn.setRequestProperty("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml");

		return conn;
	}

	@Override
	protected String parse(String serverAnswer) {
		try {
			String tmp = serverAnswer.substring(2, serverAnswer.length() - 1);
			JSONObject jo = new JSONObject(tmp);
			return jo.getString("TranslatedText");
		} catch (JSONException je) {
			QspLogger.info("Bing parsing failed: " + je.getMessage() + " for input <<" + serverAnswer + ">>");
			return null;
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		String appId = "";
		BingTranslation trans = new BingTranslation();
		System.out.println("trans test: "
				+ trans.translate("Что-бы не слушать Танины жалобы вы начали помогать парням выгружать вещи",
						ELanguage.RUSSIAN, ELanguage.ENGLISH, appId));
	}
}
