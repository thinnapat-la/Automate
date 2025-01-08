package tech.grasshopper.reporter.tests;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
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
import tech.grasshopper.pdf.structure.cell.TableWithinTableCell;
import tech.grasshopper.reporter.annotation.AnnotationStore;
import tech.grasshopper.reporter.optimizer.TextSanitizer;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.TableCreator;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TestBDDExecutableDisplay extends Display {

	private final PDFont CONTENT_FONT = reportFont.getItalicFont();

	private static final int CONTENT_FONT_SIZE = 12;
	private static final int LOGS_TABLE_CONTENT_FONT_SIZE = 10;
	private final PDFont LOGS_TABLE_CONTENT_FONT = reportFont.getRegularFont();

	private static final float PADDING = 5f;
	private static final float WIDTH = 500f;
	private static final float GAP_HEIGHT = 5f;
	private static final float CONTENT_HEIGHT = 20f;

	private static final float BORDER_WIDTH = 1f;

	protected Test test;

	private TableBuilder tableBuilder;

	private AnnotationStore annotations;

	protected final TextSanitizer textSanitizer = TextSanitizer.builder().font(CONTENT_FONT).build();

	@Override
	public void display() {

		xlocation += test.getLevel() * TestDetails.LEVEL_X_INDENT;

		createTableBuilder();
		createExecutableRow();
		drawTable();
	}

	private void createTableBuilder() {
		tableBuilder = Table.builder().addColumnsOfWidth(WIDTH - (test.getLevel() * TestDetails.LEVEL_X_INDENT))
				.padding(PADDING).borderWidth(BORDER_WIDTH).borderColor(Color.LIGHT_GRAY).font(CONTENT_FONT)
				.fontSize(CONTENT_FONT_SIZE).horizontalAlignment(HorizontalAlignment.LEFT)
				.verticalAlignment(VerticalAlignment.MIDDLE);
	}

	private void createExecutableRow() {
		test.getChildren().forEach(t -> {
			List<AbstractCell> nameAndLogDetails = new ArrayList<>();
			nameAndLogDetails.add(TextCell.builder().minHeight(CONTENT_HEIGHT)
					.text(textSanitizer.sanitizeText(t.getName())).textColor(config.statusColor(t.getStatus()))
					.font(LOGS_TABLE_CONTENT_FONT).fontSize(LOGS_TABLE_CONTENT_FONT_SIZE).build());

			LogDetailsCollector logDetailsCollector = LogDetailsCollector.builder().annotations(annotations)
					.config(config).document(document).reportFont(reportFont).test(t).bddReport(true)
					.width(WIDTH - (test.getLevel() * TestDetails.LEVEL_X_INDENT)).build();

			t.getLogs().forEach(l -> {
				List<AbstractCell> cellDetails = logDetailsCollector.createLogDetailCells(l);
				if (!cellDetails.isEmpty())
					nameAndLogDetails.addAll(cellDetails);
			});

			if (nameAndLogDetails.size() == 1)
				tableBuilder.addRow(Row.builder().add(nameAndLogDetails.get(0)).padding(PADDING).build());
			else if (nameAndLogDetails.size() > 1)
				tableBuilder.addRow(
						Row.builder().add(createMultipleDetailsLogCell(nameAndLogDetails)).padding(PADDING).build());
		});
	}

	private AbstractCell createMultipleDetailsLogCell(List<AbstractCell> allDetailCells) {
		TableBuilder multipleDetailsBuilder = Table.builder()
				.addColumnsOfWidth(WIDTH - (test.getLevel() * TestDetails.LEVEL_X_INDENT)).borderWidth(0f);

		allDetailCells.forEach(c -> {
			if (c != null)
				multipleDetailsBuilder.addRow(Row.builder().add(c).build());
		});

		return TableWithinTableCell.builder().table(multipleDetailsBuilder.build()).padding(0f).build();
	}

	private void drawTable() {
		TableCreator table = TableCreator.builder().tableBuilder(tableBuilder).document(document).startX(xlocation)
				.startY(ylocation).repeatRows(0).splitRow(true).build();
		table.displayTable();

		ylocation = table.getFinalY() - GAP_HEIGHT;
	}
}
