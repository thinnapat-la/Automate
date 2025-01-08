package tech.grasshopper.reporter.header;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;

import tech.grasshopper.reporter.component.text.Text;
import tech.grasshopper.reporter.component.text.TextComponent;
import tech.grasshopper.reporter.font.ReportFont;

public class PageHeader {

	private static final Logger logger = Logger.getLogger(PageHeader.class.getName());

	public static final String ATTRIBUTE_SUMMARY_SECTION = "ATTRIBUTE SUMMARY";
	public static final String TEST_DETAILS_SECTION = "TEST DETAILS";
	public static final String ATTRIBUTE_DETAILS_SECTION = "ATTRIBUTE DETAILS";
	public static final String EXPANDED_MEDIA_SECTION = "EXPANDED MEDIA";

	private final Map<String, Integer> sectionPageNumberMap = new LinkedHashMap<>();

	public void addSectionPageData(String pageTitle, int pageNo) {
		sectionPageNumberMap.put(pageTitle, pageNo);
	}

	public void processHeader(PDDocument document, ReportFont reportFont) {

		if (sectionPageNumberMap.isEmpty())
			return;

		String sectionName = "";
		int startPageNum = -1;

		for (Entry<String, Integer> entry : sectionPageNumberMap.entrySet()) {

			if (startPageNum == -1) {
				sectionName = entry.getKey();
				startPageNum = entry.getValue();
			} else {
				createTitleAndNumber(document, reportFont, startPageNum, entry.getValue(), sectionName);
				sectionName = entry.getKey();
				startPageNum = entry.getValue();
			}
		}
		createTitleAndNumber(document, reportFont, startPageNum, document.getNumberOfPages(), sectionName);
	}

	private void createTitleAndNumber(PDDocument document, ReportFont reportFont, int startPageNum, int endPageNum,
			String sectionName) {
		for (int i = startPageNum; i < endPageNum; i++) {
			PDPage page = document.getPage(i);

			try (final PDPageContentStream content = new PDPageContentStream(document, page, AppendMode.APPEND, true)) {
				Text pageTitle = Text.builder().textColor(Color.LIGHT_GRAY).xlocation(60)
						.font(reportFont.getItalicFont()).ylocation(page.getMediaBox().getHeight() - 40)
						.text(sectionName).build();
				TextComponent.builder().text(pageTitle).content(content).build().display();

				Text pageNumber = Text.builder().textColor(Color.LIGHT_GRAY)
						.xlocation(page.getMediaBox().getWidth() - 90).font(reportFont.getItalicFont())
						.ylocation(page.getMediaBox().getHeight() - 40).text("-- " + (i + 1) + " --").build();
				TextComponent.builder().text(pageNumber).content(content).build().display();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Unable to add page title and\\or number", e);
			}
		}
	}
}
