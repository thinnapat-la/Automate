package tech.grasshopper.reporter.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm:ss a", Locale.ENGLISH);
    
    private static final DateTimeFormatter dateTimeWOYearFormatter = DateTimeFormatter.ofPattern("MMM dd, h:mm:ss a", Locale.ENGLISH);
    
    private static final DateTimeFormatter dateWOYearFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH);
    
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss", Locale.ENGLISH);
    
    private static final DateTimeFormatter timeFormatterAmPm = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH);
    
    private static final DateTimeFormatter timeWithMillisFormatter = DateTimeFormatter.ofPattern("h:mm:ss.SSS a", Locale.ENGLISH);
    
    private static final DateTimeFormatter dateTimeWithMillisFormatter = DateTimeFormatter.ofPattern("MMM dd, h:mm:ss.SSS a", Locale.ENGLISH);
    
    private static final DateTimeFormatter millisFormatter = DateTimeFormatter.ofPattern("SSS", Locale.ENGLISH);
    
    private static final DateTimeFormatter ampmFormatter = DateTimeFormatter.ofPattern("a", Locale.ENGLISH);
	

	public static String durationValue(LocalDateTime start, LocalDateTime end) {
		Duration duration = Duration.between(start, end);
		return durationValue(duration);
	}
	
	public static String durationValue(Duration duration) {
		long minutes = duration.toMinutes();
		long seconds = duration.getSeconds() - (60 * duration.toMinutes());
		long millis = duration.toMillis() - (1000 * duration.getSeconds());				
				
		if(minutes > 0)
			return String.format("%d m %d.%03d s", minutes, seconds, millis);
		return String.format("%d.%03d s", seconds, millis);	
	}
	
	public static double duration(LocalDateTime start, LocalDateTime end) {
		return (Duration.between(start, end).toMillis() * 1.0) / 1000.0;
	}
	
	public static String formatDateTime(LocalDateTime dateTime) {
		return dateTime.format(dateTimeFormatter);
	}
	
	public static String formatDateTimeWOYear(LocalDateTime dateTime) {
		return dateTime.format(dateTimeWOYearFormatter);
	}
	
	public static String formatDateWOYear(LocalDateTime dateTime) {
		return dateTime.format(dateWOYearFormatter);
	}
	
	public static String formatTime(LocalDateTime dateTime) {
		return dateTime.format(timeFormatter);
	}
	
	public static String formatTimeAMPM(LocalDateTime dateTime) {
		return dateTime.format(timeFormatterAmPm);
	}
	
	public static String formatTimeWithMillis(LocalDateTime dateTime) {
		return dateTime.format(timeWithMillisFormatter);
	}
	
	public static String formatTimeMillis(LocalDateTime dateTime) {
		return dateTime.format(millisFormatter);
	}
	
	public static String formatAMPM(LocalDateTime dateTime) {
		return dateTime.format(ampmFormatter);
	}
	
	public static String formatDateTimeWithMillis(LocalDateTime dateTime) {
		return dateTime.format(dateTimeWithMillisFormatter);
	}
	
	public static LocalDateTime convertToLocalDateTimeFromDate(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public static LocalDateTime convertToLocalDateTimeFromTimeStamp(String timestamp) {
		return ZonedDateTime.parse(timestamp).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
	}
}
