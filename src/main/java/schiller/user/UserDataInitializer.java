package schiller.user;

import java.util.List;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.UserAccountManagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

// Order(10) specifies that this component should be addressed by Spring later in the build up process,
// it does not have the highest priority
@Component
@Order(10)
public class UserDataInitializer implements DataInitializer {

	// logger is there to record messages, warnings and errors during execution of the application
	// diagnoses issues in an application
	private static final Logger LOG = LoggerFactory.getLogger(UserDataInitializer.class);

	private final UserAccountManagement userAccountManagement;

	private final UserManagement userManagement;

	UserDataInitializer(UserAccountManagement userAccountManagement, UserManagement userManagement) {
		Assert.notNull(userAccountManagement, "UserAccountManagement must not be null!");
		Assert.notNull(userManagement, "UserManagement must not be null!");

		this.userAccountManagement = userAccountManagement;
		this.userManagement = userManagement;
	}

	@Override
	public void initialize() {

		// if the database has already been populated, this step is skipped
		if(userAccountManagement.findByUsername("boss").isPresent()) {
			return;
		}

		LOG.info("Creating default users and customer.");


		String password = "123";

		List.of(
			new RegistrationForm("boss", "boss@email.com", password, "Richie rich Street")
		).forEach(userManagement::createBoss);

		List.of(
			new RegistrationForm("Ghastly Bespoke", "bespoke@gmail.com", password, "Dublin"),
			new RegistrationForm("Valkyrie Cain", "cain@email.com", password, "Dublin"),
			new RegistrationForm("User", "user@email.com", password, "Anywhere"),
			new RegistrationForm("Skulduggery Pleasant", "pleasant@email.com", password, "Everywhere")
		).forEach(userManagement::createUser);

		List.of(
			new EmployeeForm("Billy-Ray Sanguine", "sanguine@email.com"),
			new EmployeeForm("China Sorrows", "sorrow@email.com")
		).forEach(userManagement::createEmployee);
	}
}