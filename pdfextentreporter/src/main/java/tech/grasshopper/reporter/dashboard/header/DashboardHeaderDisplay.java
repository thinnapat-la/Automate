package tech.grasshopper.reporter.dashboard.header;

import java.time.LocalDateTime;

import org.apache.pdfbox.pdmodel.font.PDFont;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.component.text.Text;
import tech.grasshopper.reporter.component.text.TextComponent;
import tech.grasshopper.reporter.optimizer.TextLengthOptimizer;
import tech.grasshopper.reporter.optimizer.TextSanitizer;
import tech.grasshopper.reporter.structure.Display;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DashboardHeaderDisplay extends Display {

	private static final int TITLE_FONT_SIZE = 22;
	private static final int TITLE_X_LOCATION = 50;

	private static final int DATE_FONT_SIZE = 16;
	private static final int DATE_X_LOCATION = 600;

	private static final int Y_LOCATION = 520;

	@Override
	public void display() {
		createReportTitleText();
		createReportDateText();
	}

	private void createReportTitleText() {
		PDFont titleFont = reportFont.getBoldFont();

		TextLengthOptimizer titleLengthOptimizer = TextLengthOptimizer.builder().font(titleFont)
				.fontsize(TITLE_FONT_SIZE).spaceWidth(DATE_X_LOCATION - TITLE_X_LOCATION - 20).build();

		TextSanitizer textSanitizer = TextSanitizer.builder().font(titleFont).build();

		Text text = Text.builder().textColor(config.getReportTitleColor()).font(titleFont).fontSize(TITLE_FONT_SIZE)
				.text(titleLengthOptimizer.optimizeText(textSanitizer.sanitizeText(config.getReportTitle())))
				.xlocation(TITLE_X_LOCATION).ylocation(Y_LOCATION).build();
		TextComponent.builder().content(content).text(text).build().display();
	}

	private void createReportDateText() {
		Text text = Text.builder().textColor(config.getReportDateColor()).font(reportFont.getRegularFont())
				.fontSize(DATE_FONT_SIZE).xlocation(DATE_X_LOCATION).ylocation(Y_LOCATION)
				.text(LocalDateTime.now().format(config.getReportDateFormat())).build();
		TextComponent.builder().content(content).text(text).build().display();
	}
}
