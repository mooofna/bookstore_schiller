package schiller.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Getter
@Entity
public class Event {

	@Id
	@GeneratedValue
	private Long id;

	@Setter
	@NotEmpty
	private String title, speaker, room;

	@Setter
	@NotEmpty
	@Lob
	private String description;

	@Setter
	private String image;

	private Quantity capacity;

	@Setter
	@OneToMany(cascade = CascadeType.ALL)
	private List<Reservation> reservations;

	@Setter
	private LocalDateTime time;

	Event(String title, String speaker, String room, String description, String image,
		  Quantity capacity, LocalDateTime time) {
		if (capacity.isLessThan(Quantity.NONE)) {
			throw new IllegalArgumentException("Quantity must not be negative.");
		}

		this.title = title;
		this.speaker = speaker;
		this.room = room;
		this.description = description;
		this.image = image;
		this.capacity = capacity;
		this.time = time;

		this.reservations = new ArrayList<>();
	}

	protected Event() {

	}

	public void update(EventForm eventForm) {
		Assert.isTrue(eventForm.isValid(), "Given Event form was not valid!");
		setTitle(eventForm.getTitle());
		setSpeaker(eventForm.getSpeaker());
		setRoom(eventForm.getRoom());
		setDescription(eventForm.getDescription());
		setCapacity(eventForm.getCapacityQuantity().get());
		setTime(eventForm.getDateTime().get());
	}

	public void setCapacity(Quantity capacity) {
		if (!getOccupiedSeats().isGreaterThan(capacity)) {
			this.capacity = capacity;
		}
	}

	public Quantity getOccupiedSeats() {
		Quantity occupiedSeats = Quantity.NONE;
		for (Reservation reservation : reservations) {
			occupiedSeats = occupiedSeats.add(reservation.getQuantity());
		}
		return occupiedSeats;
	}

	public Quantity getRemainingSeats() {
		return capacity.subtract(getOccupiedSeats());
	}

	public boolean addReservation(UserAccount userAccount, Quantity numberOfSeats) {
		if (reservations.stream().anyMatch(reservation -> reservation.getUserAccount().equals(userAccount))) {
			return false;
		}
		if (getOccupiedSeats().add(numberOfSeats).isGreaterThan(capacity)) {
			return false;
		}
		reservations.add(new Reservation(userAccount, numberOfSeats));
		return true;
	}

	public boolean updateReservation(UserAccount userAccount, Quantity numberOfSeats) {
		Optional<Reservation> reservation = reservations.stream()
			.filter(res -> res.getUserAccount().equals(userAccount)).findFirst();
		return reservation.map(res -> {
			Quantity newTotal = getOccupiedSeats().subtract(res.getQuantity()).add(numberOfSeats);
			if (newTotal.isGreaterThan(capacity)) {
				return false;
			}
			res.setQuantity(numberOfSeats);
			return true;
		}).orElse(false);
	}

	public boolean cancelReservation(UserAccount userAccount) {
		Optional<Reservation> reservation = reservations.stream()
			.filter(res -> res.getUserAccount().equals(userAccount)).findFirst();
		return reservation.map(res -> reservations.remove(res)).orElse(false);
	}

	public Optional<Reservation> getUserReservation(UserAccount userAccount) {
		return reservations.stream()
			.filter(res -> res.getUserAccount().equals(userAccount)).findFirst();
	}
}