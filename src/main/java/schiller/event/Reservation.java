package schiller.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;

@Getter
@Entity
public class Reservation {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private final UserAccount userAccount;

	@Setter
	private Quantity quantity;

	Reservation(UserAccount userAccount, Quantity quantity) {
		if (quantity.isLessThan(Quantity.NONE)) {
			throw new IllegalArgumentException("Quantity must not be negative.");
		}

		this.userAccount = userAccount;
		this.quantity = quantity;
	}

	protected Reservation() {
		this.userAccount = null;
	}
}
