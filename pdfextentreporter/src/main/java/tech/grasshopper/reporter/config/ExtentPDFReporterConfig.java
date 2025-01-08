package tech.grasshopper.reporter.config;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.configuration.AbstractConfiguration;

import lombok.Builder.Default;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.context.AttributeType;

@Setter
@SuperBuilder
public class ExtentPDFReporterConfig extends AbstractConfiguration {

	@Default
	private boolean displayAttributeSummary = true;
	@Default
	private boolean displayAttributeDetails = true;
	@Default
	private boolean displayTestDetails = true;
	@Default
	private boolean displayExpandedMedia = false;
	@Default
	private boolean displayAttachedMedia = true;

	private String title;
	private String titleColor;
	private String dateFormat;
	private String dateColor;

	private String startTimesColor;
	private String finishTimesColor;
	private String durationColor;
	private String passCountColor;
	private String failCountColor;

	private String passColor;
	private String failColor;
	private String skipColor;
	private String warnColor;
	private String infoColor;

	private String categoryAttributeColor;
	private String categoryNameColor;

	private String authorAttributeColor;
	private String authorNameColor;

	private String deviceAttributeColor;
	private String deviceNameColor;

	private String systemAttributeColor;
	private String systemNameColor;
	private String systemValueColor;

	private String exceptionAttributeColor;

	private String testNameColor;
	private String testDescriptionColor;
	private String testTimesColor;
	private String testTimeStampColor;
	private String testExceptionColor;

	private int maxTableColumnCount;

	private int testMaxIndentLevel;

	private String[] mediaFolders;

	public boolean isDisplayAttributeSummary() {
		return displayAttributeSummary;
	}

	public boolean isDisplayAttributeDetails() {
		return displayAttributeDetails;
	}

	public boolean isDisplayTestDetails() {
		return displayTestDetails;
	}

	public boolean isDisplayExpandedMedia() {
		// Attached option takes precedence
		if (displayAttachedMedia && displayExpandedMedia)
			return false;
		return displayExpandedMedia;
	}

	public boolean isDisplayAttachedMedia() {
		// Attached option takes precedence
		if (displayAttachedMedia && displayExpandedMedia)
			return true;
		return displayAttachedMedia;
	}

	public String getReportTitle() {
		if (title == null || title.isEmpty())
			title = Defaults.title;
		return title;
	}

	public Color getReportTitleColor() {
		return createColor(titleColor, Defaults.titleColor);
	}

	public DateTimeFormatter getReportDateFormat() {
		try {
			return DateTimeFormatter.ofPattern(dateFormat);
		} catch (Exception e) {
			// Log the exception
			return Defaults.dateFormatter;
		}
	}

	public Color getReportDateColor() {
		return createColor(dateColor, Defaults.dateColor);
	}

	public Color getStartTimesColor() {
		return createColor(startTimesColor, Defaults.startTimesColor);
	}

	public Color getFinishTimesColor() {
		return createColor(finishTimesColor, Defaults.finishTimesColor);
	}

	public Color getDurationColor() {
		return createColor(durationColor, Defaults.durationColor);
	}

	public Color getPassCountColor() {
		return createColor(passCountColor, Defaults.passCountColor);
	}

	public Color getFailCountColor() {
		return createColor(failCountColor, Defaults.failCountColor);
	}

	public Color getPassColor() {
		return createColor(passColor, Defaults.passColor);
	}

	public Color getFailColor() {
		return createColor(failColor, Defaults.failColor);
	}

	public Color getSkipColor() {
		return createColor(skipColor, Defaults.skipColor);
	}

	public Color getWarnColor() {
		return createColor(warnColor, Defaults.warnColor);
	}

	public Color getInfoColor() {
		return createColor(infoColor, Defaults.infoColor);
	}

	public Color getCategoryTitleColor() {
		return createColor(categoryAttributeColor, Defaults.categoryAttributeColor);
	}

	public Color getCategoryNameColor() {
		return createColor(categoryNameColor, Defaults.categoryNameColor);
	}

	public Color getAuthorTitleColor() {
		return createColor(authorAttributeColor, Defaults.authorAttributeColor);
	}

	public Color getAuthorNameColor() {
		return createColor(authorNameColor, Defaults.authorNameColor);
	}

	public Color getDeviceTitleColor() {
		return createColor(deviceAttributeColor, Defaults.deviceAttributeColor);
	}

	public Color getDeviceNameColor() {
		return createColor(deviceNameColor, Defaults.deviceNameColor);
	}

	public Color getSystemTitleColor() {
		return createColor(systemAttributeColor, Defaults.systemAttributeColor);
	}

	public Color getSystemNameColor() {
		return createColor(systemNameColor, Defaults.systemNameColor);
	}

