package tech.grasshopper.reporter.expanded;

import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Test;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.header.PageHeader;
import tech.grasshopper.reporter.header.PageHeaderAware;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.Section;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class MediaSummary extends Section implements PageHeaderAware {

	@Default
	protected float yLocation = Display.CONTENT_START_Y;

	@Override
	public void createSection() {

		List<Test> allTests = collectRelevantTests();

		if (!checkDataValidity(allTests))
			return;

		pageHeaderDetails();
		createPage();

		for (Test test : allTests) {
			boolean containsMedia = doesTestContainMedia(test);

			if (containsMedia) {
				ExpandedMediaDisplay expandedMediaDisplay = ExpandedMediaDisplay.builder().document(document)
						.reportFont(reportFont).config(config).test(test).annotations(annotations).ylocation(yLocation)
						.build();
				expandedMediaDisplay.display();
				createMediaDestination(expandedMediaDisplay);
				yLocation = expandedMediaDisplay.getYlocation();
			}
		}
	}

	protected void createMediaDestination(ExpandedMediaDisplay expandedMediaDisplay) {
		destinations.addTestMediaDestination(expandedMediaDisplay.createDestination());
	}

	@Override
	public String getSectionTitle() {
		return PageHeader.EXPANDED_MEDIA_SECTION;
	}

	protected List<Test> collectRelevantTests() {
		List<Test> tests = new ArrayList<>();
		report.getTestList().forEach(t -> {
			tests.add(t);
			collectTestNodes(t, tests);
		});
		return tests;
	}

	protected boolean doesTestContainMedia(Test test) {
		if (!test.getMedia().isEmpty())
			return true;

		for (Log log : test.getLogs()) {
			if (log.hasMedia())
				return true;
		}
		return false;
	}

	protected boolean checkDataValidity(List<Test> allTests) {
		for (Test test : allTests) {
			if (doesTestContainMedia(test))
				return true;
		}
		return false;
	}

	protected void collectTestNodes(Test test, List<Test> tests) {
		test.getChildren().forEach(t -> {
			tests.add(t);
			collectTestNodes(t, tests);
		});
	}
}
