package tech.grasshopper.reporter.dashboard.statistics;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.Status;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.component.Component;
import tech.grasshopper.reporter.component.decorator.BackgroundDecorator;
import tech.grasshopper.reporter.component.decorator.BorderDecorator;
import tech.grasshopper.reporter.component.text.MultipleTextComponent;
import tech.grasshopper.reporter.component.text.Text;
import tech.grasshopper.reporter.dashboard.AnalysisStrategyDisplay;
import tech.grasshopper.reporter.optimizer.TextLengthOptimizer;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.util.DateUtil;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DashboardStatisticsDisplay extends Display {

	private static final int TEST_START_X_LOCATION = 50;
	private static final int TEST_END_X_LOCATION = 210;
	private static final int TEST_DURATION_X_LOCATION = 370;
	private static final int TEST_PASSED_X_LOCATION = 530;
	private static final int TEST_FAILED_X_LOCATION = 665;

	private static final int FIRST_ROW_Y_LOCATION = 465;
	private static final int SECOND_ROW_Y_LOCATION = 440;

	private static final int CONTAINER_PADDING = 10;
	private static final int CONTAINER_WIDTH = 150;
	private static final int CONTAINER_PASS_FAIL_WIDTH = 125;
	private static final int CONTAINER_HEIGHT = 55;
	private static final int CONTAINER_Y_BOTTOM = 430;

	private static final int TITLE_FONT_SIZE = 13;
	private static final int DATE_FONT_SIZE = 17;
	private static final int DATE_MILLIS_FONT_SIZE = 14;
	private static final int DURATION_PASS_FAIL_FONT_SIZE = 20;

	private AnalysisStrategyDisplay strategyDisplay;

	@Override
	public void display() {

		strategyDisplay = AnalysisStrategyDisplay.displaySettings(report);

		createTestStartedTextBox();
		createTestFinishedTextBox();
		createTestDurationTextBox();
		createFirstLevelPassedTextBox();
		createFirstLevelFailedTextBox();
	}

	private void createTestStartedTextBox() {
		LocalDateTime start = DateUtil.convertToLocalDateTimeFromDate(report.getStartTime());
		List<Text> texts = createTestTexts(start, "Started : ", config.getStartTimesColor(), TEST_START_X_LOCATION);
		createTestStatisticsTextBox(texts, TEST_START_X_LOCATION, CONTAINER_WIDTH);
	}

	private void createTestFinishedTextBox() {
		LocalDateTime end = DateUtil.convertToLocalDateTimeFromDate(report.getEndTime());
		List<Text> texts = createTestTexts(end, "Finished : ", config.getFinishTimesColor(), TEST_END_X_LOCATION);
		createTestStatisticsTextBox(texts, TEST_END_X_LOCATION, CONTAINER_WIDTH);
	}

	private void createTestDurationTextBox() {
		LocalDateTime start = DateUtil.convertToLocalDateTimeFromDate(report.getStartTime());
		LocalDateTime end = DateUtil.convertToLocalDateTimeFromDate(report.getEndTime());

		List<Text> texts = createDurationPassFailTexts("Duration : ", DateUtil.durationValue(start, end),
				config.getDurationColor(), TEST_DURATION_X_LOCATION);
		createTestStatisticsTextBox(texts, TEST_DURATION_X_LOCATION, CONTAINER_WIDTH);
	}

	private void createFirstLevelPassedTextBox() {
		List<Text> texts = createDurationPassFailTexts(strategyDisplay.firstLevelText() + " Passed : ",
				String.valueOf(report.getStats().getParent().get(Status.PASS)), config.getPassCountColor(),
				TEST_PASSED_X_LOCATION);
		createTestStatisticsTextBox(texts, TEST_PASSED_X_LOCATION, CONTAINER_PASS_FAIL_WIDTH);
	}

	private void createFirstLevelFailedTextBox() {
		List<Text> texts = createDurationPassFailTexts(strategyDisplay.firstLevelText() + " Failed : ",
				String.valueOf(report.getStats().getParent().get(Status.FAIL)), config.getFailCountColor(),
				TEST_FAILED_X_LOCATION);
		createTestStatisticsTextBox(texts, TEST_FAILED_X_LOCATION, CONTAINER_PASS_FAIL_WIDTH);
	}

	private List<Text> createDurationPassFailTexts(String title, String value, Color valueColor, float xLocation) {
		List<Text> texts = new ArrayList<>();
		texts.add(Text.builder().fontSize(TITLE_FONT_SIZE).font(reportFont.getItalicFont()).textColor(Color.BLACK)
				.xlocation(xLocation + CONTAINER_PADDING).ylocation(FIRST_ROW_Y_LOCATION).text(title).build());
		texts.add(Text.builder().fontSize(DURATION_PASS_FAIL_FONT_SIZE).font(reportFont.getBoldItalicFont())
				.textColor(valueColor).xlocation(xLocation + CONTAINER_PADDING).ylocation(SECOND_ROW_Y_LOCATION)
				.text(value).build());
		return texts;
	}

	private List<Text> createTestTexts(LocalDateTime datetime, String title, Color dateTimeColor, float xLocation) {
		List<Text> texts = new ArrayList<>();
		texts.add(Text.builder().fontSize(TITLE_FONT_SIZE).font(reportFont.getItalicFont()).textColor(Color.BLACK)
				.xlocation(xLocation + CONTAINER_PADDING).ylocation(FIRST_ROW_Y_LOCATION).text(title).build());

		int titleWidth = TextLengthOptimizer.builder().font(reportFont.getItalicFont()).fontsize(TITLE_FONT_SIZE)
				.build().textWidth(title);

		texts.add(Text.builder().fontSize(DATE_FONT_SIZE).font(reportFont.getBoldItalicFont()).textColor(dateTimeColor)
				.xlocation(xLocation + CONTAINER_PADDING + titleWidth + 2).ylocation(FIRST_ROW_Y_LOCATION)
				.text(DateUtil.formatDateWOYear(datetime)).build());

		texts.add(Text.builder().fontSize(DATE_FONT_SIZE).font(reportFont.getBoldItalicFont()).textColor(dateTimeColor)
				.xlocation(xLocation + CONTAINER_PADDING).ylocation(SECOND_ROW_Y_LOCATION)
				.text(DateUtil.formatTime(datetime)).build());

		int timeWidth = TextLengthOptimizer.builder().font(reportFont.getBoldItalicFont()).fontsize(DATE_FONT_SIZE)
				.build().textWidth(DateUtil.formatTime(datetime));

		texts.add(Text.builder().fontSize(DATE_MILLIS_FONT_SIZE).font(reportFont.getBoldItalicFont())
				.textColor(dateTimeColor).xlocation(xLocation + CONTAINER_PADDING + timeWidth + 1)
				.ylocation(SECOND_ROW_Y_LOCATION).text("." + DateUtil.formatTimeMillis(datetime)).build());

		int millisWidth = TextLengthOptimizer.builder().font(reportFont.getBoldItalicFont())
				.fontsize(DATE_MILLIS_FONT_SIZE).build().textWidth("." + DateUtil.formatTimeMillis(datetime));

		texts.add(Text.builder().fontSize(DATE_FONT_SIZE).font(reportFont.getBoldItalicFont()).textColor(dateTimeColor)
				.xlocation(xLocation + CONTAINER_PADDING + timeWidth + 1 + millisWidth + 5)
				.ylocation(SECOND_ROW_Y_LOCATION).text(DateUtil.formatAMPM(datetime)).build());

		return texts;
	}

	private void createTestStatisticsTextBox(List<Text> texts, float xBoxLocation, float boxWidth) {

		Component component = MultipleTextComponent.builder().content(content).texts(texts).build();
		component = BackgroundDecorator.builder().component(component).content(content).containerColor(Color.WHITE)
				.xContainerBottomLeft(xBoxLocation).yContainerBottomLeft(CONTAINER_Y_BOTTOM).containerWidth(boxWidth)
				.containerHeight(CONTAINER_HEIGHT).build();
		component = BorderDecorator.builder().component(component).content(content).xContainerBottomLeft(xBoxLocation)
				.yContainerBottomLeft(CONTAINER_Y_BOTTOM).containerWidth(boxWidth).containerHeight(CONTAINER_HEIGHT)
				.build();
		component.display();
	}
}
