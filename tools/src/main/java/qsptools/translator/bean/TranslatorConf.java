package qsptools.translator.bean;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import hmi.errorPopup.InnerErrorPopup;
import hmi.log.QspLogger;
import qsptools.translator.enums.ECharset;
import translator.enums.EEngine;
import translator.enums.ELanguage;

/**
 *
 * @author pseudo555
 */
public class TranslatorConf {
    
    public File qspFile;
    public EEngine engine;
    public ELanguage from;
    public ELanguage to;
    public Map<String,String> customDico;
    public ECharset encoding;
    public boolean isMulti;
    
    //allow translation of sentence instead of word.
    private static final int SPACE = ' ';
    private static final int END_PIC_DETECTION = '"';
    private static final int END_SOUND_DETECTION = '\'';
    private static final int END_VAR_IN_X='>';
    private static final int START_VAR_IN_X = '<';
    private static final String OPEN_BRAQUET = "[";
    private static final char DOLLAR_CHAR = '$';
    private static final String EOL = "\n";
    /* chars that can be count for russian one. */
    private static final String DONT_SPLIT_FOR_CHARS = " _";
    private static final String PIC_DETECTION = "img src=\"";
    private static final String SOUND_DETECTION = "play'";
	private static final String SOUND_DETECTION_2 = "close'";

	private boolean serviceFailed = false;
	private boolean transMissed = false;
	public boolean silentMode = false;
	private StringBuilder allNonTranslatedString = new StringBuilder();

	public TranslatorConf() {
	};

	public TranslatorConf(TranslatorConf conf, String s) {
		this.qspFile = new File(s);
		this.engine = conf.engine;
		this.from = conf.from;
		this.to = conf.to;
		this.customDico = conf.customDico;
		this.encoding = conf.encoding;
		this.isMulti = false;
		this.silentMode = true;
	}

	public boolean isTransIncomplete() {
		return serviceFailed || transMissed;
	}

	private String fixTranslationForQspImport(final String res) {
		if (res == null) {
			return null;
		}
		String tmp = res.replace('\'', '`');
		if (!tmp.trim().contains(" ")) {
			tmp = tmp.replace(" ", "_");
		}
		return tmp;
	}

	/**
	 * Return true only if the given text required a valid translation
	 * 
	 * @param ru
	 *            Original text to check
	 * @return boolean
	 */
	private boolean isValidTranslationRequired(String ru) {
		if (ru != null) {
			byte[] ruBytes = ru.getBytes();
			if (ruBytes.length == 3 && ruBytes[0] == (byte) -17 && ruBytes[1] == (byte) -69
					&& ruBytes[2] == (byte) -65) {
				// ru.bytecode = 0xff if ok, don't knwo why
				return false;
			} else if (ruBytes.length == 1 && ruBytes[0] == 63) {
				return false;
			} else if (ru.trim().isEmpty()) {
				// ru is only space chars -> no translation required
				return false;
			}
			return true;
		}
		// else ru is null, so I guess that's ok
		return false;
	}

