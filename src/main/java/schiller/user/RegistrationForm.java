package schiller.user;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.Errors;

/**
 * Form-backing class to accept input for {@link Customer} registration
 */

@Getter
public class RegistrationForm {

	private final @NotEmpty String name;
	private final @NotEmpty String email;
	private final @NotEmpty String password;
	private final @NotEmpty String address;
	@Setter
	private String confirmPassword;

	public RegistrationForm(String name, String email, String password, String address) {

		this.name = name;
		this.email = email;
		this.password = password;
		this.address = address;
	}

	public void validate(Errors errors) {
		if (!password.equals(confirmPassword)) {
			errors.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
		}
	}

}