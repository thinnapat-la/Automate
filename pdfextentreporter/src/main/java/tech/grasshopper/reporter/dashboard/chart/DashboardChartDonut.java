package tech.grasshopper.reporter.dashboard.chart;

import java.awt.Color;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.PieStyler.ClockwiseDirectionType;
import org.knowm.xchart.style.Styler.ChartTheme;

import com.aventstack.extentreports.Status;

import lombok.Builder;
import lombok.Setter;
import tech.grasshopper.reporter.component.chart.ChartDisplayer;
import tech.grasshopper.reporter.config.ExtentPDFReporterConfig;

@Setter
@Builder
public class DashboardChartDonut {

	private int width;
	private int height;
	private float xlocation;
	private float ylocation;
	private Map<Status, Long> data;
	private ExtentPDFReporterConfig config;
	private PDDocument document;
	private PDPageContentStream content;

	public void display() {
		PieChart chart = new PieChart(width, height, ChartTheme.XChart);
		updateChartStyler(chart.getStyler());
		updateChartData(chart, data);

		ChartDisplayer.builder().document(document).content(content).chart(chart).xBottomLeft(xlocation)
				.yBottomLeft(ylocation).build().display();
	}

	private void updateChartStyler(PieStyler styler) {
		styler.setSeriesColors(new Color[] { config.getPassColor(), config.getFailColor(), config.getSkipColor(),
				config.getWarnColor(), config.getInfoColor() });
		styler.setLegendVisible(false);
		styler.setPlotContentSize(0.85);
		styler.setPlotBorderVisible(true);
		styler.setPlotBorderColor(Color.BLACK);
		styler.setChartPadding(1);
		styler.setClockwiseDirectionType(ClockwiseDirectionType.CLOCKWISE);
		styler.setHasAnnotations(false);
		styler.setDefaultSeriesRenderStyle(PieSeriesRenderStyle.Donut);
		styler.setDonutThickness(0.4);
		styler.setSumVisible(true);
		styler.setSumFontSize(20);
		styler.setDecimalPattern("#");
		styler.setChartBackgroundColor(Color.WHITE);
	}

	private void updateChartData(PieChart chart, Map<Status, Long> statusData) {
		chart.addSeries(Status.PASS.toString(), statusData.getOrDefault(Status.PASS, 0L));
		chart.addSeries(Status.FAIL.toString(), statusData.getOrDefault(Status.FAIL, 0L));
		chart.addSeries(Status.SKIP.toString(), statusData.getOrDefault(Status.SKIP, 0L));
		chart.addSeries(Status.WARNING.toString(), statusData.getOrDefault(Status.WARNING, 0L));
		chart.addSeries(Status.INFO.toString(), statusData.getOrDefault(Status.INFO, 0L));
	}
}
