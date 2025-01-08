package tech.grasshopper.reporter.tests;

import java.awt.Color;
import java.util.List;

import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Row.RowBuilder;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.structure.cell.TableWithinTableCell;
import tech.grasshopper.pdf.structure.cell.TextLabelCell;
import tech.grasshopper.reporter.annotation.AnnotationStore;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.TableCreator;
import tech.grasshopper.reporter.util.DateUtil;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TestLogsDisplay extends Display implements TestIndent {

	protected static final float LOGS_HEADER_HEIGHT = 20f;

	protected static final int LOGS_HEADER_FONT_SIZE = 11;
	protected static final float PADDING = 5f;
	protected static final float LOGS_STATUS_WIDTH = 50f;
	private static final float LOGS_TIMESTAMP_WIDTH = 70f;
	private static final float LOGS_DETAILS_WIDTH = 380f;

	protected static final float BORDER_WIDTH = 1f;
	protected static final int LOGS_TABLE_CONTENT_FONT_SIZE = 10;

	protected static final float GAP_HEIGHT = 15f;

	protected Test test;

	protected TableBuilder tableBuilder;

	protected AnnotationStore annotations;

	@Override
	public void display() {

		if (test.hasLog()) {

			xlocation += calculateIndent(test.getLevel(), config.getTestMaxIndentLevel()) * TestDetails.LEVEL_X_INDENT;

			createTableBuilder();
			createHeaderRow();
			createLogRows();
			drawTable();
		}
	}

	private void createTableBuilder() {
		tableBuilder = Table.builder().addColumnsOfWidth(columnsWidth()).padding(PADDING).borderColor(Color.LIGHT_GRAY)
				.borderWidth(BORDER_WIDTH).horizontalAlignment(HorizontalAlignment.LEFT)
				.verticalAlignment(VerticalAlignment.TOP);
	}

	protected float[] columnsWidth() {
		return new float[] { LOGS_STATUS_WIDTH, LOGS_TIMESTAMP_WIDTH,
				LOGS_DETAILS_WIDTH - (test.getLevel() * TestDetails.LEVEL_X_INDENT) };
	}

	private void createHeaderRow() {
		RowBuilder rowBuilder = Row.builder().height(LOGS_HEADER_HEIGHT).font(reportFont.getItalicFont())
				.fontSize(LOGS_HEADER_FONT_SIZE);

		tableHeaders(rowBuilder);
		tableBuilder.addRow(rowBuilder.build());
	}

	protected void tableHeaders(RowBuilder rowBuilder) {
		rowBuilder.add(TextCell.builder().text("Status").build()).add(TextCell.builder().text("Timestamp").build())
				.add(TextCell.builder().text("Log Details").build());
	}

	private void createLogRows() {
		test.getLogs().forEach(l -> {
			AbstractCell detailsCell = createLogDisplayCell(l, test);

			RowBuilder rowBuilder = Row.builder().padding(PADDING).font(reportFont.getRegularFont())
					.fontSize(LOGS_TABLE_CONTENT_FONT_SIZE);
			logRow(rowBuilder, detailsCell, l);

			tableBuilder.addRow(rowBuilder.build());
		});
	}

	protected void logRow(RowBuilder rowBuilder, AbstractCell detailsCell, Log l) {
		rowBuilder
				.add(TextLabelCell.builder().text(l.getStatus().toString())
						.labelColor(config.statusColor(l.getStatus())).build())
				.add(TextCell.builder()
						.text(DateUtil.formatTimeAMPM(DateUtil.convertToLocalDateTimeFromDate(l.getTimestamp())))
						.textColor(config.getTestTimeStampColor()).build())
				.add(detailsCell);
	}

	protected AbstractCell createLogDisplayCell(Log log, Test test) {
		LogDetailsCollector logDetailsCollector = LogDetailsCollector.builder().annotations(annotations).config(config)
				.document(document).reportFont(reportFont).test(test)
				.width(LOGS_DETAILS_WIDTH - (test.getLevel() * TestDetails.LEVEL_X_INDENT)).build();

		List<AbstractCell> allDetailCells = logDetailsCollector.createLogDetailCells(log);

		if (allDetailCells.isEmpty())
			return TextCell.builder().text("").padding(PADDING).build();
		else if (allDetailCells.size() == 1)
			return allDetailCells.get(0);
		else
			return createMultipleDetailsLogCell(allDetailCells);
	}

	protected AbstractCell createMultipleDetailsLogCell(List<AbstractCell> allDetailCells) {
		TableBuilder multipleDetailsBuilder = Table.builder()
				.addColumnsOfWidth(LOGS_DETAILS_WIDTH - (test.getLevel() * TestDetails.LEVEL_X_INDENT)).borderWidth(0f);

		allDetailCells.forEach(c -> {
			if (c != null)
				multipleDetailsBuilder.addRow(Row.builder().add(c).padding(PADDING).build());
		});

		return TableWithinTableCell.builder().table(multipleDetailsBuilder.build()).padding(0f).build();
	}

	private void drawTable() {
		TableCreator table = TableCreator.builder().tableBuilder(tableBuilder).document(document).startX(xlocation)
				.startY(ylocation).repeatRows(1).splitRow(true).build();
		table.displayTable();

		ylocation = table.getFinalY() - GAP_HEIGHT;
	}
}
