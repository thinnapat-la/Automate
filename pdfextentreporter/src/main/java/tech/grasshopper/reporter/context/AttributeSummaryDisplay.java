package tech.grasshopper.reporter.context;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.structure.Table.TableBuilder;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.NameValuePair;
import com.aventstack.extentreports.model.NamedAttribute;
import com.aventstack.extentreports.model.context.NamedAttributeContext;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.destination.Destination;
import tech.grasshopper.reporter.destination.DestinationAware;
import tech.grasshopper.reporter.exception.PdfReportException;
import tech.grasshopper.reporter.optimizer.TextSanitizer;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.TableCreator;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public abstract class AttributeSummaryDisplay extends Display implements DestinationAware {

	protected static final float NAME_HEIGHT = 25;
	protected static final float HEADER_HEIGHT = 25;
	protected static final float ROW_HEIGHT = 25;
	protected static final float GAP_HEIGHT = 15;

	protected static final int NAME_FONT_SIZE = 15;
	protected static final int HEADER_FONT_SIZE = 13;

	protected static final float BORDER_WIDTH = 1f;
	protected static final float HEADER_PADDING = 5f;
	protected static final float TABLE_PADDING = 7f;
	protected static final int TABLE_CONTENT_FONT_SIZE = 12;
	protected PDFont TABLE_CONTENT_FONT = reportFont.getRegularFont();
	protected static final float MULTILINE_SPACING = 1f;

	protected AttributeType type;

	protected TableBuilder tableBuilder;

	private int destinationY;

	protected boolean splitRow;

	protected Map<String, Map<Status, Integer>> contextAttributeData() {

		// Move to separate subclasses!!
		switch (type) {
		case AUTHOR:
			return convertContextRowData(report.getAuthorCtx().getSet());
		case CATEGORY:
			return convertContextRowData(report.getCategoryCtx().getSet());
		case DEVICE:
			return convertContextRowData(report.getDeviceCtx().getSet());
		default:
			break;
		}
		throw new PdfReportException("Unsupported context attribute type.");
	}

	protected final TextSanitizer textSanitizer = TextSanitizer.builder().font(TABLE_CONTENT_FONT).build();

	private Map<String, Map<Status, Integer>> convertContextRowData(
			Set<? extends NamedAttributeContext<? extends NamedAttribute>> attributes) {
		Map<String, Map<Status, Integer>> data = new LinkedHashMap<>();
		attributes.forEach(a -> data.put(a.getAttr().getName(), a.getStatusDist()));
		return data;
	}

	protected Map<String, String> systemAttributeData() {

		// Move to separate subclass!!
		switch (type) {
		case SYSTEM:
			return convertSystemRowData(report.getSystemEnvInfo());
		default:
			break;
		}
		throw new PdfReportException("Unsupported system attribute type.");
	}

	private Map<String, String> convertSystemRowData(List<? extends NameValuePair> attributes) {
		Map<String, String> data = new LinkedHashMap<>();
		attributes.forEach(s -> data.put(s.getName(), s.getValue()));
		return data;
	}

	protected void drawTable() {

		PDPage initialPage = document.getPage(document.getNumberOfPages() - 1);
		destinationY = (int) ylocation;

		TableCreator table = TableCreator.builder().tableBuilder(tableBuilder).document(document).startX(xlocation)
				.startY(ylocation).repeatRows(2).splitRow(true).build();
		table.displayTable();

		ylocation = table.getFinalY() - GAP_HEIGHT;
		page = table.getTableStartPage();

		if (!initialPage.equals(page))
			destinationY = (int) Display.CONTENT_START_Y;
	}

	@Override
	public Destination createDestination() {
		return Destination.builder().name(type.toString()).yCoord(destinationY).page(page).build();
	}
}
