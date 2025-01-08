package tech.grasshopper.reporter.tests;

import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.gherkin.model.ScenarioOutline;
import com.aventstack.extentreports.model.Test;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.destination.Destination;
import tech.grasshopper.reporter.header.PageHeader;
import tech.grasshopper.reporter.header.PageHeaderAware;
import tech.grasshopper.reporter.optimizer.TextSanitizer;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.Section;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TestBDDDetails extends Section implements PageHeaderAware {

	public static final int LEVEL_X_INDENT = 20;

	@Default
	private float yLocation = Display.CONTENT_START_Y;

	private Destination destination;

	@Override
	public void createSection() {

		if (report.getTestList().isEmpty())
			return;

		pageHeaderDetails();
		createPage();

		for (Test level0Test : report.getTestList()) {
			displayBasicTestDetails(level0Test);

			for (Test level1Test : level0Test.getChildren()) {
				boolean isSO = level1Test.getBddType() == ScenarioOutline.class ? true : false;

				displayBasicTestDetails(level1Test);
				if (!isSO && level1Test.hasChildren()) {
					// Display the next level test names and logs. Make destination to parent test.
					// Hide children tests but collect destination with scenario (Level 1)
					// destination
					displayExecutableTestDetailsAndCreateHiddenTestDestination(level1Test);
					continue;
				}

				for (Test level2Test : level1Test.getChildren()) {
					displayBasicTestDetails(level2Test);
					// Display the next level test names and logs. Make destination to parent test.
					// Hide children () tests but collect destination with scenario (Level 2)
					// destination
					if (level2Test.hasChildren())
						displayExecutableTestDetailsAndCreateHiddenTestDestination(level2Test);
				}
			}
		}
	}

	private void displayBasicTestDetails(Test test) {
		TestBasicDetailsDisplay testBasicDetailsDisplay = TestBasicDetailsDisplay.builder().document(document)
				.reportFont(reportFont).config(config).test(test).ylocation(yLocation).build();
		testBasicDetailsDisplay.display();
		createTestDestination(testBasicDetailsDisplay);
		yLocation = testBasicDetailsDisplay.getYlocation();
	}

	private void displayExecutableTestDetailsAndCreateHiddenTestDestination(Test test) {
		TestBDDExecutableDisplay executableDisplay = TestBDDExecutableDisplay.builder().test(test).document(document)
				.reportFont(reportFont).config(config).ylocation(yLocation).annotations(annotations).build();
		executableDisplay.display();
		yLocation = executableDisplay.getYlocation();

		TextSanitizer textSanitizer = TextSanitizer.builder().font(reportFont.getRegularFont()).build();

		List<Test> childTests = new ArrayList<>();
		collectTestNodes(test, childTests);

		childTests.forEach(t -> {
			destinations.addTestDestination(
					Destination.builder().id(t.getId()).name(textSanitizer.sanitizeText(t.getName()))
							.yCoord(destination.getYCoord()).page(destination.getPage()).build());
		});
	}

	private void collectTestNodes(Test test, List<Test> tests) {
		test.getChildren().forEach(t -> {
			tests.add(t);
			collectTestNodes(t, tests);
		});
	}

	private void createTestDestination(TestBasicDetailsDisplay testDisplay) {
		destination = testDisplay.createDestination();
		destinations.addTestDestination(destination);

		if (testDisplay.getTest().getLevel() == 0)
			destinations.addTopLevelTestDestination(destination);
	}

	@Override
	public String getSectionTitle() {
		return PageHeader.TEST_DETAILS_SECTION;
	}

}
