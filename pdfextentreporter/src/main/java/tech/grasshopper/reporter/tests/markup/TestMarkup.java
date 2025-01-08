package tech.grasshopper.reporter.tests.markup;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vandeseer.easytable.structure.cell.AbstractCell;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Test;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.config.ExtentPDFReporterConfig;
import tech.grasshopper.reporter.font.ReportFont;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TestMarkup {

	private static final Logger logger = Logger.getLogger(TestMarkup.class.getName());

	private Test test;

	private Log log;

	protected float width;

	protected ExtentPDFReporterConfig config;

	@NonNull
	protected ReportFont reportFont;

	@Default
	private boolean bddReport = false;

	public AbstractCell createMarkupCell() {
		String html = log.getDetails();
		Status status = bddReport ? test.getStatus() : log.getStatus();

		Document document = null;
		try {
			document = Jsoup.parseBodyFragment(html);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error is parsing markup html, diplaying raw log details");
			return displayDefault(status);
		}

		Element element = document.selectFirst("body > span[class*=\"badge\"]");
		if (element != null)
			return createLabelMarkup(element);

		element = document.selectFirst("body > table[class*=\"markup-table table\"]");
		if (element != null)
			return createTableMarkup(status, element);

		Elements elements = document.select("body > ol > li");
		if (elements.size() > 0)
			return createOrderedListMarkup(status, elements);

		elements = document.select("body > ul > li");
		if (elements.size() > 0)
			return createUnorderedListMarkup(status, elements);

		elements = document.select("body textarea[class*=\"code-block\"]");
		if (elements.size() > 0)
			return createCodeBlockMarkup(status, elements);

		if (html.contains("JSONTree"))
			return createJsonMarkup(html, status);

		return displayDefault(status);
	}

	protected AbstractCell createJsonMarkup(String html, Status status) {
		return JsonMarkup.builder().html(html).logFont(reportFont.getRegularFont())
				.textColor(config.statusColor(status)).build().displayDetails();
	}

	protected AbstractCell createCodeBlockMarkup(Status status, Elements elements) {
		return CodeBlockMarkup.builder().elements(elements).logFont(reportFont.getRegularFont())
				.textColor(config.statusColor(status)).width(width).build().displayDetails();
	}

	protected AbstractCell createUnorderedListMarkup(Status status, Elements elements) {
		return UnorderedListMarkup.builder().elements(elements).logFont(reportFont.getRegularFont())
				.textColor(config.statusColor(status)).width(width).build().displayDetails();
	}

	protected AbstractCell createOrderedListMarkup(Status status, Elements elements) {
		return OrderedListMarkup.builder().elements(elements).logFont(reportFont.getRegularFont())
				.textColor(config.statusColor(status)).width(width).build().displayDetails();
	}

	protected AbstractCell createTableMarkup(Status status, Element element) {
		return TableMarkup.builder().element(element).logFont(reportFont.getRegularFont())
				.textColor(config.statusColor(status)).maxTableColumnCount(config.getMaxTableColumnCount()).width(width)
				.build().displayDetails();
	}

	protected AbstractCell createLabelMarkup(Element element) {
		return LabelMarkup.builder().element(element).logFont(reportFont.getRegularFont()).build().displayDetails();
	}

	private AbstractCell displayDefault(Status status) {
		return DefaultMarkup.builder().log(log).logFont(reportFont.getRegularFont())
				.textColor(config.statusColor(status)).build().displayDetails();
	}

	public static boolean isMarkup(String markup) {
		if (markup.trim().startsWith("<") && markup.trim().endsWith(">"))
			return true;
		return false;
	}

}
