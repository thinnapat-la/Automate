package tech.grasshopper.reporter.tests.markup;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Element;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.structure.cell.TableWithinTableCell;
import tech.grasshopper.reporter.optimizer.TextSanitizer;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class UnorderedListMarkup extends MarkupDisplay {

	private static final Logger logger = Logger.getLogger(UnorderedListMarkup.class.getName());

	private float width;

	private static final float STAR_COLUMN_WIDTH = 15f;

	@Override
	public AbstractCell displayDetails() {

		return TableWithinTableCell.builder().table(listTable()).build();
	}

	private Table listTable() {

		TableBuilder tableBuilder = Table.builder().addColumnsOfWidth(STAR_COLUMN_WIDTH, width - STAR_COLUMN_WIDTH)
				.fontSize(LOG_FONT_SIZE).font(logFont).borderWidth(0).wordBreak(true);

		TextSanitizer textSanitizer = TextSanitizer.builder().font(logFont).build();
		for (Element elem : elements) {
			String text = "";
			// Catch all exceptions for safety. Needs to be refactored in future.
			try {
				text = elem.text();
			} catch (Exception e) {
				text = "Error in accessing line.";
				logger.log(Level.SEVERE, "Unable to get text for cell, default to error message.");
			}

			tableBuilder.addRow(Row.builder().add(TextCell.builder().text("*").build()).add(TextCell.builder()
					.text(textSanitizer.sanitizeText(text)).textColor(textColor).lineSpacing(MULTILINE_SPACING).build())
					.build());
		}
		return tableBuilder.build();
	}
}
