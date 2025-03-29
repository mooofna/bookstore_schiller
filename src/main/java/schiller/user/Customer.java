package schiller.user;

import jakarta.persistence.Entity;
import org.salespointframework.useraccount.UserAccount;

/**
 * Represents Customers of the website
 */

@Entity
public class Customer extends User {

	@SuppressWarnings("unused")
	public Customer() {
	}

	private String address;

	public Customer(UserAccount userAccount, String address) {
		super(userAccount);
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
