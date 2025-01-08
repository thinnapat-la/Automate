package tech.grasshopper.reporter.tests.markup;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.optimizer.TextSanitizer;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class JsonMarkup extends MarkupDisplay {

	private static final Logger logger = Logger.getLogger(JsonMarkup.class.getName());

	private String html;

	@Override
	public AbstractCell displayDetails() {
		TextSanitizer textSanitizer = TextSanitizer.builder().font(logFont).build();
		return TextCell.builder().text(textSanitizer.sanitizeText(jsonText())).textColor(textColor)
				.fontSize(LOG_FONT_SIZE).font(logFont).lineSpacing(MULTILINE_SPACING).build();
	}

	// The code is not optimum with regards to exception handling. Needs to be
	// refactored in future.
	private String jsonText() {
		try {
			String jsonStringHolder = html.substring(html.indexOf("JSONTree"));
			int startIndex = jsonStringHolder.indexOf('{');
			if (startIndex == -1)
				return "";

			int endIndex = startIndex;
			int bktCnt = 0;

			for (char c : jsonStringHolder.substring(startIndex).toCharArray()) {
				endIndex++;
				if (c == '{')
					bktCnt++;
				else if (c == '}')
					bktCnt--;
				if (bktCnt == 0)
					break;
			}

			if (endIndex == startIndex)
				return "";

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(JsonParser.parseString(jsonStringHolder.substring(startIndex, endIndex)));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to process JSON code block.");
			return "Error in accessing and processing JSON code block.";
		}
	}
}