	/**
	 * Russian to english sentence with ' replaced by `.
	 * 
	 * @param toTranslate
	 * @return en
	 */
	private String ru2en(final String toTranslate) {
		if (customDico.containsKey(toTranslate)) {
			return customDico.get(toTranslate);
		}

		// System.err.print("ask to translate >"+ru+"<");
		if (!serviceFailed) {
			try {
				String res = fixTranslationForQspImport(engine.getTranslator().translate(toTranslate, ELanguage.RUSSIAN,
						ELanguage.ENGLISH, engine.getAppId()));
				if (res == null || res.isEmpty()) {
					if (isValidTranslationRequired(toTranslate)) {

						QspLogger.error("Miss translation for >" + toTranslate + "< " + toTranslate.length() + " "
								+ String.format("%02X", (byte) toTranslate.charAt(0)));
						transMissed = true;
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								InnerErrorPopup innerPopup = new InnerErrorPopup(
										"Translation Service failed translation for:", "<<" + toTranslate + ">>", null);
								JOptionPane.showMessageDialog(null, innerPopup, "Error", JOptionPane.ERROR_MESSAGE);
							}
						});
						allNonTranslatedString.append(toTranslate).append(EOL);
					}
				} else {
					customDico.put(toTranslate, res);
				}
				// System.err.println(" answer >"+res+"<");
				return res;
			} catch (final IOException ioe) {
				QspLogger.error("Translation service error: " + ioe.getMessage());
				serviceFailed = true;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						InnerErrorPopup innerPopup = new InnerErrorPopup("Translation Service failed", ioe.getMessage(),
								"Translation will continue using only dictionnary.");
						JOptionPane.showMessageDialog(null, innerPopup, "Error", JOptionPane.ERROR_MESSAGE);
					}
				});
				// System.err.println(" failed");
			}
		}
		QspLogger.error(">" + toTranslate + "< was not translated");
		allNonTranslatedString.append(toTranslate).append(EOL);
		return toTranslate;
	}

	/**
	 * Ask only for russian part to be translated.
	 * 
	 * @param txtOri
	 *            Original russian line to translate
	 * @return Translated version of the given line
	 */
	public String translate(String txtOri) {
		boolean isEn = true;
		StringBuilder recomposed = new StringBuilder();
		StringBuilder toTranslate = new StringBuilder();
		boolean ispreviousCharSpace = false;
		boolean picPath = false;
		boolean varInPic = false;

		// System.err.println("\t txtOri: "+txtOri);

		for (int idx = 0; idx < txtOri.length(); idx++) {
			char c = txtOri.charAt(idx);
			char next = (idx < txtOri.length() - 1) ? txtOri.charAt(idx + 1) : (char) 0;
			// System.err.println("\t parse: '"+c+"' & '"+next+"' picpath:
			// "+picPath+ " varInpic: "+varInPic);

			int chPos = c;
			if (chPos > 1000 || (DONT_SPLIT_FOR_CHARS.contains("" + c) && !isEn) || picPath) {
				if (picPath) {
					// don't start the translation
					if (varInPic && !(chPos == START_VAR_IN_X || chPos == END_VAR_IN_X)) {
						// System.err.println("\t\t\t append varInPic
						// character");
						toTranslate.append(c);
					} else {
						if (toTranslate.length() > 0) {
							// System.err.println("\t\t\t\t start translation
							// picPath");
							String toBeTranslated = toTranslate.toString();
							String suffixToAdd = null;

							if (toBeTranslated.charAt(0) == DOLLAR_CHAR) {
								recomposed.append(DOLLAR_CHAR);
								toBeTranslated = toBeTranslated.substring(1);
							}
							int idxArray = toBeTranslated.indexOf(OPEN_BRAQUET);
							if (idxArray != -1) {
								suffixToAdd = toBeTranslated.substring(idxArray);
								toBeTranslated = toBeTranslated.substring(0, idxArray);
							}
							final String toBeAdded = translate(toBeTranslated);
							recomposed.append(toBeAdded);
							if (suffixToAdd != null) {
								recomposed.append(suffixToAdd);
							}
							if (ispreviousCharSpace) {
								recomposed.append(" ");
							}
							toTranslate = new StringBuilder();
						}
						// System.err.println("\t\t\t append char to recomposed
						// txt");
						recomposed.append(c);
					}

					if (chPos == END_PIC_DETECTION || chPos == END_SOUND_DETECTION) {
						// System.err.println("\t\t\t end of picpath");
						picPath = false;
					} else if (chPos == START_VAR_IN_X && next == START_VAR_IN_X) {
						// System.err.println("\t\t\t start var in pic");
						varInPic = true;
					} else if (chPos == END_VAR_IN_X && next == END_VAR_IN_X) {
						// System.err.println("\t\t\t end var in pic");
						varInPic = false;
					}
				} else {
					// System.err.println("\t\t not in picpath -> append to
					// tobetranslated str");
					isEn = false;
					toTranslate.append(c);
					ispreviousCharSpace = chPos == SPACE;
				}
			} else {
				// System.err.println("\t english part");
				isEn = true;
				if (toTranslate.length() > 0) {
					// System.err.println("\t\t ask for translation");
					final String toBeAdded = ru2en(toTranslate.toString());
					recomposed.append(toBeAdded);
					if (ispreviousCharSpace) {
						recomposed.append(" ");
					}
					toTranslate = new StringBuilder();
				}
				recomposed.append(c);
				if (recomposed.toString().endsWith(PIC_DETECTION) || recomposed.toString().endsWith(SOUND_DETECTION)
						|| recomposed.toString().endsWith(SOUND_DETECTION_2)
						|| recomposed.toString().endsWith(SOUND_DETECTION.toUpperCase())
						|| recomposed.toString().endsWith(SOUND_DETECTION_2.toUpperCase())) {
					picPath = true;
				}
			}
		}
		// System.err.println("\t ask for translation (jit)");
		if (toTranslate.length() > 0) {
			recomposed.append(ru2en(toTranslate.toString()));
		}

		return recomposed.toString();
	}

	public String getAllMissTrans() {
		String res = allNonTranslatedString.toString();
		allNonTranslatedString = new StringBuilder();
		return res;
	}
}
