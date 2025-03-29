package schiller.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import schiller.AbstractIntegrationTests;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.user.RegistrationForm;
import schiller.user.UserManagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class CartControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	CartManagementController cartManagementController;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookCatalog bookCatalog;

	@Autowired
	private UserManagement userManagement;

	@BeforeEach
	void setup() {
		userManagement.createUser(
			new RegistrationForm("user", "test@mail.com", "123", "SomeWhere")
		);
	}

	@Test
	void testCartViewAndModelAttributes() throws Exception {
		MvcResult result = mockMvc.perform(get("/cart"))
			.andExpect(status().isOk())
			.andExpect(view().name("cart"))
			.andReturn();

		ModelMap model = result.getModelAndView().getModelMap();

		assertThat(model).containsKey("availability");
	}

	@Test
	void testAddOrUpdateProductInCart() throws Exception {
		Book book = bookCatalog.findAll().iterator().next();

		mockMvc.perform(post("/cart/addOrUpdate")
				.param("productId", String.valueOf(book.getId()))
				.param("amount", "1")
				.param("onCartSite", "false"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/catalog"));
	}

	@Test
	void testDeleteProductFromCart() throws Exception {
		Book book = bookCatalog.findAll().iterator().next();

		mockMvc.perform(post("/cart/deleteItem")
				.param("pid", String.valueOf(book.getId())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/cart"));
	}

	@Test
	@WithMockUser(username = "user", roles = "CUSTOMER")
	void testCheckout() throws Exception {
		mockMvc.perform(post("/cart/checkout")
				.param("OrderType", "Pickup"))
			.andExpect(status().isOk())
			.andExpect(view().name("thank_you"));
	}

	@Test
	@WithMockUser(username = "user", roles = "CUSTOMER")
	void testDeliveryCheckout() throws Exception {
		mockMvc.perform(post("/cart/checkout/delivery")
				.param("addressChoice", "default")
				.flashAttr("billingAddress", "123 Main St"))
			.andExpect(status().isOk())
			.andExpect(view().name("thank_you"));
	}

	@Test
	void testEmptyCart() throws Exception {
		mockMvc.perform(post("/cart/empty_cart"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/cart"));
	}

}
