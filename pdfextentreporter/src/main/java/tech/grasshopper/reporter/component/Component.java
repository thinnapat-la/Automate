package tech.grasshopper.reporter.component;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class Component {

	protected PDPageContentStream content;
	
	public abstract void display();
}
