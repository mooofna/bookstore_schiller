package schiller.event;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.quantity.Quantity;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Component("EventDataInitializer")
@Order(20)
public class EventDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(EventDataInitializer.class);

	private final EventRepository eventRepository;

	EventDataInitializer(EventRepository eventRepository){
		Assert.notNull(eventRepository, "EventRepository must not be null!");
		this.eventRepository = eventRepository;
	}

	//TODO! add more events at different timeIntervals
	@Override
	public void initialize() {
		if(eventRepository.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default Event entries.");

		eventRepository.save(new Event("TestTitle", "TestSpeaker", "TestRoom",
			"TestDescription", "TestEvent.png",
			Quantity.of(100), LocalDateTime.of(2024, 3, 3, 1, 1)));
	}
}