package tech.grasshopper.reporter.tests;

import com.aventstack.extentreports.gherkin.model.ScenarioOutline;
import com.aventstack.extentreports.model.Test;

import lombok.Builder;
import lombok.Setter;

@Builder
public class TestGherkinDisplayOptions {

	@Setter
	private Test test;

	public boolean displayTestDetails() {

		switch (test.getLevel()) {
		case 0:
		case 1:
		case 2:
			return true;
		case 3:
			if (test.getParent().getParent().getBddType() == ScenarioOutline.class)
				return true;
			else
				return false;
		default:
			return false;
		}
	}
	
	

}
