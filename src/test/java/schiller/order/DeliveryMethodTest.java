package schiller.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import schiller.AbstractIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
class DeliveryMethodTest extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderManagementService orderManagementService;


	@Test
	void testDeliveryMethodId() {
		Delivery delivery = new Delivery("Haus");
		delivery.setId(123L);
		assertThat(delivery.getId()).isEqualTo(123L);
	}

	@Test
	void testDeliveryMethodGettersSetters() {
		Delivery delivery = new Delivery("Haus");
		delivery.setId(123L);
		assertThat(delivery.getId()).isEqualTo(123L);
	}
}