	public Color getSystemValueColor() {
		return createColor(systemValueColor, Defaults.systemValueColor);
	}

	public Color getExceptionTitleColor() {
		return createColor(exceptionAttributeColor, Defaults.exceptionAttributeColor);
	}

	public Color getTestNameColor() {
		return createColor(testNameColor, Defaults.testNameColor);
	}

	public Color getTestDescriptionColor() {
		return createColor(testDescriptionColor, Defaults.testDescriptionColor);
	}

	public Color getTestTimesColor() {
		return createColor(testTimesColor, Defaults.testTimesColor);
	}

	public Color getTestTimeStampColor() {
		return createColor(testTimeStampColor, Defaults.testTimeStampColor);
	}

	public Color getTestExceptionColor() {
		return createColor(testExceptionColor, Defaults.testExceptionColor);
	}

	private Color createColor(String hexCode, Color defaultColor) {
		try {
			return Color.decode("#" + hexCode);
		} catch (Exception e) {
			// Log the exception
			return defaultColor;
		}
	}

	public int getMaxTableColumnCount() {
		if (maxTableColumnCount == 0)
			maxTableColumnCount = Defaults.maxTableColumnCount;
		return maxTableColumnCount;
	}

	public int getTestMaxIndentLevel() {
		if (testMaxIndentLevel == 0)
			testMaxIndentLevel = Defaults.testMaxIndentLevel;
		return testMaxIndentLevel;
	}

	public String[] getMediaFolders() {
		if (mediaFolders == null)
			mediaFolders = Defaults.mediaFolders;
		return mediaFolders;
	}

	private static class Defaults {

		private static final String title = "PDF Extent Report";
		private static final Color titleColor = Color.BLACK;
		private static final DateTimeFormatter dateFormatter = DateTimeFormatter
				.ofLocalizedDateTime(FormatStyle.MEDIUM);
		private static final Color dateColor = Color.BLUE;
		private static final Color startTimesColor = Color.RED;
		private static final Color finishTimesColor = Color.RED;
		private static final Color durationColor = Color.RED;
		private static final Color passCountColor = Color.RED;
		private static final Color failCountColor = Color.RED;
		private static final Color passColor = Color.GREEN;
		private static final Color failColor = Color.RED;
		private static final Color skipColor = Color.ORANGE;
		private static final Color warnColor = Color.YELLOW;
		private static final Color infoColor = Color.BLUE;
		private static final Color categoryAttributeColor = Color.CYAN;
		private static final Color categoryNameColor = Color.BLACK;
		private static final Color authorAttributeColor = Color.MAGENTA;
		private static final Color authorNameColor = Color.BLACK;
		private static final Color deviceAttributeColor = Color.DARK_GRAY;
		private static final Color deviceNameColor = Color.BLACK;
		private static final Color systemAttributeColor = Color.BLACK;
		private static final Color systemNameColor = Color.BLACK;
		private static final Color systemValueColor = Color.BLACK;
		private static final Color exceptionAttributeColor = Color.RED;
		private static final Color testNameColor = Color.RED;
		private static final Color testDescriptionColor = Color.BLACK;
		private static final Color testTimesColor = Color.BLUE;
		private static final Color testTimeStampColor = Color.BLACK;
		private static final Color testExceptionColor = Color.RED;
		private static final int maxTableColumnCount = 5;
		private static final int testMaxIndentLevel = 2;
		private static final String[] mediaFolders = new String[] { "", "images", "medias", "screenshots" };
	}

	public Color attributeHeaderColor(AttributeType type) {
		if (type == AttributeType.CATEGORY)
			return getCategoryTitleColor();
		if (type == AttributeType.DEVICE)
			return getDeviceTitleColor();
		if (type == AttributeType.AUTHOR)
			return getAuthorTitleColor();
		if (type == AttributeType.EXCEPTION)
			return getExceptionTitleColor();
		if (type == AttributeType.SYSTEM)
			return getSystemTitleColor();
		return Color.RED;
	}

	public Color attributeNameColor(AttributeType type) {
		if (type == AttributeType.CATEGORY)
			return getCategoryNameColor();
		if (type == AttributeType.DEVICE)
			return getDeviceNameColor();
		if (type == AttributeType.AUTHOR)
			return getAuthorNameColor();
		if (type == AttributeType.SYSTEM)
			return getSystemNameColor();
		return Color.BLACK;
	}

	public Color statusColor(Status status) {
		if (status == Status.PASS)
			return getPassColor();
		if (status == Status.FAIL)
			return getFailColor();
		if (status == Status.SKIP)
			return getSkipColor();
		if (status == Status.WARNING)
			return getWarnColor();
		if (status == Status.INFO)
			return getInfoColor();
		return Color.BLACK;
	}
}
