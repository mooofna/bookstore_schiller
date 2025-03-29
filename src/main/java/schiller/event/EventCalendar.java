package schiller.event;

import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import schiller.inventory.storage.StorageService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EventCalendar {

	private final EventRepository eventRepository;

	private final BusinessTime businessTime;

	private final StorageService storageService;

	EventCalendar(EventRepository eventRepository, BusinessTime businessTime, StorageService storageService){
		this.eventRepository = eventRepository;
		this.businessTime = businessTime;
		this.storageService = storageService;
	}

	public List<Event> nextUpcomingEvents() {
		return eventRepository.findAll().stream()
			.filter(event -> event.getTime().isAfter(businessTime.getTime()))
			.sorted(Comparator.comparing(Event::getTime)).toList();
	}

	public List<Event> nextUpcomingEvents(int numberOfEvents) {
		Assert.isTrue(numberOfEvents > 0, "Number of Events must be larger than 0.");

		return nextUpcomingEvents().stream().limit(numberOfEvents).toList();
	}

	public boolean makeReservation(Long Id, UserAccount userAccount, Quantity numberOfSeats) {
		Assert.notNull(Id, "Id must not be null.");
		Assert.notNull(userAccount, "UserAccount must not be null.");
		Assert.notNull(numberOfSeats, "Quantity must not be null.");

		if(eventRepository.findById(Id).isEmpty()){
			return false;
		}

		Event event = eventRepository.findById(Id).get();
		if (event.addReservation(userAccount, numberOfSeats)) {
			eventRepository.save(event);
			return true;
		}
		return false;
	}

	public boolean updateReservation(Long Id, UserAccount userAccount, Quantity numberOfSeats) {
		Assert.notNull(Id, "Id must not be null.");
		Assert.notNull(userAccount, "UserAccount must not be null.");
		Assert.notNull(numberOfSeats, "Quantity must not be null.");

		if(eventRepository.findById(Id).isEmpty()){
			return false;
		}

		Event event = eventRepository.findById(Id).get();
		if (event.updateReservation(userAccount, numberOfSeats)) {
			eventRepository.save(event);
			return true;
		}
		return false;
	}

	public boolean cancelReservation(Long Id, UserAccount userAccount) {
		Assert.notNull(Id, "Id must not be null.");
		Assert.notNull(userAccount, "UserAccount must not be null.");

		if (eventRepository.findById(Id).isEmpty()){
			return false;
		}

		Event event = eventRepository.findById(Id).get();
		if (event.cancelReservation(userAccount)) {
			eventRepository.delete(event);
			return true;
		}
		return false;
	}

	public boolean addEvent(EventForm eventForm) {
		Assert.notNull(eventForm, "Eventform must not be null.");
		Optional<Event> optionalEvent = eventForm.toEvent();
		if (optionalEvent.isEmpty()) {
			return false;
		}
		Event event = optionalEvent.get();
		if (!eventForm.getImage().getOriginalFilename().isEmpty()) {
			String fileName = storageService.store(eventForm.getImage());
			event.setImage(fileName);
		}
		eventRepository.save(event);
		return true;
	}

	public boolean updateEvent(Long eventId, EventForm eventForm) {
		Optional<Event> optionalEvent = eventRepository.findById(eventId);
		Event event;
		if (optionalEvent.isPresent()) {
			event = optionalEvent.get();
		} else {
			throw new IllegalArgumentException("Event Id does not exist.");
		}
		if (eventForm.isValid(event)) {
			event.update(eventForm);
			if (!eventForm.getImage().getOriginalFilename().isEmpty()) {
				String fileName = storageService.store(eventForm.getImage());
				event.setImage(fileName);
			}
			eventRepository.save(event);
			return true;
		}
		return false;
	}

	public void deleteEvent(Long eventId) {
		Optional<Event> optionalEvent = eventRepository.findById(eventId);
		Event event;
		if (optionalEvent.isPresent()) {
			event = optionalEvent.get();
		} else {
			throw new IllegalArgumentException("Event Id does not exist.");
		}
		eventRepository.delete(event);
	}

	public List<Event> findByUserAccount(UserAccount userAccount){
		Assert.notNull(userAccount, "UserAccount must not be null.");
		return eventRepository.findAll().stream()
			.filter(event ->
				event.getUserReservation(userAccount).isPresent()
			).sorted(Comparator.comparing(Event::getTime)).toList();
	}

	public Optional<Event> findById(Long Id){
		Assert.notNull(Id, "ID must not be null.");
		return eventRepository.findById(Id);
	}

}