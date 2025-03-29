package schiller.order;

import com.mysema.commons.lang.Assert;
import jakarta.persistence.*;
import lombok.Getter;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;

@Entity
public class SchillerOrder extends Order {

	@OneToOne(cascade = CascadeType.ALL)
	private final DeliveryMethod deliveryMethod;

	@Getter
	private String userName = "";

	private OrderStatus status;

	public SchillerOrder(UserAccount.UserAccountIdentifier userAccountIdentifier,
						 PaymentMethod paymentMethod,
						 DeliveryMethod deliveryMethod) {
		super(userAccountIdentifier, paymentMethod);

		Assert.notNull(deliveryMethod, "Delivery Method must not be null.");
		this.deliveryMethod = deliveryMethod;
	}

	public SchillerOrder() {
		super();
		deliveryMethod = null;
	}

	public DeliveryMethod getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setUserName(String name) {
		userName = (name == null || name.trim().isEmpty()) ? String.valueOf(this.getUserAccountIdentifier()) : name;
	}

	public OrderStatus getStatus() {
		return this.status;
	}
}
