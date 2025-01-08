package tech.grasshopper.reporter.dashboard.legend;

import static com.aventstack.extentreports.Status.FAIL;
import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.Status.PASS;
import static com.aventstack.extentreports.Status.SKIP;
import static com.aventstack.extentreports.Status.WARNING;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import com.aventstack.extentreports.Status;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.dashboard.AnalysisStrategyDisplay;
import tech.grasshopper.reporter.structure.Display;

@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DashboardChartLegendDisplay extends Display {

	private static final int TOP_LEGEND_Y_LOCATION = 180;
	private static final int CHART_TITLE_X_PADDING = 25;

	private static final Map<Status, Color> statusColor = new LinkedHashMap<>();
	private AnalysisStrategyDisplay strategyDisplay;

	@Override
	public void display() {

		statusColor.put(PASS, config.getPassColor());
		statusColor.put(FAIL, config.getFailColor());
		statusColor.put(SKIP, config.getSkipColor());
		statusColor.put(WARNING, config.getWarnColor());
		statusColor.put(INFO, config.getInfoColor());

		strategyDisplay = AnalysisStrategyDisplay.displaySettings(report);

		createFirstChartDataBox();
		createSecondChartDataBox();
		createThirdChartDataBox();
		createLogsChartDataBox();
	}

	private void createChartLegend(Map<Status, Long> data, float xLocation) {
		DashboardChartLegend.builder().content(content).xlocation(xLocation).ylocation(TOP_LEGEND_Y_LOCATION)
				.statusColor(statusColor).statusData(data).keyFont(reportFont.getItalicFont())
				.valueFont(reportFont.getBoldFont()).build().display();
	}

	private void createFirstChartDataBox() {
		createChartLegend(report.getStats().getParent(),
				strategyDisplay.firstLevelChartXLocation() + CHART_TITLE_X_PADDING);
	}

	private void createSecondChartDataBox() {
		createChartLegend(report.getStats().getChild(),
				strategyDisplay.secondLevelChartXLocation() + CHART_TITLE_X_PADDING);
	}

	private void createThirdChartDataBox() {
		createChartLegend(report.getStats().getGrandchild(),
				strategyDisplay.thirdLevelChartXLocation() + CHART_TITLE_X_PADDING);
	}

	private void createLogsChartDataBox() {
		if (strategyDisplay.displayLogsChart())
			createChartLegend(report.getStats().getLog(), strategyDisplay.logsChartXLocation() + CHART_TITLE_X_PADDING);
	}
}
