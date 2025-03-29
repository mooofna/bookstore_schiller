package schiller.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.Objects;

@Entity
public class Delivery extends DeliveryMethod {

	private final String shippingAddress;

	private DeliveryStatus deliveryStatus;

	@Id
	@Getter
	@Setter
	private Long id;

	public Delivery(String shippingAddress) {
		super("Shipping");
		Assert.notNull(shippingAddress, "Shipping Address must not be Null.");
		this.shippingAddress = shippingAddress;
		deliveryStatus = DeliveryStatus.READY_FOR_SHIPPING;
	}

	public Delivery() {
		shippingAddress = "";
	}

	public String toString() {
		return "Delivery()";
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Delivery)) {
			return false;
		} else {
			Delivery other = (Delivery)o;
			if (!other.canEqual(this)) {
				return false;
			} else {
				return super.equals(o) &&
					(Objects.equals(this.shippingAddress, other.shippingAddress));
			}
		}
	}

	protected boolean canEqual(final Object other) {
		return other instanceof Delivery;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((shippingAddress == null) ? 0 : shippingAddress.hashCode());
		return result;
	}

	public String getShippingAddress() {
		return this.shippingAddress;
	}

	public DeliveryStatus getDeliveryStatus() {
		return this.deliveryStatus;
	}

	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		Assert.notNull(deliveryStatus, "Delivery Status must not be null.");
		this.deliveryStatus = deliveryStatus;
	}

	@Override
	public boolean readyForCompletion() {
		return deliveryStatus.equals(DeliveryStatus.DELIVERED);
	}

	public enum DeliveryStatus {
		READY_FOR_SHIPPING,
		SHIPPED,
		DELIVERED;
	}
}
