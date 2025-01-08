package tech.grasshopper.reporter.context.detail;

import java.util.Set;

import com.aventstack.extentreports.model.NamedAttribute;
import com.aventstack.extentreports.model.context.NamedAttributeContext;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.context.AttributeType;
import tech.grasshopper.reporter.header.PageHeader;
import tech.grasshopper.reporter.header.PageHeaderAware;
import tech.grasshopper.reporter.structure.Display;
import tech.grasshopper.reporter.structure.Section;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class AttributeDetails extends Section implements PageHeaderAware {

	@Default
	private float yLocation = Display.CONTENT_START_Y;

	@Override
	public void createSection() {

		if (!checkDataValidity())
			return;

		pageHeaderDetails();
		createPage();

		processDisplay(report.getCategoryCtx().getSet(), AttributeType.CATEGORY);
		processDisplay(report.getAuthorCtx().getSet(), AttributeType.AUTHOR);
		processDisplay(report.getDeviceCtx().getSet(), AttributeType.DEVICE);
		processDisplay(report.getExceptionInfoCtx().getSet(), AttributeType.EXCEPTION);
	}

	private <T extends NamedAttribute> void processDisplay(Set<NamedAttributeContext<T>> attributes,
			AttributeType type) {

		for (NamedAttributeContext<? extends NamedAttribute> attribute : attributes) {
			AttributeTestStatusBasicDisplay attributeBasicDisplay = AttributeTestStatusBasicDisplay.builder()
					.document(document).reportFont(reportFont).config(config).type(type).attribute(attribute)
					.ylocation(yLocation).build();
			attributeBasicDisplay.display();
			createAttributeDestination(attributeBasicDisplay);
			yLocation = attributeBasicDisplay.getYlocation();

			AttributeTestStatusDetailsDisplay attributeDetailsDisplay = AttributeTestStatusDetailsDisplay.builder()
					.document(document).reportFont(reportFont).config(config).attribute(attribute)
					.annotations(annotations).ylocation(yLocation).build();
			attributeDetailsDisplay.display();
			yLocation = attributeDetailsDisplay.getYlocation();
		}
	}

	private void createAttributeDestination(AttributeTestStatusBasicDisplay attributeBasicDisplay) {
		destinations.addAttributeDetailDestination(attributeBasicDisplay.createDestination());
	}

	@Override
	public String getSectionTitle() {
		return PageHeader.ATTRIBUTE_DETAILS_SECTION;
	}

	private boolean checkDataValidity() {
		if (report.getCategoryCtx().hasItems() || report.getAuthorCtx().hasItems() || report.getDeviceCtx().hasItems()
				|| report.getExceptionInfoCtx().hasItems())
			return true;
		return false;
	}
}
