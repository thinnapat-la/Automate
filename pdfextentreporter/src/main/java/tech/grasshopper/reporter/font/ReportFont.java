package tech.grasshopper.reporter.font;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import lombok.Getter;
import tech.grasshopper.reporter.exception.PdfReportException;

@Getter
public class ReportFont {

	private static final Logger logger = Logger.getLogger(ReportFont.class.getName());

	private PDFont regularFont;
	private PDFont boldFont;
	private PDFont italicFont;
	private PDFont boldItalicFont;
	private static final String FONT_FOLDER = "/tech/grasshopper/ttf/";

	public ReportFont(PDDocument document) {
		loadReportFontFamily(document);
	}

	private void loadReportFontFamily(PDDocument document) {
		try {
			regularFont = PDType0Font.load(document,
					ReportFont.class.getResourceAsStream(FONT_FOLDER + "Kanit-Regular.ttf"));

			boldFont = PDType0Font.load(document,
					ReportFont.class.getResourceAsStream(FONT_FOLDER + "Kanit-Regular.ttf"));

			italicFont = PDType0Font.load(document,
					ReportFont.class.getResourceAsStream(FONT_FOLDER + "Kanit-Regular.ttf"));

			boldItalicFont = PDType0Font.load(document,
					ReportFont.class.getResourceAsStream(FONT_FOLDER + "Kanit-Regular.ttf"));
		} catch (IOException e) {
			logger.log(Level.WARNING,
					"Unable to load report font - Kanit. The 'ttf' files should be available in '/tech/grasshopper/ttf' folder.",
					e);
			throw new PdfReportException(e);
		}
	}
//	private void loadReportFontFamily(PDDocument document) {
//		try {
//			regularFont = PDType0Font.load(document,
//					ReportFont.class.getResourceAsStream(FONT_FOLDER + "LiberationSans-Regular.ttf"));
//
//			boldFont = PDType0Font.load(document,
//					ReportFont.class.getResourceAsStream(FONT_FOLDER + "LiberationSans-Bold.ttf"));
//
//			italicFont = PDType0Font.load(document,
//					ReportFont.class.getResourceAsStream(FONT_FOLDER + "LiberationSans-Italic.ttf"));
//
//			boldItalicFont = PDType0Font.load(document,
//					ReportFont.class.getResourceAsStream(FONT_FOLDER + "LiberationSans-BoldItalic.ttf"));
//		} catch (IOException e) {
//			logger.log(Level.WARNING,
//					"Unable to load report font - LiberationSans. The 'ttf' files should be available in '/tech/grasshopper/ttf' folder.",
//					e);
//			throw new PdfReportException(e);
//		}
//	}

	@Override
	public String toString() {
		return regularFont.getName() + " - " + boldFont.getName() + " - " + italicFont.getName() + " - "
				+ boldItalicFont.getName();
	}
}
