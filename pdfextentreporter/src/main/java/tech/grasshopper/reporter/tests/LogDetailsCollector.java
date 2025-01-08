package tech.grasshopper.reporter.tests;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.ImageCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Test;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.annotation.Annotation;
import tech.grasshopper.pdf.annotation.FileAnnotation;
import tech.grasshopper.pdf.structure.cell.TableWithinTableCell;
import tech.grasshopper.pdf.structure.cell.TextFileLinkCell;
import tech.grasshopper.pdf.structure.cell.TextLinkCell;
import tech.grasshopper.reporter.annotation.AnnotationStore;
import tech.grasshopper.reporter.config.ExtentPDFReporterConfig;
import tech.grasshopper.reporter.font.ReportFont;
import tech.grasshopper.reporter.optimizer.TextSanitizer;
import tech.grasshopper.reporter.tests.markup.TestMarkup;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class LogDetailsCollector {

	private PDDocument document;

	protected ReportFont reportFont;

	protected Test test;

	protected ExtentPDFReporterConfig config;

	private AnnotationStore annotations;

	protected float width;

	@Default
	private boolean bddReport = false;

	protected static final float PADDING = 5f;
	private static final float LOGS_MEDIA_HEIGHT = 100f;
	private static final float LOGS_MEDIA_WIDTH = 100f;
	protected static final int LOGS_TABLE_CONTENT_FONT_SIZE = 10;
	private static final int LOGS_STACK_TRACE_TABLE_CONTENT_FONT_SIZE = 10;

	private static final float LOGS_DETAILS_HEIGHT = 15f;
	private static final float LOGS_MEDIA_PLUS_WIDTH = 15f;

	public List<AbstractCell> createLogDetailCells(Log log) {
		List<AbstractCell> allDetailCells = new ArrayList<>();

		if (!log.getDetails().isEmpty())
			allDetailCells.add(createDetailsMarkupCell(log));

		if (log.hasException())
			allDetailCells.add(createExceptionCell(log));

		if (log.hasMedia())
			allDetailCells.add(createMediaCell(log));

		return allDetailCells;
	}

	protected AbstractCell createDetailsMarkupCell(Log log) {
		PDFont LOGS_TABLE_CONTENT_FONT = reportFont.getRegularFont();

		TextSanitizer textSanitizer = TextSanitizer.builder().font(LOGS_TABLE_CONTENT_FONT).build();

		Status status = bddReport ? test.getStatus() : log.getStatus();
		AbstractCell detailMarkupCell = TextCell.builder().text(textSanitizer.sanitizeText(log.getDetails()))
				.font(LOGS_TABLE_CONTENT_FONT).fontSize(LOGS_TABLE_CONTENT_FONT_SIZE)
				.textColor(config.statusColor(status)).build();

		if (TestMarkup.isMarkup(log.getDetails()))
			detailMarkupCell = TestMarkup.builder().log(log).test(test).bddReport(bddReport).reportFont(reportFont)
					.width(width - (2 * PADDING)).config(config).build().createMarkupCell();
		return detailMarkupCell;
	}

	protected AbstractCell createMediaCell(Log log) {
		if (config.isDisplayExpandedMedia() || config.isDisplayAttachedMedia()) {
			TableBuilder tableBuilder = Table.builder()
					.addColumnsOfWidth(LOGS_MEDIA_PLUS_WIDTH, width - LOGS_MEDIA_PLUS_WIDTH).padding(0f);

			TestMedia testMedia = TestMedia.builder().media(log.getMedia()).document(document).padding(PADDING)
					.width(width - LOGS_MEDIA_PLUS_WIDTH).height(LOGS_MEDIA_HEIGHT).locations(config.getMediaFolders())
					.build();

			ImageCell image = testMedia.createImageCell();
			boolean imageAbsent = testMedia.isImageNotAvailable();

			if (imageAbsent) {
				tableBuilder.addRow(Row.builder().add(TextCell.builder().text(" ").build()).add(image).build());
			} else {
				if (config.isDisplayExpandedMedia()) {
					Annotation annotation = Annotation.builder().id(test.getId()).build();
					annotations.addTestMediaAnnotation(annotation);

					tableBuilder.addRow(Row.builder()
							.add(TextLinkCell.builder().text("+").annotation(annotation)
									.font(reportFont.getRegularFont()).fontSize(15).textColor(Color.RED).showLine(false)
									.verticalAlignment(VerticalAlignment.TOP)
									.horizontalAlignment(HorizontalAlignment.CENTER).build())
							.add(image).build());
				} else {
					List<FileAnnotation> fileAnnotations = new ArrayList<>();
					fileAnnotations
							.add(FileAnnotation.builder().text(" ").link(log.getMedia().getResolvedPath()).build());

					tableBuilder.addRow(
							Row.builder().add(TextFileLinkCell.builder().text(" ").annotations(fileAnnotations).build())
									.add(image).build());
					annotations.addTestMediaFileAnnotation(fileAnnotations.get(0));
				}
			}

			return TableWithinTableCell.builder().table(tableBuilder.build()).width(width - LOGS_MEDIA_PLUS_WIDTH)
					.build();
		} else
			return TestMedia.builder().media(log.getMedia()).document(document).width(LOGS_MEDIA_WIDTH)
					.height(LOGS_MEDIA_HEIGHT).padding(PADDING).build().createImageCell();
	}

	protected AbstractCell createExceptionCell(Log log) {
		return TestStackTrace.builder().log(log).font(reportFont.getRegularFont()).color(config.getTestExceptionColor())
				.width(width - (2 * PADDING)).height(LOGS_DETAILS_HEIGHT)
				.fontSize(LOGS_STACK_TRACE_TABLE_CONTENT_FONT_SIZE).padding(PADDING).build().createStackTraceCell();
	}
}
