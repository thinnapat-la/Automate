package tech.grasshopper.reporter.dashboard.chart;

import java.util.Map;

import org.knowm.xchart.PieChart;

import com.aventstack.extentreports.Status;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.dashboard.AnalysisStrategyDisplay;
import tech.grasshopper.reporter.structure.Display;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DashboardDonutChartDisplay extends Display {

	private PieChart chart;

	private static final int CHART_TITLE_X_PADDING = 10;
	private static final int CHART_TITLE_Y_LOCATION = 390;
	private static final int CHART_TITLE_FONT_SIZE = 16;

	private static final int CHART_DIMENSION = 170;
	private static final int CHART_Y_LOCATION = 210;

	private AnalysisStrategyDisplay strategyDisplay;

	@Override
	public void display() {

		strategyDisplay = AnalysisStrategyDisplay.displaySettings(report);

		createFirstChartTitle();
		createSecondChartTitle();
		createThirdChartTitle();
		createLogsChartTitle();

		createFirstDonutChart();
		createSecondDonutChart();
		createThirdDonutChart();
		createLogsDonutChart();
	}

	private void createTitle(String title, float xLocation) {
		DashboardChartTitle.builder().content(content).font(reportFont.getItalicFont()).fontSize(CHART_TITLE_FONT_SIZE)
				.xlocation(xLocation).ylocation(CHART_TITLE_Y_LOCATION).title(title).build().display();
	}

	private void createFirstChartTitle() {
		createTitle(strategyDisplay.firstLevelText(),
				strategyDisplay.firstLevelChartXLocation() + CHART_TITLE_X_PADDING);
	}

	private void createSecondChartTitle() {
		createTitle(strategyDisplay.secondLevelText(),
				strategyDisplay.secondLevelChartXLocation() + CHART_TITLE_X_PADDING);
	}

	private void createThirdChartTitle() {
		createTitle(strategyDisplay.thirdLevelText(),
				strategyDisplay.thirdLevelChartXLocation() + CHART_TITLE_X_PADDING);
	}

	private void createLogsChartTitle() {
		if (strategyDisplay.displayLogsChart()) {
			createTitle(strategyDisplay.logsText(), strategyDisplay.logsChartXLocation() + CHART_TITLE_X_PADDING);
		}
	}

	private void createDonutChart(Map<Status, Long> data, float xLocation) {
		DashboardChartDonut.builder().content(content).document(document).config(config).width(CHART_DIMENSION)
				.height(CHART_DIMENSION).xlocation(xLocation).ylocation(CHART_Y_LOCATION).data(data).build().display();
	}

	private void createFirstDonutChart() {
		createDonutChart(report.getStats().getParent(), strategyDisplay.firstLevelChartXLocation());
	}

	private void createSecondDonutChart() {
		createDonutChart(report.getStats().getChild(), strategyDisplay.secondLevelChartXLocation());
	}

	private void createThirdDonutChart() {
		createDonutChart(report.getStats().getGrandchild(), strategyDisplay.thirdLevelChartXLocation());
	}

	private void createLogsDonutChart() {
		if (strategyDisplay.displayLogsChart())
			createDonutChart(report.getStats().getLog(), strategyDisplay.logsChartXLocation());
	}
}
