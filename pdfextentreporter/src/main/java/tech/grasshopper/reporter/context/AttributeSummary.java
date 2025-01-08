package tech.grasshopper.reporter.context;

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
public class AttributeSummary extends Section implements PageHeaderAware {

	@Default
	private float destinationYLocation = Display.CONTENT_START_Y;

	@Override
	public void createSection() {

		if (!checkDataValidity())
			return;

		pageHeaderDetails();
		createPage();

		ContextAttributeSummary categories = ContextAttributeSummary.builder().config(config).document(document)
				.type(AttributeType.CATEGORY).report(report).reportFont(reportFont).annotations(annotations)
				.ylocation(destinationYLocation).build();
		categories.display();
		if (report.getCategoryCtx().hasItems())
			createAttributeDestination(categories);

		ContextAttributeSummary authors = ContextAttributeSummary.builder().config(config).document(document)
				.type(AttributeType.AUTHOR).report(report).reportFont(reportFont).annotations(annotations)
				.ylocation(categories.getYlocation()).build();
		authors.display();
		if (report.getAuthorCtx().hasItems())
			createAttributeDestination(authors);

		ContextAttributeSummary devices = ContextAttributeSummary.builder().config(config).document(document)
				.type(AttributeType.DEVICE).report(report).reportFont(reportFont).annotations(annotations)
				.ylocation(authors.getYlocation()).build();
		devices.display();
		if (report.getDeviceCtx().hasItems())
			createAttributeDestination(devices);

		SystemAttributeSummary systems = SystemAttributeSummary.builder().config(config).document(document)
				.type(AttributeType.SYSTEM).report(report).reportFont(reportFont).ylocation(devices.getYlocation())
				.build();
		systems.display();
		if (!report.getSystemEnvInfo().isEmpty())
			createAttributeDestination(systems);
	}

	private void createAttributeDestination(AttributeSummaryDisplay summaryDisplay) {
		destinations.addAttributeSummaryDestination(summaryDisplay.createDestination());
	}

	@Override
	public String getSectionTitle() {
		return PageHeader.ATTRIBUTE_SUMMARY_SECTION;
	}

	private boolean checkDataValidity() {
		if (report.getCategoryCtx().hasItems() || report.getAuthorCtx().hasItems() || report.getDeviceCtx().hasItems()
				|| !report.getSystemEnvInfo().isEmpty())
			return true;
		return false;
	}
}
