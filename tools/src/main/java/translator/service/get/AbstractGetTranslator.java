package translator.service.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import translator.enums.ELanguage;
import translator.service.ITranslator;
import translator.service.ServiceUtil;

/**
 *
 * @author pseudo555
 */
public abstract class AbstractGetTranslator implements ITranslator {

	@Override
	public String translate(String toTranslate, ELanguage src, ELanguage dst, String appId) {
		try {
			String url = getUrl(ServiceUtil.toByteString(toTranslate), src, dst, appId);

			HttpURLConnection conn = getConnection(new URL(url));
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.err.println("Failed : HTTP error code : " + conn.getResponseCode());
				// throw new RuntimeException("Failed : HTTP error code : "+
				// conn.getResponseCode());
				return null;
			}

			BufferedReader br = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
			StringBuilder answer = new StringBuilder();
			String output;
			// System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				// System.out.println(output);
				answer.append(output);
			}
			conn.disconnect();

			return parse(answer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected abstract String getUrl(String txt, ELanguage srcLg, ELanguage dstLg, String appId);

	protected abstract HttpURLConnection getConnection(URL url) throws IOException;

	protected abstract String parse(String serverAnswer);

}
