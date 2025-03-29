package schiller.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import schiller.AbstractIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc

class PickupTest extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderManagementService orderManagementService;



	@Test
	void testPickupConstructor() {
		Pickup pickup = new Pickup();
		assertThat(pickup.getId()).isNull();
		assertThat(pickup.isPickedUp()).isFalse();
	}

	@Test
	void testPickUpAndIsPickedUpMethods() {
		Pickup pickup = new Pickup();
		assertThat(pickup.isPickedUp()).isFalse();

		pickup.pickUp();
		assertThat(pickup.isPickedUp()).isTrue();
	}
}
