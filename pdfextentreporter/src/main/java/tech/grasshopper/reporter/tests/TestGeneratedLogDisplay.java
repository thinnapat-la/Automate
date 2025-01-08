package tech.grasshopper.reporter.tests;

import java.awt.Color;

import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import com.aventstack.extentreports.model.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.structure.cell.TextLabelCell;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.TableCreator;
import tech.grasshopper.reporter.tests.markup.TestMarkup;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TestGeneratedLogDisplay extends Display implements TestIndent {

	public static final float LOGS_HEADER_HEIGHT = 20f;
	public static final float LOGS_ROW_HEIGHT = 20f;

	private static final int LOGS_HEADER_FONT_SIZE = 11;
	private static final float PADDING = 5f;
	private static final float LOGS_STATUS_WIDTH = 50f;
	private static final float LOGS_DETAILS_WIDTH = 450f;

	private static final float BORDER_WIDTH = 1f;
	private static final float GAP_HEIGHT = 10f;
	private static final int LOGS_TABLE_CONTENT_FONT_SIZE = 10;

	protected Test test;

	private TableBuilder tableBuilder;

	@Override
	public void display() {
		if (!test.getGeneratedLog().isEmpty()) {

			xlocation += calculateIndent(test.getLevel(), config.getTestMaxIndentLevel()) * TestDetails.LEVEL_X_INDENT;

			createTableBuilder();
			createHeaderRow();
			createLogRows();
			drawTable();
		}
	}

	private void createTableBuilder() {
		tableBuilder = Table.builder()
				.addColumnsOfWidth(LOGS_STATUS_WIDTH,
						LOGS_DETAILS_WIDTH - (test.getLevel() * TestDetails.LEVEL_X_INDENT))
				.padding(PADDING).borderColor(Color.LIGHT_GRAY).borderWidth(BORDER_WIDTH)
				.horizontalAlignment(HorizontalAlignment.LEFT).verticalAlignment(VerticalAlignment.TOP);
	}

	private void createHeaderRow() {
		tableBuilder.addRow(Row.builder().height(LOGS_HEADER_HEIGHT).font(reportFont.getItalicFont())
				.fontSize(LOGS_HEADER_FONT_SIZE).add(TextCell.builder().text("Status").build())
				.add(TextCell.builder().text("Generated Log Details").build()).build());
	}

	private void createLogRows() {
		test.getGeneratedLog().forEach(l -> {
			AbstractCell detailCell = TestMarkup.builder().test(test).log(l)
					.width(LOGS_DETAILS_WIDTH - (2 * PADDING) - (test.getLevel() * TestDetails.LEVEL_X_INDENT))
					.config(config).reportFont(reportFont).build().createMarkupCell();

			Row row = Row.builder().font(reportFont.getRegularFont()).fontSize(LOGS_TABLE_CONTENT_FONT_SIZE)
					.wordBreak(true).padding(PADDING).add(TextLabelCell.builder().text(l.getStatus().toString())
							.labelColor(config.statusColor(l.getStatus())).build())
					.add(detailCell).build();
			tableBuilder.addRow(row);
		});
	}

	private void drawTable() {
		TableCreator table = TableCreator.builder().tableBuilder(tableBuilder).document(document).startX(xlocation)
				.startY(ylocation).repeatRows(1).splitRow(true).build();
		table.displayTable();

		ylocation = table.getFinalY() - GAP_HEIGHT;
	}
}
