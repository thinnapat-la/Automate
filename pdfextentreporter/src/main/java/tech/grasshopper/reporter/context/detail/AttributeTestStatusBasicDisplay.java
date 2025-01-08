package tech.grasshopper.reporter.context.detail;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;
import org.vandeseer.easytable.structure.cell.paragraph.ParagraphCell;
import org.vandeseer.easytable.structure.cell.paragraph.ParagraphCell.Paragraph;
import org.vandeseer.easytable.structure.cell.paragraph.ParagraphCell.Paragraph.ParagraphBuilder;
import org.vandeseer.easytable.structure.cell.paragraph.StyledText;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.NamedAttribute;
import com.aventstack.extentreports.model.context.NamedAttributeContext;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.context.AttributeType;
import tech.grasshopper.reporter.destination.Destination;
import tech.grasshopper.reporter.destination.DestinationAware;
import tech.grasshopper.reporter.optimizer.TextSanitizer;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.TableCreator;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class AttributeTestStatusBasicDisplay extends Display implements DestinationAware {

	private static final int NAME_FONT_SIZE = 15;
	private static final int STATUS_FONT_SIZE = 12;

	private static final float NAME_STATUS_WIDTH = 500f;
	private static final float NAME_HEIGHT = 25f;
	private static final float STATUS_HEIGHT = 20f;
	private static final float GAP_HEIGHT = 5f;
	private static final float PADDING = 5f;
	protected static final float MULTILINE_SPACING = 1f;

	protected NamedAttributeContext<? extends NamedAttribute> attribute;

	protected AttributeType type;

	protected TableBuilder tableBuilder;

	private int destinationY;

	@Override
	public void display() {

		createDetailsTableBuilder();
		createNameRow();
		createStatusRow();
		drawDetailsTable();
	}

	private void createDetailsTableBuilder() {
		tableBuilder = Table.builder().addColumnsOfWidth(NAME_STATUS_WIDTH).padding(PADDING).borderWidth(0)
				.horizontalAlignment(HorizontalAlignment.LEFT).verticalAlignment(VerticalAlignment.MIDDLE);
	}

	private void createNameRow() {
		PDFont nameFont = reportFont.getBoldItalicFont();

		TextSanitizer textSanitizer = TextSanitizer.builder().font(nameFont).build();

		tableBuilder.addRow(Row.builder().add(TextCell.builder().minHeight(NAME_HEIGHT).fontSize(NAME_FONT_SIZE)
				.font(nameFont)
				.text(type.toString().toLowerCase() + "- " + textSanitizer.sanitizeText(attribute.getAttr().getName()))
				.wordBreak(true).lineSpacing(MULTILINE_SPACING).textColor(config.attributeHeaderColor(type)).build())
				.build());
	}

	private void createStatusRow() {
		ParagraphBuilder paraBuilder = Paragraph.builder();
		for (Status status : Status.values()) {
			if (attribute.getStatusDist().getOrDefault(status, 0) > 0) {
				paraBuilder.append(
						StyledText.builder().text("/ " + attribute.getStatusDist().get(status) + " " + status + " /")
								.color(config.statusColor(status)).build());
			}
		}

		tableBuilder.addRow(
				Row.builder().height(STATUS_HEIGHT).fontSize(STATUS_FONT_SIZE).font(reportFont.getBoldItalicFont())
						.add(ParagraphCell.builder().paragraph(paraBuilder.build()).build()).build());
	}

	protected void drawDetailsTable() {
		PDPage initialPage = document.getPage(document.getNumberOfPages() - 1);
		destinationY = (int) ylocation;

		TableCreator table = TableCreator.builder().tableBuilder(tableBuilder).document(document).startX(xlocation)
				.startY(ylocation).repeatRows(0).splitRow(true).build();
		table.displayTable();

		ylocation = table.getFinalY() - GAP_HEIGHT;
		page = table.getTableStartPage();

		if (!initialPage.equals(page))
			destinationY = (int) Display.CONTENT_START_Y;
	}

	@Override
	public Destination createDestination() {
		return Destination.builder().name(type.toString().toLowerCase() + "- " + attribute.getAttr().getName())
				.yCoord(destinationY).page(page).build();
	}
}
