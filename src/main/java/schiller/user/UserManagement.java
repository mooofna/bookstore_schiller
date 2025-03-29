package schiller.user;

import jakarta.validation.Valid;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

// Transactional defines the scope of a database transaction
// transitions are automatically rolled back if anything goes wrong
@Service
@Transactional
public class UserManagement {

	private static final Logger LOG = LoggerFactory.getLogger(UserDataInitializer.class);
	public static final Role USER_ROLE = Role.of("USER");
	public static final Role BOSS_ROLE = Role.of("BOSS");
	public static final Role CUSTOMER_ROLE = Role.of("CUSTOMER");

	public static final Role EMPLOYEE_ROLE = Role.of("EMPLOYEE");

	private final UserRepository users;
	private final UserAccountManagement userAccounts;

	UserManagement(UserRepository users, UserAccountManagement userAccounts) {

		Assert.notNull(users, "UserRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null");

		this.users = users;
		this.userAccounts = userAccounts;
	}

	/**
	 * Creates {@link User} using {@link RegistrationForm} during registration
	 * @param form
	 * @return the new {@link Customer} instance
	 */

	public User createBoss(RegistrationForm form) {

		Assert.notNull(form, "RegistrationForm must not be null!");

		UnencryptedPassword password = UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getName(), password, form.getEmail(), BOSS_ROLE);

		return users.save(new Customer(userAccount, form.getAddress()));

	}

	public User createUser(RegistrationForm form) {

		Assert.notNull(form, "RegistrationForm must not be null!");

		UnencryptedPassword password = UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getName(), password, form.getEmail(), CUSTOMER_ROLE);

		return users.save(new Customer(userAccount, form.getAddress()));
	}


	/**
	 * Creates {@link User} using {@link EmployeeForm} to add employee to system
	 * @param form
	 * @return the new {@link User} instance
	 */

	public User createEmployee(EmployeeForm form) {

		Assert.notNull(form, "EmployeeForm must not be null!");

		UnencryptedPassword password = UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getName(), password, form.getEmail(), EMPLOYEE_ROLE);

		return users.save(new User(userAccount));
	}

	@Transactional
	public void updateCustomer(RegistrationForm form, Customer customer) {

		Assert.notNull(form, "RegistrationForm must not be null!");

		var password = UnencryptedPassword.of(form.getPassword());

		customer.getUserAccount().setUsername(form.getName());
		customer.getUserAccount().setEmail(form.getEmail());
		customer.setAddress(form.getAddress());

		userAccounts.changePassword(customer.getUserAccount(), password);
	}

	@Transactional
	public void updateEmployee(EmployeeForm form, User employee) {

		Assert.notNull(form, "EmployeeForm must not be null!");

		employee.getUserAccount().setUsername(form.getName());
		employee.getUserAccount().setEmail(form.getEmail());
	}

	public Streamable<User> findAll() {
		return users.findAll();
	}

	public Streamable<Customer> findCustomers() {
		return users.findUsersByUserAccount_Roles(CUSTOMER_ROLE);
	}

	public  Optional<Customer> findCustomerById(Streamable<Customer> customers, String customerId) {
		Optional<Customer> customer = Optional.empty();
		for (Customer customer1 : customers){
            assert customer1.getId() != null;
            if(customer1.getId().toString().equals(customerId)){
				customer= Optional.of(customer1);
			}
		}
		return customer;
	}

	public User findByUsername(String name) { return users.findUserByUserAccountUsernameContaining(name); }

	public boolean checkIfPasswordWeak(@Valid RegistrationForm form) {
		if (checkPasswordStrength(form) && checkPasswordLength(form)) {
			return false;
		}
		return true;
	}

	private boolean checkPasswordLength(@Valid RegistrationForm form) {
		int minimumLength = 8;
		int passwordLength = form.getPassword().length();
		return passwordLength >= minimumLength;
	}

	private boolean checkPasswordStrength(@Valid RegistrationForm form) {
		String password = form.getPassword();
		return checkNumbers(password) && checkSpecialCharacters(password);
	}

	private boolean checkNumbers(String string) {
		for (char c : string.toCharArray()) {
			if (Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkSpecialCharacters(String string) {
		Set<Character> specialChars = new HashSet<>(Arrays.asList('!', '"', '$', '%', '&', '/', '=', '?', '+', '*', '#' ));

		for (char c : string.toCharArray()) {
			if (specialChars.contains(c)){
				return true;
			}
		}
		return false;
	}

	public boolean nameAlreadyExists(@Valid RegistrationForm form) {
		Optional<UserAccount> userName = userAccounts.findByUsername(form.getName());
		return userName.isPresent();
	}

	public boolean employeeAlreadyExists(@Valid EmployeeForm form) {
		Optional<UserAccount> userName = userAccounts.findByUsername(form.getName());
		return userName.isPresent();
	}

	public boolean userEmailTaken(@Valid RegistrationForm form) {
		Optional<User> user = Optional.ofNullable(users.findUserByUserAccount_Email(form.getEmail()));
        return user.isPresent();
    }

	public boolean employeeEmailTaken(@Valid EmployeeForm form) {
		Optional<User> employee = Optional.ofNullable(users.findUserByUserAccount_Email(form.getEmail()));
		return employee.isPresent();
	}

	public boolean validateEmail(String email) {
		return email.contains("@");
	}

}