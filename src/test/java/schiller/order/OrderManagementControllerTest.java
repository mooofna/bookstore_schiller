package schiller.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import schiller.AbstractIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class OrderManagementControllerTest extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderManagementService orderManagementService;

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testCompletedOrdersViewAndModelAttributes() throws Exception {
		MvcResult result = mockMvc.perform(get("/orders/completed"))
			.andExpect(status().isOk())
			.andExpect(view().name("orders"))
			.andReturn();

		ModelMap model = result.getModelAndView().getModelMap();

		assertThat(model).containsKey("orders");
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testOpenedOrdersViewAndModelAttributes() throws Exception {
		MvcResult result = mockMvc.perform(get("/orders/open"))
			.andExpect(status().isOk())
			.andExpect(view().name("orders"))
			.andReturn();

		ModelMap model = result.getModelAndView().getModelMap();

		assertThat(model).containsKey("orders");
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testMarkCashOrderAsPaid() throws Exception {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		SchillerOrder order = orderManagementService.getAllOrders(pageable).getContent().get(0);

		mockMvc.perform(post("/orders/pay/cash/markPaid")
				.param("orderId", String.valueOf(order.getId())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/orders"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testMarkBillAsSent() throws Exception {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		SchillerOrder order = orderManagementService.getAllOrders(pageable).getContent().get(0);
		order.setPaymentMethod(new Bill());

		mockMvc.perform(post("/orders/pay/bill/sendBill")
				.param("orderId", String.valueOf(order.getId())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/orders"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testMarkBillAsPaid() throws Exception {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		SchillerOrder order = orderManagementService.getAllOrders(pageable).getContent().get(0);
		Bill bill = new Bill();
		bill.send();
		order.setPaymentMethod(bill);

		mockMvc.perform(post("/orders/pay/bill/markPaid")
				.param("orderId", String.valueOf(order.getId())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/orders"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testCancelOrder() throws Exception {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		SchillerOrder order = orderManagementService.getAllOrders(pageable).getContent().get(0);

		mockMvc.perform(post("/orders/cancel")
				.param("orderId", String.valueOf(order.getId())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/orders"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testShowSearchForm() throws Exception {
		mockMvc.perform(get("/showSearchOrder"))
			.andExpect(status().isOk())
			.andExpect(view().name("searchorder.html"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testSearch() throws Exception {
		mockMvc.perform(get("/orders/search")
				.param("searchTerm", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("orders"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testSearchOrder() throws Exception {
		mockMvc.perform(get("/showSearchOrder"))
			.andExpect(status().isOk())
			.andExpect(view().name("searchorder.html"));
	}
	@Test
	void testSearchModel() {

		SearchModel searchModel = new SearchModel();
		assertThat(searchModel.getSearchTerm()).isNull();


		String testSearchTerm = "testTerm";
		searchModel.setSearchTerm(testSearchTerm);
		assertThat(searchModel.getSearchTerm()).isEqualTo(testSearchTerm);
	}
}