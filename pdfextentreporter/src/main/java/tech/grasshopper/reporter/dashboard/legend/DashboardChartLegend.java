package tech.grasshopper.reporter.dashboard.legend;

import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.aventstack.extentreports.Status;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Setter;
import tech.grasshopper.reporter.component.Component;
import tech.grasshopper.reporter.component.decorator.BackgroundDecorator;
import tech.grasshopper.reporter.component.text.Text;
import tech.grasshopper.reporter.component.text.TextComponent;

@Setter
@Builder
public class DashboardChartLegend {

	private float xlocation;
	private float ylocation;
	@Default
	private float legendGap = 25f;
	@Default
	private float legendWidth = 50f;
	@Default
	private float legendHeight = 20f;
	@Default
	private float legendXShift = 5f;
	@Default
	private float legendYShift = 6f;
	private Map<Status, Long> statusData;
	private Map<Status, Color> statusColor;
	@Default
	private float keyFontSize = 11f;
	private PDFont keyFont;
	@Default
	private float valueFontSize = 14f;
	private PDFont valueFont;
	private PDPageContentStream content;

	public void display() {
		for (Entry<Status, Color> entry : statusColor.entrySet()) {

			long value = statusData.getOrDefault(entry.getKey(), 0L);
			if (value > 0) {
				createLegendKey(entry.getKey().toString(), entry.getValue());
				createLegendValue(value);
				ylocation = ylocation - legendGap;
			}
		}
	}

	private void createLegendKey(String legendText, Color legendColor) {
		Text text = Text.builder().fontSize(keyFontSize).xlocation(xlocation).ylocation(ylocation).text(legendText)
				.font(keyFont).build();
		Component component = TextComponent.builder().content(content).text(text).build();
		component = BackgroundDecorator.builder().component(component).content(content).containerColor(legendColor)
				.xContainerBottomLeft(xlocation - legendXShift).yContainerBottomLeft(ylocation - legendYShift)
				.containerWidth(legendWidth).containerHeight(legendHeight).build();
		component.display();
	}

	private void createLegendValue(Long legendValue) {
		Text text = Text.builder().fontSize(valueFontSize).xlocation(xlocation + 65).ylocation(ylocation)
				.text(String.valueOf(legendValue)).font(valueFont).build();
		Component component = TextComponent.builder().content(content).text(text).build();
		component.display();
	}
}
