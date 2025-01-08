package tech.grasshopper.reporter.dashboard.chart;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import lombok.Builder;
import lombok.Setter;
import tech.grasshopper.reporter.component.text.Text;
import tech.grasshopper.reporter.component.text.TextComponent;

@Setter
@Builder
public class DashboardChartTitle {

	private String title;
	private float xlocation;
	private float ylocation;
	private float fontSize;
	private PDFont font;
	private PDPageContentStream content;

	public void display() {
		Text text = Text.builder().xlocation(xlocation).ylocation(ylocation).text(title).font(font).fontSize(fontSize)
				.build();
		TextComponent.builder().content(content).text(text).build().display();
	}
}
