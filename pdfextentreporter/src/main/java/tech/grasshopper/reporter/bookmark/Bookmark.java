package tech.grasshopper.reporter.bookmark;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.aventstack.extentreports.model.Report;

import lombok.Builder;
import lombok.Data;
import tech.grasshopper.reporter.config.ExtentPDFReporterConfig;
import tech.grasshopper.reporter.context.AttributeType;
import tech.grasshopper.reporter.dashboard.AnalysisStrategyDisplay;
import tech.grasshopper.reporter.destination.Destination;
import tech.grasshopper.reporter.destination.Destination.DestinationStore;

@Data
@Builder
public class Bookmark {

	private final static String SUMMARY_BOOKMARK_TEXT = "SUMMARY";
	public final static String DASHBOARD_BOOKMARK_TEXT = "DASHBOARD";
	private final static String ATTRIBUTES_BOOKMARK_TEXT = "ATTRIBUTES";
	private final static String NON_BDD_BOOKMARK_TEXT = "TESTS";
	private final static String BDD_BOOKMARK_TEXT = "FEATURES";
	private final static String MEDIAS_BOOKMARK_TEXT = "MEDIAS";

	private final PDDocumentOutline outline = new PDDocumentOutline();

	private DestinationStore destinationStore;

	private ExtentPDFReporterConfig config;

	private Report report;

	public PDDocumentOutline createDocumentOutline() {

		PDOutlineItem summaryOutline = createSummaryOutline();
		createDashboardOutline(summaryOutline);

		if (config.isDisplayAttributeSummary())
			createAttributeSummaryOutline(summaryOutline);

		if (config.isDisplayTestDetails())
			createTestsOutline();

		if (config.isDisplayAttributeDetails())
			createAttributeDetailsOutline();

		if (config.isDisplayExpandedMedia())
			createExpandedMediaOutline();

		return outline;
	}

	private PDOutlineItem createSummaryOutline() {
		PDOutlineItem summaryOutline = createOutlineItem(destinationStore.getDashboardDestination(),
				SUMMARY_BOOKMARK_TEXT);
		summaryOutline.setBold(true);
		outline.addLast(summaryOutline);
		return summaryOutline;
	}

	private void createDashboardOutline(PDOutlineItem summaryOutline) {
		PDOutlineItem dashboardOutline = createOutlineItem(destinationStore.getDashboardDestination(),
				destinationStore.getDashboardDestination().getName());
		summaryOutline.addLast(dashboardOutline);
		summaryOutline.openNode();
	}

	private void createAttributeSummaryOutline(PDOutlineItem summaryOutline) {
		if (!destinationStore.getAttributeSummaryDestinations().isEmpty()) {
			PDOutlineItem attributeOutline = createOutlineItem(
					destinationStore.getAttributeSummaryDestinations().get(0), ATTRIBUTES_BOOKMARK_TEXT);
			summaryOutline.addLast(attributeOutline);

			for (Destination destination : destinationStore.getAttributeSummaryDestinations()) {
				PDOutlineItem attTypeOutline = createOutlineItem(destination,
						AttributeType.valueOf(destination.getName()).toString());
				attributeOutline.addLast(attTypeOutline);
			}
			attributeOutline.openNode();
		}
	}

	private void createExpandedMediaOutline() {
		if (!destinationStore.getTestMediaDestinations().isEmpty())
			outline.addLast(
					createChapterOutlineItems(destinationStore.getTestMediaDestinations(), MEDIAS_BOOKMARK_TEXT));
	}

	private void createAttributeDetailsOutline() {
		if (!destinationStore.getAttributeDetailDestinations().isEmpty()) {

			PDOutlineItem attributeOutline = createOutlineItem(destinationStore.getAttributeDetailDestinations().get(0),
					ATTRIBUTES_BOOKMARK_TEXT);
			attributeOutline.setBold(true);
			outline.addLast(attributeOutline);

			Map<AttributeType, List<Destination>> typeDestinationMap = destinationStore.getAttributeDetailDestinations()
					.stream().collect(Collectors.groupingBy(d -> {
						String name = d.getName();
						return AttributeType.valueOf(name.substring(0, name.indexOf("- ")).toUpperCase());
					}, LinkedHashMap::new, Collectors.mapping(Function.identity(), Collectors.toList())));

			typeDestinationMap.forEach((type, destinations) -> {
				PDOutlineItem attTypeOutline = createOutlineItem(destinations.get(0), type.toString());
				attributeOutline.addLast(attTypeOutline);

				destinations.forEach(dest -> {
					String name = dest.getName();
					PDOutlineItem attOutline = createOutlineItem(dest, name.substring(name.indexOf("- ") + 2));
					attTypeOutline.addLast(attOutline);
				});
			});
		}
	}

	private void createTestsOutline() {
		String heading = NON_BDD_BOOKMARK_TEXT;

		if (AnalysisStrategyDisplay.displaySettings(report) == AnalysisStrategyDisplay.BDD)
			heading = BDD_BOOKMARK_TEXT;

		if (!destinationStore.getTestDestinations().isEmpty())
			outline.addLast(createChapterOutlineItems(destinationStore.getTopLevelTestDestinations(), heading));
	}

	private PDOutlineItem createOutlineItem(Destination destination, String title) {
		return createOutlineItem(destination.createPDPageDestination(), title);
	}

	private PDOutlineItem createOutlineItem(Destination destination) {
		return createOutlineItem(destination, destination.getName());
	}

	private PDOutlineItem createOutlineItem(PDDestination destination, String title) {
		PDOutlineItem bookmark = new PDOutlineItem();
		bookmark.setDestination(destination);
		bookmark.setTitle(title);
		return bookmark;
	}

	private PDOutlineItem createChapterOutlineItems(List<Destination> destinations, String title) {
		PDOutlineItem chapterBookmark = createOutlineItem(destinations.get(0), title);
		chapterBookmark.setBold(true);
		destinations.forEach(d -> {
			PDOutlineItem pagesBookmark = createOutlineItem(d);
			chapterBookmark.addLast(pagesBookmark);
		});
		return chapterBookmark;
	}
}
