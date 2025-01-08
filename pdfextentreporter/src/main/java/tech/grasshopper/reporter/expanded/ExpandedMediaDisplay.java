package tech.grasshopper.reporter.expanded;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.ImageCell;

import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.model.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.annotation.Annotation;
import tech.grasshopper.pdf.structure.cell.TextLinkCell;
import tech.grasshopper.reporter.annotation.AnnotationStore;
import tech.grasshopper.reporter.destination.Destination;
import tech.grasshopper.reporter.destination.DestinationAware;
import tech.grasshopper.reporter.optimizer.TextSanitizer;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.TableCreator;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ExpandedMediaDisplay extends Display implements DestinationAware {

	private static final int NAME_FONT_SIZE = 15;

	private static final float PADDING = 5f;
	private static final float WIDTH = 500f;
	private static final float NAME_HEIGHT = 25f;
	private static final float GAP_HEIGHT = 10f;

	private static final float BORDER_WIDTH = 0;
	private static final float MULTI_LINE_SPACING = 1f;

	private int destinationY;

	protected Test test;

	private TableBuilder tableBuilder;

	private AnnotationStore annotations;

	@Override
	public Destination createDestination() {
		TextSanitizer textSanitizer = TextSanitizer.builder().font(reportFont.getBoldItalicFont()).build();

		return Destination.builder().id(test.getId()).name(textSanitizer.sanitizeText(test.getName()))
				.yCoord(destinationY).page(page).build();
	}

	@Override
	public void display() {
		tableBuilder = Table.builder().addColumnsOfWidth(WIDTH).padding(PADDING).borderWidth(BORDER_WIDTH)
				.font(reportFont.getBoldItalicFont()).horizontalAlignment(HorizontalAlignment.LEFT)
				.verticalAlignment(VerticalAlignment.MIDDLE);

		Annotation annotation = Annotation.builder().id(test.getId()).build();
		annotations.addTestNameMediaAnnotation(annotation);

		TextSanitizer textSanitizer = TextSanitizer.builder().font(reportFont.getBoldItalicFont()).build();

		tableBuilder.addRow(Row.builder()
				.add(TextLinkCell.builder().annotation(annotation).showLine(false).minHeight(NAME_HEIGHT)
						.fontSize(NAME_FONT_SIZE).text(textSanitizer.sanitizeText(test.getName()))
						.lineSpacing(MULTI_LINE_SPACING).textColor(config.getTestNameColor()).build())
				.build());

		List<Media> medias = new ArrayList<>();
		if (!test.getMedia().isEmpty())
			medias.addAll(test.getMedia());

		for (Log log : test.getLogs()) {
			if (log.hasMedia())
				medias.add(log.getMedia());
		}

		for (Media media : medias) {
			ExpandedMedia expandedMedia = ExpandedMedia.builder().media(media).document(document).padding(PADDING)
					.locations(config.getMediaFolders()).build();

			ImageCell image = expandedMedia.createImageCell();
			boolean imageAbsent = expandedMedia.isImageNotAvailable();
			if (!imageAbsent)
				tableBuilder.addRow(Row.builder().add(image).borderWidth(1f).borderColor(Color.LIGHT_GRAY).build());
		}

		PDPage initialPage = document.getPage(document.getNumberOfPages() - 1);
		destinationY = (int) ylocation;

		TableCreator table = TableCreator.builder().tableBuilder(tableBuilder).document(document).repeatRows(0)
				.startX(xlocation).startY(ylocation).build();
		table.displayTable();

		ylocation = table.getFinalY() - GAP_HEIGHT;
		page = table.getTableStartPage();

		if (!initialPage.equals(page))
			destinationY = (int) Display.CONTENT_START_Y;
	}
}
