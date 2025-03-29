package schiller.user;

import jakarta.validation.Valid;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import schiller.order.OrderManagementService;
import schiller.order.SchillerOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
class UserController {

	private final UserManagement userManagement;
	private final OrderManagementService orderManagementService;

	UserController(UserManagement userManagement, OrderManagementService orderManagementService) {
		Assert.notNull(userManagement, "UserManagement must not be null!");

		this.userManagement = userManagement;
		this.orderManagementService = orderManagementService;
	}

	@GetMapping("/register")
	String register(Model model, RegistrationForm form, BindingResult result){

		return "register";
	}

	// Valid checks whether input from browser (binding result) is invalid
	// Validation is triggered on the RegistrationForm
	// Errors result captures those errors, if there are any

	@PostMapping("/register")
	String registerNewUser(@Valid RegistrationForm form, BindingResult result, Model model) {

		form.validate(result);
		if(result.hasErrors()){
			return "register";
		}

		if(userManagement.nameAlreadyExists(form)){
			model.addAttribute("username_error", "This username has already been taken!");
			return "register";
		}

		if(userManagement.userEmailTaken(form)){
			model.addAttribute("email_error", "This email has already been taken!");
			return "register";
		}

		if(userManagement.checkIfPasswordWeak(form)) {
			model.addAttribute("password_weak",
				"Password must be at least 8 characters long, contain 1 number and 1 special character!");
			return "register";
		}

		userManagement.createUser(form);

		return "redirect:/login";
	}

	@GetMapping("/add_employee")
	@PreAuthorize("hasRole('BOSS')")
	public String add_employee(Model model, EmployeeForm form) {
		return "add_employee";
	}

