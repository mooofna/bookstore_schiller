package schiller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for security setup.
 */
@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityIntegrationTests {

	@Autowired
	MockMvc mvc;

	/**
	 * Trying to access a secured resource should result in a redirect to the login page.
	 */
	@Test
	void redirectsToLoginPageForSecuredResource() throws Exception {

		mvc.perform(get("/orders")) //
			.andExpect(status().isFound()) //
			.andExpect(header().string("Location", endsWith("/login")));
	}

	/**
	 * Trying to access the orders as boss should result in the page being rendered.
	 */
	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void returnsModelAndViewForSecuredUriAfterAuthentication() throws Exception {

		mvc.perform(get("/orders"))
			.andExpect(status().isOk())
			.andExpect(view().name("orders"));
	}

	/**
	 * Trying to access the orders as user should result in a 403.
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void testOrdersEndpointAsNonBossUser() throws Exception {

		mvc.perform(get("/orders")) //
			.andExpect(status().isForbidden());
	}

	/**
	 * Trying to access the inventory as user should result in a 403.
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void testInventoryEndpointAsNonBossUser() throws Exception {

		mvc.perform(get("/inventory")) //
			.andExpect(status().isForbidden());
	}

	/**
	 * Trying to access the add_employee as user should result in a 403.
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void testAddEmployeeEndpointAsNonBossUser() throws Exception {

		mvc.perform(get("/add_employee")) //
			.andExpect(status().isForbidden());
	}

	/**
	 * Trying to access the add_employee as boss should result in the page being rendered.
	 */
	@WithMockUser(username = "user", roles = "USER")
	void testUsersEndpointAsNonBossUser() throws Exception {

		mvc.perform(get("/users")) //
			.andExpect(status().isForbidden());
	}
}