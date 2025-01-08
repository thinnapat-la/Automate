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
public class OrderedListMarkup extends MarkupDisplay {

	private static final Logger logger = Logger.getLogger(OrderedListMarkup.class.getName());

	private float width;

	private static final float SNO_COLUMN_WIDTH = 25f;

	@Override
	public AbstractCell displayDetails() {

		return TableWithinTableCell.builder().table(listTable()).build();
	}

	private Table listTable() {

		TableBuilder tableBuilder = Table.builder().addColumnsOfWidth(SNO_COLUMN_WIDTH, width - SNO_COLUMN_WIDTH)
				.fontSize(LOG_FONT_SIZE).font(logFont).borderWidth(0).wordBreak(true);

		TextSanitizer textSanitizer = TextSanitizer.builder().font(logFont).build();
		int sno = 1;
		for (Element elem : elements) {
			// Catch all exceptions for safety. Needs to be refactored in future.
			String text = "";
			try {
				text = elem.text();
			} catch (Exception e) {
				text = "Error in accessing line.";
				logger.log(Level.SEVERE, "Unable to get text for cell, default to error message.");
			}

			tableBuilder.addRow(Row.builder().add(TextCell.builder().text(String.valueOf(sno)).build())
					.add(TextCell.builder().text(textSanitizer.sanitizeText(text)).textColor(textColor)
							.lineSpacing(MULTILINE_SPACING).build())
					.build());
			sno++;
		}
		return tableBuilder.build();
	}
}
