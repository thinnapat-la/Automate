package tech.grasshopper.reporter.component.chart;

import java.awt.Color;
import java.util.Map;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.PieStyler.ClockwiseDirectionType;
import org.knowm.xchart.style.Styler.ChartTheme;

import com.aventstack.extentreports.Status;

public class ReportDonutChart extends PieChart {

	private Color[] sliceColors = new Color[] { Color.GREEN, Color.RED, Color.ORANGE, Color.YELLOW, Color.BLUE };

	public ReportDonutChart(int width, int height) {
		super(width, height, ChartTheme.XChart);
		updateStyler();
	}

	public void updateStyler() {
		PieStyler styler = getStyler();

		styler.setLegendVisible(false);
		styler.setPlotContentSize(0.85);
		styler.setPlotBorderVisible(true);
		styler.setPlotBorderColor(Color.BLACK);
		styler.setChartPadding(1);
		styler.setClockwiseDirectionType(ClockwiseDirectionType.CLOCKWISE);
		styler.setSeriesColors(sliceColors);
		styler.setHasAnnotations(false);
		styler.setDefaultSeriesRenderStyle(PieSeriesRenderStyle.Donut);
		styler.setDonutThickness(0.4);
		styler.setSumVisible(true);
		styler.setSumFontSize(20);
		styler.setDecimalPattern("#");
		styler.setChartBackgroundColor(Color.WHITE);
	}

	public void updateData(Map<String, Long> data) {

		addSeries(Status.PASS.toString(), data.getOrDefault(Status.PASS.toString(), 0L));
		addSeries(Status.FAIL.toString(), data.getOrDefault(Status.FAIL.toString(), 0L));
		addSeries(Status.WARNING.toString(), data.getOrDefault(Status.WARNING.toString(), 0L));
		addSeries(Status.SKIP.toString(), data.getOrDefault(Status.SKIP.toString(), 0L));
		addSeries(Status.INFO.toString(), data.getOrDefault(Status.INFO.toString(), 0L));
	}
}
