package schiller.user;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.validation.Errors;

/**
 * Form-backing class to accept input for {@link User}, so employee creation
 */

public class EmployeeForm {

	private final @NotEmpty String name, email, password;

	public EmployeeForm(String name, String email) {

		this.name = name;
		this.email = email;
		this.password = "123";
	}

	public String getName(){
		return name;
	}

	public String getEmail(){
		return email;
	}

	public String getPassword(){
		return password;
	}

	public void validate(Errors errors) {
		// stuff
	}

}