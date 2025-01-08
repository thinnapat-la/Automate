package tech.grasshopper.reporter.tests.markup;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class MarkupDisplay {

	protected final float BORDER_WIDTH = 1f;
	protected final float MULTILINE_SPACING = 1f;

	@NonNull
	protected PDFont logFont;
	protected final int LOG_FONT_SIZE = 11;

	protected Element element;
	protected Elements elements;

	@Default
	protected Color textColor = Color.BLACK;

	public abstract AbstractCell displayDetails();

	protected AbstractCell errorDisplay(String error) {
		return TextCell.builder().text(error).fontSize(LOG_FONT_SIZE).font(logFont).textColor(Color.RED).build();
	}
}
