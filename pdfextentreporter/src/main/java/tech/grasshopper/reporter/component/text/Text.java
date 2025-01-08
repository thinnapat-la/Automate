package tech.grasshopper.reporter.component.text;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.font.PDFont;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Text {

	@Default
	private Color textColor = Color.BLACK;
	@NonNull
	private PDFont font;
	@Default
	private float fontSize = 12;
	private float xlocation;
	private float ylocation;
	private String text;
}
