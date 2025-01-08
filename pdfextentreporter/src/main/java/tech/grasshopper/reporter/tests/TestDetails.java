package tech.grasshopper.reporter.tests;

import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.model.Test;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.destination.Destination;
import tech.grasshopper.reporter.header.PageHeader;
import tech.grasshopper.reporter.header.PageHeaderAware;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.Section;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TestDetails extends Section implements PageHeaderAware {

	public static final int LEVEL_X_INDENT = 20;

	@Default
	protected float yLocation = Display.CONTENT_START_Y;

	@Override
	public void createSection() {
		if (report.getTestList().isEmpty())
			return;

		pageHeaderDetails();
		createPage();

		List<Test> allTests = new ArrayList<>();
		report.getTestList().forEach(t -> {
			allTests.add(t);
			collectTestNodes(t, allTests);
		});

		displayTestAndLogDetails(allTests);
	}

	protected void displayTestAndLogDetails(List<Test> allTests) {
		for (Test test : allTests) {

			displayTestBasicData(test);
			displayTestMedias(test);
			displayTestGeneratedLogs(test);
			displayTestLogs(test);
		}
	}

	protected void displayTestLogs(Test test) {
		if (test.hasLog()) {
			TestLogsDisplay testLogsDisplay = TestLogsDisplay.builder().document(document).reportFont(reportFont)
					.config(config).test(test).annotations(annotations).ylocation(yLocation).build();
			testLogsDisplay.display();

			yLocation = testLogsDisplay.getYlocation();
		}
	}

	protected void displayTestGeneratedLogs(Test test) {
		if (!test.getGeneratedLog().isEmpty()) {
			TestGeneratedLogDisplay testGeneratedLogDisplay = TestGeneratedLogDisplay.builder().document(document)
					.reportFont(reportFont).config(config).test(test).ylocation(yLocation).build();
			testGeneratedLogDisplay.display();

			yLocation = testGeneratedLogDisplay.getYlocation();
		}
	}

	protected void displayTestMedias(Test test) {
		if (test.hasScreenCapture()) {
			TestMediaDisplay testMediaDisplay = TestMediaDisplay.builder().document(document).reportFont(reportFont)
					.config(config).test(test).annotations(annotations).ylocation(yLocation).build();
			testMediaDisplay.display();

			yLocation = testMediaDisplay.getYlocation();
		}
	}

	protected void displayTestBasicData(Test test) {
		TestBasicDetailsDisplay testBasicDetailsDisplay = TestBasicDetailsDisplay.builder().document(document)
				.reportFont(reportFont).config(config).test(test).ylocation(yLocation).build();
		testBasicDetailsDisplay.display();
		createTestDestination(testBasicDetailsDisplay);

		yLocation = testBasicDetailsDisplay.getYlocation();
	}

	private void collectTestNodes(Test test, List<Test> tests) {
		test.getChildren().forEach(t -> {
			tests.add(t);
			collectTestNodes(t, tests);
		});
	}

	private void createTestDestination(TestBasicDetailsDisplay testDisplay) {
		Destination destination = testDisplay.createDestination();
		destinations.addTestDestination(destination);

		if (testDisplay.getTest().getLevel() == 0)
			destinations.addTopLevelTestDestination(destination);
	}

	@Override
	public String getSectionTitle() {
		return PageHeader.TEST_DETAILS_SECTION;
	}
}
