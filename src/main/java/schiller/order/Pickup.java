package schiller.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Pickup extends DeliveryMethod {

	@Id
	@Getter
	@Setter
	private Long id;

	private boolean pickedUp = false;

	public Pickup() {
		super("Pickup");
	}

	public String toString() {
		return "Pickup()";
	}

	public boolean equals(final Object o) {
        return o instanceof Pickup;
    }

	public int hashCode() {
        return super.hashCode();
	}

	public void pickUp() {
		pickedUp = true;
	}

	public boolean isPickedUp() {
		return pickedUp;
	}

	@Override
	public boolean readyForCompletion() {
		return pickedUp;
	}
}
