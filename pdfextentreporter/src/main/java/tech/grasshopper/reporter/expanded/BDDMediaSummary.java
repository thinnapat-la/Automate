package tech.grasshopper.reporter.expanded;

import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.gherkin.model.ScenarioOutline;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class BDDMediaSummary extends MediaSummary {

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

	protected List<Test> collectRelevantTests() {
		List<Test> tests = new ArrayList<>();

		report.getTestList().forEach(t0 -> {

			t0.getChildren().forEach(t1 -> {

				if (t1.getBddType() == ScenarioOutline.class ? true : false)
					t1.getChildren().forEach(t2 -> tests.addAll(t2.getChildren()));
				else
					tests.addAll(t1.getChildren());
			});
		});
		return tests;
	}

	protected boolean doesTestContainMedia(Test test) {
		for (Log log : test.getLogs()) {
			if (log.hasMedia())
				return true;
		}
		return false;
	}
}
