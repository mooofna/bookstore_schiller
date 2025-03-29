package schiller.event;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class Date {
	private int day, month, year;

	// String is expected in the following format: YYYY-MM-DD
	public static Optional<Date> fromString(String dateString) {
		if (dateString == null) {
			return Optional.empty();
		}

		String[] dateSegments = dateString.split("-");
		int year = Integer.parseInt(dateSegments[0]);
		int month = Integer.parseInt(dateSegments[1]);
		int day = Integer.parseInt(dateSegments[2]);

		Date date = new Date();
		date.setDay(day);
		date.setMonth(month);
		date.setYear(year);
		return Optional.of(date);
	}
}

