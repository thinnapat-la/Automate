package tech.grasshopper.reporter.context;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import com.aventstack.extentreports.Status;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.annotation.Annotation;
import tech.grasshopper.pdf.structure.cell.TextLinkCell;
import tech.grasshopper.reporter.annotation.AnnotationStore;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ContextAttributeSummary extends AttributeSummaryDisplay {

	private static final float HEADER_NAME_WIDTH = 215f;
	private static final float HEADER_PASS_WIDTH = 45f;
	private static final float HEADER_FAIL_WIDTH = 45f;
	private static final float HEADER_SKIP_WIDTH = 45f;
	private static final float HEADER_WARN_WIDTH = 45f;
	private static final float HEADER_INFO_WIDTH = 45f;
	private static final float HEADER_PASS_PERCENT_WIDTH = 60f;

	@Default
	private Map<String, Map<Status, Integer>> data = new LinkedHashMap<>();

	protected AnnotationStore annotations;

	@Override
	public void display() {

		data = contextAttributeData();

		if (data.isEmpty())
			return;

		createTableBuilder();
		createTitleRow();
		createHeaderRow();
		createDataRows();
		drawTable();
	}

	private void createTableBuilder() {
		tableBuilder = Table.builder()
				.addColumnsOfWidth(HEADER_NAME_WIDTH, HEADER_PASS_WIDTH, HEADER_FAIL_WIDTH, HEADER_SKIP_WIDTH,
						HEADER_WARN_WIDTH, HEADER_INFO_WIDTH, HEADER_PASS_PERCENT_WIDTH)
				.padding(HEADER_PADDING).borderColor(Color.LIGHT_GRAY).borderWidth(BORDER_WIDTH)
				.horizontalAlignment(HorizontalAlignment.CENTER).verticalAlignment(VerticalAlignment.TOP);
	}

	private void createTitleRow() {
		tableBuilder.addRow(Row.builder().height(NAME_HEIGHT).font(reportFont.getBoldItalicFont())
				.fontSize(NAME_FONT_SIZE).borderWidth(0)
				.add(TextCell.builder().text(type.toString()).textColor(config.attributeHeaderColor(type))
						.horizontalAlignment(HorizontalAlignment.LEFT).colSpan(7).build())
				.build());
	}

	private void createHeaderRow() {
		tableBuilder
				.addRow(Row.builder().height(HEADER_HEIGHT).font(reportFont.getItalicFont()).fontSize(HEADER_FONT_SIZE)
						.add(TextCell.builder().text("Name").textColor(config.attributeNameColor(type))
								.font(reportFont.getBoldItalicFont()).lineSpacing(MULTILINE_SPACING)
								.horizontalAlignment(HorizontalAlignment.LEFT).build())
						.add(TextCell.builder().text("Pass").textColor(config.getPassColor()).build())
						.add(TextCell.builder().text("Fail").textColor(config.getFailColor()).build())
						.add(TextCell.builder().text("Skip").textColor(config.getSkipColor()).build())
						.add(TextCell.builder().text("Warn").textColor(config.getWarnColor()).build())
						.add(TextCell.builder().text("Info").textColor(config.getInfoColor()).build())
						.add(TextCell.builder().text("Pass %").textColor(config.getPassColor()).build()).build());
	}

	private void createDataRows() {
		data.forEach((k, v) -> {
			Annotation annotation = Annotation.builder().title(type.toString().toLowerCase() + "- " + k).build();
			annotations.addAttributeNameAnnotation(annotation);

			int passpercent = (v.getOrDefault(Status.PASS, 0) * 100)
					/ (v.values().stream().mapToInt(Integer::intValue).sum());

			Row row = Row.builder().font(TABLE_CONTENT_FONT).fontSize(TABLE_CONTENT_FONT_SIZE).wordBreak(true)
					.padding(TABLE_PADDING).add(createAttributeNameCell(textSanitizer.sanitizeText(k), annotation))
					.add(TextCell.builder().text(String.valueOf(v.getOrDefault(Status.PASS, 0)))
							.textColor(config.getPassColor()).build())
					.add(TextCell.builder().text(String.valueOf(v.getOrDefault(Status.FAIL, 0)))
							.textColor(config.getFailColor()).build())
					.add(TextCell.builder().text(String.valueOf(v.getOrDefault(Status.SKIP, 0)))
							.textColor(config.getSkipColor()).build())
					.add(TextCell.builder().text(String.valueOf(v.getOrDefault(Status.WARNING, 0)))
							.textColor(config.getWarnColor()).build())
					.add(TextCell.builder().text(String.valueOf(v.getOrDefault(Status.INFO, 0)))
							.textColor(config.getInfoColor()).build())
					.add(TextCell.builder().text(String.valueOf(passpercent)).textColor(config.getPassColor()).build())
					.build();

			tableBuilder.addRow(row);
		});
	}

	private AbstractCell createAttributeNameCell(String title, Annotation annotation) {
		if (config.isDisplayAttributeDetails()) {
			return TextLinkCell.builder().annotation(annotation).text(title).lineSpacing(MULTILINE_SPACING)
					.textColor(config.attributeNameColor(type)).horizontalAlignment(HorizontalAlignment.LEFT).build();
		}
		return TextCell.builder().text(title).lineSpacing(MULTILINE_SPACING).textColor(config.attributeNameColor(type))
				.horizontalAlignment(HorizontalAlignment.LEFT).build();
	}
}
