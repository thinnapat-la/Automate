package tech.grasshopper.reporter.component.text;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.component.Component;
import tech.grasshopper.reporter.exception.PdfReportException;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TextComponent extends Component {
	
	private static final Logger logger = Logger.getLogger(TextComponent.class.getName());

	private Text text;
	
	@Override
	public void display() {		
		try {
			content.beginText();
			content.setNonStrokingColor(text.getTextColor());
			content.setFont(text.getFont(), text.getFontSize());
			content.newLineAtOffset(text.getXlocation(), text.getYlocation());
			content.showText(text.getText());
			content.endText();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "An exception occurred", e);
			throw new PdfReportException(e);
		}
	}
}
