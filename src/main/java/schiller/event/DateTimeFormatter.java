package schiller.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

public class DateTimeFormatter {
	// formats date into: DD.MM.YYYY
	public static String formatDate(LocalDateTime date) {
		return date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear();
	}

	// formats time into: HH:MM
	public static String formatTime(LocalDateTime time) {
		int hour = time.getHour();
		int minute = time.getMinute();
		return ((hour < 10) ? "0" + hour : hour) + ":" + ((minute < 10) ? "0" + minute : minute);
	}
}
