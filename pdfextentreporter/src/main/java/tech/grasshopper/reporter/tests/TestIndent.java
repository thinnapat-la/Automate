package tech.grasshopper.reporter.tests;

public interface TestIndent {

	public default int calculateIndent(int testLevel, int maxTestLevel) {
		return testLevel < maxTestLevel ? testLevel : maxTestLevel;
	}
}
