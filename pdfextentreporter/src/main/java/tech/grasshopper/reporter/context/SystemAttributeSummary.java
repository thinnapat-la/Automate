package tech.grasshopper.reporter.context;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SystemAttributeSummary extends AttributeSummaryDisplay {

	private static final float HEADER_NAME_WIDTH = 225f;
	private static final float HEADER_VALUE_WIDTH = 275f;

	@Default
	private Map<String, String> data = new LinkedHashMap<>();

	@Override
	public void display() {

		data = systemAttributeData();

		if (data.isEmpty())
			return;

		createTableBuilder();
		createTitleRow();
		createHeaderRow();
		createDataRows();
		drawTable();
	}

	private void createTableBuilder() {
		tableBuilder = Table.builder().addColumnsOfWidth(HEADER_NAME_WIDTH, HEADER_VALUE_WIDTH).padding(HEADER_PADDING)
				.borderColor(Color.LIGHT_GRAY).borderWidth(BORDER_WIDTH).verticalAlignment(VerticalAlignment.TOP)
				.horizontalAlignment(HorizontalAlignment.LEFT);
	}

	private void createTitleRow() {
		tableBuilder.addRow(Row.builder().height(NAME_HEIGHT).font(reportFont.getBoldItalicFont())
				.fontSize(NAME_FONT_SIZE).borderWidth(0).add(TextCell.builder().text(type.toString())
						.textColor(config.getSystemTitleColor()).colSpan(2).build())
				.build());
	}

	private void createHeaderRow() {
		tableBuilder.addRow(
				Row.builder().font(reportFont.getBoldItalicFont()).fontSize(HEADER_FONT_SIZE).height(HEADER_HEIGHT)
						.add(TextCell.builder().text("Name").textColor(config.getSystemNameColor()).build())
						.add(TextCell.builder().text("Value").textColor(config.getSystemValueColor()).build()).build());
	}

	private void createDataRows() {
		data.forEach((k, v) -> {
			Row row = Row.builder().font(TABLE_CONTENT_FONT).fontSize(TABLE_CONTENT_FONT_SIZE).wordBreak(true)
					.padding(TABLE_PADDING)
					.add(TextCell.builder().text(textSanitizer.sanitizeText(k)).textColor(config.getSystemNameColor())
							.lineSpacing(MULTILINE_SPACING).build())
					.add(TextCell.builder().text(textSanitizer.sanitizeText(v)).textColor(config.getSystemValueColor())
							.lineSpacing(MULTILINE_SPACING).build())
					.build();

			tableBuilder.addRow(row);
		});
	}
}