	@PostMapping("/add_employee")
	@PreAuthorize("hasRole('BOSS')")
	String addNewEmployee(@Valid EmployeeForm form, Errors result, Model model) {

		boolean hasFormErrors = result.hasErrors();
		boolean employeeExists = !hasFormErrors && userManagement.employeeAlreadyExists(form);
		boolean emailTaken = !hasFormErrors && !employeeExists && userManagement.employeeEmailTaken(form);

		if (hasFormErrors) {
			return "add_employee";
		} else if (employeeExists) {
			model.addAttribute("username_error", "This employee already exists!");
		} else if (emailTaken) {
			model.addAttribute("email_error", "This email has already been taken!");
		} else {
			userManagement.createEmployee(form);
			return "redirect:/";
		}

		return "add_employee";
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('BOSS')")
	String users(Model model) {
		Streamable<User> users = userManagement.findAll();
		Map<UserAccount, Integer> userOpenOrdersCount = new HashMap<>();

		for (User user : users) {
			List<SchillerOrder> orders = orderManagementService.getOpenedOrdersByUser(user.getUserAccount());
			userOpenOrdersCount.put(user.getUserAccount(), orders.size());
		}

		model.addAttribute("userlist", users);
		model.addAttribute("userOpenOrdersCount", userOpenOrdersCount);

		return "users";
	}

	@GetMapping("/users/openOrders")
	@PreAuthorize("hasRole('BOSS')")
	String showUsersWithOpenOrders(Model model) {
		Streamable<User> allUsers = userManagement.findAll();
		Map<UserAccount, Integer> userOpenOrdersCount = new HashMap<>();

		for (User user : allUsers) {
			List<SchillerOrder> orders = orderManagementService.getOpenedOrdersByUser(user.getUserAccount());
			userOpenOrdersCount.put(user.getUserAccount(), orders.size());
		}

		Streamable<User> users = allUsers.filter(user -> {
			List<SchillerOrder> orders = orderManagementService.getOpenedOrdersByUser(user.getUserAccount());
			return !orders.isEmpty();
		});

		model.addAttribute("userlist", users);
		model.addAttribute("userOpenOrdersCount", userOpenOrdersCount);

		return "users";
	}

	@PostMapping("/users/resetPassword")
	@PreAuthorize("hasRole('BOSS')")
	String resetUserPassword(@RequestParam("userId") String userId, Model model){
		Streamable<Customer> customers = userManagement.findCustomers();
		Optional<Customer> customer = userManagement.findCustomerById(customers, userId);
		if (customer.isEmpty()) {
			return "users";
		}
		model.addAttribute("userlist", userManagement.findAll());
		UserAccount userAccount = customer.get().getUserAccount();
		RegistrationForm registrationForm = new RegistrationForm(userAccount.getUsername()
			,userAccount.getEmail()
			,"123"
			, customer.get().getAddress());
		userManagement.updateCustomer(registrationForm,customer.get());
		return "redirect:/users";
	}


	@GetMapping("/account")
	@PreAuthorize("hasRole('CUSTOMER')")
	String account(@LoggedIn Optional<UserAccount> userAccount, Model model) {


		if (userAccount.isPresent()) {

			var account = userAccount.get();
			String username = account.getUsername();

			Customer user = userManagement.findCustomers().filter(
				customer -> {
					return customer.getUserAccount().equals(account);
				}
			).stream().findFirst().get();

			model.addAttribute("username", username);
			model.addAttribute("address", user.getAddress());
			model.addAttribute("email", user.getUserAccount().getEmail());
			model.addAttribute("status", false);
			model.addAttribute("status_msg", "");

			return "account";
		}

		return "login";
	}

	@GetMapping("/edit_account")
	@PreAuthorize("hasRole('CUSTOMER')")
	String edit_account(Model model, @LoggedIn Optional<UserAccount> userAccount, RegistrationForm form){

		model.addAttribute("registrationForm", form);

		if(userAccount.isPresent()){

			var account = userAccount.get();
			String username = account.getUsername();

			Customer user = userManagement.findCustomers().filter(
				customer -> {
					return customer.getUserAccount().equals(account);
				}
			).stream().findFirst().get();

			model.addAttribute("username", username);
			model.addAttribute("address", user.getAddress());
			model.addAttribute("email", user.getUserAccount().getEmail());

		}

		return "edit_account";
	}

	@PostMapping("/edit_account")
	@PreAuthorize("hasRole('CUSTOMER')")
	String editing_account(Model model, @LoggedIn Optional<UserAccount> userAccount,
						   @Valid RegistrationForm form, Errors result){

		model.addAttribute("registrationForm", form);

		if(result.hasErrors()){
			return "edit_account";
		}

		if(userAccount.isPresent()){

			var account = userAccount.get();
			String username = account.getUsername();

			Customer user = userManagement.findCustomers().filter(
				customer -> {
					return customer.getUserAccount().equals(account);
				}
			).stream().findFirst().get();

			model.addAttribute("username", username);
			model.addAttribute("address", user.getAddress());
			model.addAttribute("email", user.getUserAccount().getEmail());

			userManagement.updateCustomer(form, user);
		}
		return "index";
	}

	@GetMapping("/edit_employee/{employeeName}")
	@PreAuthorize("hasRole('BOSS')")
	String edit_employee(@PathVariable String employeeName, Model model, EmployeeForm form) {

		User employee = userManagement.findByUsername(employeeName);

		model.addAttribute("name", employeeName);
		model.addAttribute("email", employee.getUserAccount().getEmail());
		model.addAttribute("registrationForm", form);
		model.addAttribute("employee", employee);

		return "edit_employee";
	}

	@PostMapping("/edit_employee/{employeeName}")
	@PreAuthorize("hasRole('BOSS')")
	String editing_employee(@PathVariable String employeeName, Model model, EmployeeForm form) {

		User employee = userManagement.findByUsername(employeeName);

		model.addAttribute("employee", employee);
		model.addAttribute("name", employee.getUserAccount().getUsername());
		model.addAttribute("email", employee.getUserAccount().getEmail());
		model.addAttribute("employeeForm", form);

		userManagement.updateEmployee(form, employee);

		return "redirect:/";
	}

}