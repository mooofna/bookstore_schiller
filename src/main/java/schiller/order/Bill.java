package schiller.order;

import com.mysema.commons.lang.Assert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.salespointframework.payment.PaymentMethod;

import javax.annotation.processing.Generated;

@Entity
public class Bill extends PaymentMethod {

	@Getter
	private final String billingAddress;

	private boolean sent;

	@Id
	@GeneratedValue
	@Getter
	private Long id;

	public Bill(String billingAddress) {
		super("Payment by Bill");
		Assert.notNull(billingAddress, "Billing address must not be null.");
		if (billingAddress.isEmpty()) {
			throw new IllegalArgumentException("Billing address must not be empty.");
		}

		this.billingAddress = billingAddress;
		this.sent = false;
	}

	public Bill() {
		super("");
		billingAddress = "";
		sent = false;
	}

	public String toString() {
		return "Bill()";
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isSent() {
		return this.sent;
	}

	public void send() {
		this.sent = true;
	}
}
