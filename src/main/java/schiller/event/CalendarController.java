package schiller.event;

import lombok.NonNull;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class CalendarController {

	static int SHOWN_NUMBER_OF_EVENTS = 3;

	private final @NonNull EventCalendar calendar;

	CalendarController(EventCalendar calendar) {
		this.calendar = calendar;
	}

	@GetMapping("/calendar_preview")
	public String viewCalendarPreview(Model model) {
		model.addAttribute("events", calendar.nextUpcomingEvents(SHOWN_NUMBER_OF_EVENTS));
		return "calendar";
	}

	@GetMapping("/calendar")
	public String viewCalendar(Model model) {
		model.addAttribute("events", calendar.nextUpcomingEvents());
		model.addAttribute("formatter", DateTimeFormatter.class);
		return "calendar";
	}

	@GetMapping("/calendar/event")
	public String viewEvent(Model model, @RequestParam Long eventId, @LoggedIn UserAccount userAccount) {
		Optional<Event> optionalEvent = calendar.findById(eventId);
		return optionalEvent.map(event -> {
			model.addAttribute("event", event);
			model.addAttribute("userAccount", userAccount);
			return "calendar_event";
		}).orElse("custom_error");
	}

	@PostMapping("/calendar/event/reserve")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String makeReservation(Model model,
								  @LoggedIn UserAccount userAccount,
								  @RequestParam Long eventId,
								  @RequestParam int numberOfSeats) {
		if (!calendar.makeReservation(eventId, userAccount, Quantity.of(numberOfSeats))) {
			model.addAttribute("failedReservation", true);
			return viewEvent(model, eventId, userAccount);
		}
		return viewEvent(model, eventId, userAccount);
	}

	@GetMapping("/reservations")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String viewReservations(Model model, @LoggedIn UserAccount userAccount) {
		model.addAttribute("events", calendar.findByUserAccount(userAccount));
		model.addAttribute("userAccount", userAccount);
		model.addAttribute("formatter", DateTimeFormatter.class);
		return "reservations";
	}

	@PostMapping("/reservations/update")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String updateReservation(Model model,
									@LoggedIn UserAccount userAccount,
									@RequestParam Long eventId,
									@RequestParam int numberOfSeats) {
		if (!calendar.updateReservation(eventId, userAccount, Quantity.of(numberOfSeats))) {
			model.addAttribute("failedReservation", true);
			return viewEvent(model, eventId, userAccount);
		}
		return viewEvent(model, eventId, userAccount);
	}

	@PostMapping("/reservations/cancel")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String cancelReservation(Model model, @LoggedIn UserAccount userAccount, @RequestParam Long eventId) {
		if (!calendar.cancelReservation(eventId, userAccount)) {
			return "custom_error";
		}
		return viewEvent(model, eventId, userAccount);
	}
}