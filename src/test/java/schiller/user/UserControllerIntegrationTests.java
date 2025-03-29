package schiller.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.util.Streamable;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class UserControllerIntegrationTests {

	private MockMvc mockMvc;

	@Mock
	private UserManagement userManagement;

	@Mock
	private Model model;

	@Mock
	private Streamable<Customer> customers;

	@Mock
	private Customer customer;

	@Mock
	private UserAccount userAccount;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}


	@Test
	void testRegisterNewUser() throws Exception {
		RegistrationForm form = new RegistrationForm("Willy", "lol@kekw.com", "password123!", "Lol City");
		form.setConfirmPassword("password123!");

		mockMvc.perform(post("/register").flashAttr("registrationForm", form))

			.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testAddNewEmployee() throws Exception {
		EmployeeForm form = new EmployeeForm("Mark Forster", "Ananas@uf.com");

		mockMvc.perform(post("/add_employee").flashAttr("employeeForm", form))
			.andExpect(status().is3xxRedirection());
	}



	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testEditEmployee() throws Exception {
		String employeeName = "Billy-Ray Sanguine";


		UserAccount mockUserAccount = mock(UserAccount.class);
		when(mockUserAccount.getEmail()).thenReturn("sanguine@email.com");


		User mockUser = mock(User.class);
		when(mockUser.getUserAccount()).thenReturn(mockUserAccount);


		when(userManagement.findByUsername(employeeName)).thenReturn(mockUser);

		mockMvc.perform(get("/edit_employee/{employeeName}", employeeName))
			.andExpect(status().isOk())
			.andExpect(view().name("edit_employee"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testEditingEmployee() throws Exception {
		String employeeName = "Billy-Ray Sanguine";
		EmployeeForm form = new EmployeeForm("Jane Doe", "jane.doe@example.com");

		UserAccount mockUserAccount = mock(UserAccount.class);
		when(mockUserAccount.getUsername()).thenReturn(employeeName);


		User mockUser = mock(User.class);
		when(mockUser.getUserAccount()).thenReturn(mockUserAccount);

		when(userManagement.findByUsername(employeeName)).thenReturn(mockUser);

		mockMvc.perform(post("/edit_employee/{employeeName}", employeeName).flashAttr("employeeForm", form))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testResetUserPassword(){
		String userId = "1";
		String viewNameWithoutCustomer = userController.resetUserPassword(userId, model);
		assertEquals("users", viewNameWithoutCustomer);
		when(userManagement.findCustomers()).thenReturn(customers);
		when(userManagement.findCustomerById(customers, userId)).thenReturn(Optional.of(customer));
		when(Optional.of(customer).get().getUserAccount()).thenReturn(userAccount);
		String viewName = userController.resetUserPassword(userId, model);
		assertEquals("redirect:/users",viewName);
	}

}