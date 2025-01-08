package tech.grasshopper.reporter.tests.markup;

import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import com.aventstack.extentreports.model.Log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.optimizer.TextSanitizer;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DefaultMarkup extends MarkupDisplay {

	private Log log;

	@Override
	public AbstractCell displayDetails() {
		TextSanitizer textSanitizer = TextSanitizer.builder().font(logFont).build();

		return TextCell.builder().text(textSanitizer.sanitizeText(log.getDetails())).textColor(textColor)
				.fontSize(LOG_FONT_SIZE).font(logFont).lineSpacing(MULTILINE_SPACING).build();
	}
}
