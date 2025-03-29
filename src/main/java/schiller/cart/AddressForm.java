package schiller.cart;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.Errors;

public class AddressForm {

	private final @NotNull String street, houseNumber, postCode, district;

	AddressForm(String street, String houseNumber, String postCode, String district) {
		this.street = street;
		this.houseNumber = houseNumber;
		this.postCode = postCode;
		this.district = district;
	}

	public String compressAddress() {
		return street + ' ' + houseNumber + ", " + postCode + ", " + district;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getDistrict() {
		return district;
	}

	public void validate(Errors errors) {
		// Address validation goes here

	}
}
