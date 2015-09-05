package qsptools.translator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import hmi.log.QspLogger;
import qsptools.QspToolsUtil;
import qsptools.translator.bean.DicoEntryWrapper;
import qsptools.translator.model.DicoTableModel;

/**
 *
 * @author pseudo555
 */
public final class TranslatorUtils {

	private TranslatorUtils() {
	}

	/*
	 * Dico management -------------------------------------------------------
	 */

	/**
	 * Use content of given file to fill the given DicoTableModel.
	 * 
	 * @param model
	 *            DicoTableModel to fill
	 * @param file
	 *            content to be used
	 * @return success state of the operation
	 */
	public static boolean fillDico(DicoTableModel model, File file) {
		if (!file.exists()) {
			QspLogger.error("import failed: given file doesn't exist");
			return false;
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(DicoEntryWrapper.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			try (Reader r = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"))) {
				DicoEntryWrapper dicoWrapped = (DicoEntryWrapper) jaxbUnmarshaller.unmarshal(file);
				model.clear(false);
				model.addItems(dicoWrapped.getDico());
				return true;
			} catch (IOException e) {
				QspLogger.error("import failed: " + e.getMessage());
			}
		} catch (JAXBException e) {
			QspLogger.error("import failed: " + e.getMessage());
		}
		return false;

	}

	/**
	 * Save the given DicoTableModel in given file.
	 * 
	 * @param dico
	 *            map(string,string) to persist
	 * @param file
	 *            which will content the persisted content
	 * @return success state of the operation
	 */
	public static boolean persistDico(Map<String, String> dico, File file) {
		try {
			final String xml = getXmlFromData(dico);
			if (xml != null && !xml.isEmpty()) {
				Files.write(Paths.get(file.getAbsolutePath()), xml.getBytes(Charset.forName("UTF-8")));
				return true;
			}
		} catch (IOException e) {
			QspLogger.error("Save failed: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Convert given dico to xml string.
	 * 
	 * @param dico
	 *            Dictionary to be converted
	 * @return xml string
	 */
	private static String getXmlFromData(Map<String, String> dico) {
		final DicoEntryWrapper object2convert = new DicoEntryWrapper(dico);
		try (final StringWriter writer = new StringWriter()) {
			final JAXBContext context = JAXBContext.newInstance(object2convert.getClass());
			final Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(object2convert, writer);
			return writer.toString();
		} catch (final Exception e) {
			QspLogger.error("persistDico failed: " + e.getMessage());
			return null;
		}
	}

	/*
	 * File chooser for txt --------------------------------------------------
	 */

	private static JFileChooser txtFC = null;
	private static final String EXT_INPUT = "txt";
	private static final String EXT_INPUT_DESC = "TXT files";

	/**
	 * @return JFileChooser to use to save translated file or to load file to be
	 *         translated
	 */
	public static JFileChooser getTxtFileChooser() {
		if (txtFC == null) {
			txtFC = new JFileChooser();
			txtFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			txtFC.setAcceptAllFileFilterUsed(false);
			txtFC.addChoosableFileFilter(new FileFilter() {

				@Override
				public boolean accept(File file) {
					return (file.isDirectory() || EXT_INPUT.equalsIgnoreCase(QspToolsUtil.getExt(file)));
				}

				@Override
				public String getDescription() {
					return EXT_INPUT_DESC;
				}
			});
		}
		return txtFC;
	}

	/*
	 * File chooser for renpy --------------------------------------------------
	 */

	private static JFileChooser folderFC = null;

	/**
	 * @return JFileChooser to use to save translated file or to load file to be
	 *         translated
	 */
	public static JFileChooser getRenpyFileChooser() {
		if (folderFC == null) {
			folderFC = new JFileChooser();
			folderFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			folderFC.setAcceptAllFileFilterUsed(false);
		}
		return folderFC;
	}

	/*
	 * File chooser for dico -------------------------------------------------
	 */

	private static JFileChooser dicoFC = null;
	private static final String EXT_DICO = "xml";
	private static final String EXT_DICO_DESC = "XML files";

	public static JFileChooser getDicoFileChooser() {
		if (dicoFC == null) {
			dicoFC = new JFileChooser();
			dicoFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			dicoFC.setAcceptAllFileFilterUsed(false);
			dicoFC.addChoosableFileFilter(new FileFilter() {

				@Override
				public boolean accept(File file) {
					return (file.isDirectory() || EXT_DICO.equalsIgnoreCase(QspToolsUtil.getExt(file)));
				}

				@Override
				public String getDescription() {
					return EXT_DICO_DESC;
				}
			});
		}
		return dicoFC;
	}

	/**
	 * Return a list of all files included in given directory.
	 * 
	 * @param directory
	 *            Directory to parse
	 * @return list of files' path
	 */
	public static List<String> fileList(Path directory, final String ext) {
		List<String> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			for (Path path : directoryStream) {
				File f = path.toFile();
				if (f.isDirectory()) {
					fileNames.addAll(fileList(f.toPath(), ext));
				} else if (path.toString().endsWith(ext)) {
					fileNames.add(path.toString());
				}
			}
		} catch (IOException ex) {
		}
		return fileNames;
	}
}
