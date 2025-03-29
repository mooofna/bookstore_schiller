package schiller.event;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class Time {
	private int hour, minute;

	public static Optional<Time> fromString(String timeString) {
		String[] timeSegments = timeString.split(":");
		if (timeSegments.length != 2) {
			return Optional.empty();
		}
		try {
			Time time = new Time();
			time.setHour(Integer.parseInt(timeSegments[0]));
			time.setMinute(Integer.parseInt(timeSegments[1]));
			return Optional.of(time);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}