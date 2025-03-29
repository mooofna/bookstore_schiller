package schiller.event;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.salespointframework.quantity.Quantity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Setter
@Getter
public class EventForm {

	private String title, speaker, room, description;

	private MultipartFile image;

	// Date is required as DD.MM.YYYY
	// time is required as HH:MM (or if possible H:MM)
	public String date, time;

	private int capacity;

	public static EventForm fromEvent(Event event) {
		EventForm form = new EventForm();
		form.setTitle(event.getTitle());
		form.setSpeaker(event.getSpeaker());
		form.setRoom(event.getRoom());
		form.setDescription(event.getDescription());
		form.setDate(event.getTime().toLocalDate().toString());
		form.setTime(DateTimeFormatter.formatTime(event.getTime()));
		form.setCapacity(event.getCapacity().getAmount().intValue());
		return form;
	}

	public Optional<Event> toEvent() {
		if (!isValid()) {
			return Optional.empty();
		}
		return Optional.of(new Event(title,
			speaker,
			room,
			description,
			null,
			Quantity.of(capacity),
			getDateTime().get()));
	}

	public Optional<LocalDateTime> getDateTime() {
		Optional<Date> optionalDate = Date.fromString(date);
		if (optionalDate.isEmpty()) {
			return Optional.empty();
		}
		Date date = optionalDate.get();

		Optional<Time> optionalTime = Time.fromString(time);
		if (optionalTime.isEmpty()) {
			return Optional.empty();
		}
		Time time = optionalTime.get();

		try {
			return Optional.of(LocalDateTime.of(
				date.getYear(),
				date.getMonth(),
				date.getDay(),
				time.getHour(),
				time.getMinute()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public Optional<Quantity> getCapacityQuantity() {
		if (capacity >= 0) {
			return Optional.of(Quantity.of(capacity));
		}
		return Optional.empty();
	}

	public boolean isValid(Event event) {
		return getCapacityQuantity().map(quantity ->
			!quantity.isLessThan(event.getOccupiedSeats()) && isValid()
		).orElse(false);
	}

	public boolean isValid() {
		boolean valid = true;
		if (getCapacityQuantity().isEmpty() || getDateTime().isEmpty()) {
			valid = false;
		} else if (title == null || title.isEmpty()) {
			valid = false;
		} else if (speaker == null || speaker.isEmpty()) {
			valid = false;
		} else if (room == null || room.isEmpty()) {
			valid = false;
		} else if (description == null || description.isEmpty()) {
			valid = false;
		}
		return valid;
	}
}