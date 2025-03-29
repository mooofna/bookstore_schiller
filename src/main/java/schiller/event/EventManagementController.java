package schiller.event;

import lombok.NonNull;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class EventManagementController {

	private final @NonNull EventCalendar calendar;

	EventManagementController(EventCalendar calendar) {
		this.calendar = calendar;
	}

	@GetMapping("/events")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String eventOverview(Model model) {
		model.addAttribute("events", calendar.nextUpcomingEvents());
		model.addAttribute("formatter", DateTimeFormatter.class);
		return "events";
	}

	@GetMapping("/events/new")
	@PreAuthorize("hasRole('BOSS')")
	public String toEventCreationPage(Model model) {
		model.addAttribute("eventForm", new EventForm());
		return "new_event";
	}

	@PostMapping("/events/create")
	@PreAuthorize("hasRole('BOSS')")
	public String createEvent(Model model, EventForm eventForm) {
		if (calendar.addEvent(eventForm)) {
			return "redirect:/events";
		}
		model.addAttribute("error_msg",
			"Please check your inputs again, the event could not be created.");
		return "new_event";
	}

	@GetMapping("/events/view")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String viewEvent(Model model, @RequestParam Long eventId, @RequestParam boolean updatable) {
		Event event = calendar.findById(eventId).get();
		model.addAttribute("event", event);
		model.addAttribute("updateMode", updatable);
		model.addAttribute("formatter", DateTimeFormatter.class);
		model.addAttribute("eventForm", EventForm.fromEvent(event));
		return "event_details";
	}

	@PostMapping("/events/update")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String updateEvent(Model model, @RequestParam Long eventId, EventForm eventForm) {
		if (calendar.updateEvent(eventId, eventForm)) {
			model.addAttribute("changeSuccessful", true);
			return viewEvent(model, eventId, false);
		}
		model.addAttribute("changeFailed", true);
		return viewEvent(model, eventId, true);
	}

	@PostMapping("/events/delete_reservation")
	@PreAuthorize("hasRole('BOSS')")
	public String deleteReservation(Model model, @RequestParam Long eventId, @RequestParam UserAccount userAccount) {
		Event event = calendar.findById(eventId).get();
		event.cancelReservation(userAccount);
		return viewEvent(model, eventId, false);
	}

	@PostMapping("/events/delete")
	@PreAuthorize("hasRole('BOSS')")
	public String deleteEvent(@RequestParam Long eventId) {
		calendar.deleteEvent(eventId);
		return "redirect:/events";
	}
}