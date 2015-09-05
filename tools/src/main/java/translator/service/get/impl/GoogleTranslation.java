package translator.service.get.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hmi.log.QspLogger;
import translator.enums.ELanguage;
import translator.service.get.AbstractGetTranslator;

/**
 *
 * @author pseudo555
 */
public class GoogleTranslation extends AbstractGetTranslator {

	Logger LOG = LogManager.getLogger(GoogleTranslation.class);

	@Override
	protected String getUrl(String byteString, ELanguage srcLg, ELanguage dstLg, String appId) {
		LOG.debug("Bytestring: " + byteString);
		return "https://translate.google.fr/translate_a/single?client=t&sl=" + srcLg.getCode() + "&tl="
				+ dstLg.getCode() + "&dt=t&ie=UTF-8&oe=UTF-8&trs=1&inputm=1&source=btn&q=" + byteString;
	}

	@Override
	protected HttpURLConnection getConnection(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Referer", "https://translate.google.fr/");
		conn.setRequestProperty("Host", "translate.google.fr");
		conn.setRequestProperty("User-Agent",
				"Opera/9.80 (Windows NT 6.1; Win64; x64; Edition Next) Presto/2.12.388 Version/12.15");
		conn.setRequestProperty("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml");

		return conn;
	}

	@Override
	protected String parse(String serverAnswer) {
		LOG.debug(serverAnswer);
		String tmp = serverAnswer.substring(4);
		int idx = tmp.indexOf("\",\"");
		if (idx == -1) {
			QspLogger.info("Google translate send strange answer " + serverAnswer);
			return "";
		}
		return tmp.substring(0, idx);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		GoogleTranslation trans = new GoogleTranslation();
		FileInputStream fis = new FileInputStream("filetest.txt");
		byte[] buf = new byte[1024];
		int size = fis.read(buf);
		String test2 = new String(buf, 0, size, Charset.forName("UTF-8"));
		// String test2 = new String("Арендовать квартиру в старом
		// городе".getBytes(Charset.forName("UTF-8")));
		PrintStream out = new PrintStream(new FileOutputStream("out.txt"), true, "UTF-8");
		out.println(test2);
		out.println("trans test: " + trans.translate(test2, ELanguage.RUSSIAN, ELanguage.ENGLISH, null));
	}
}
