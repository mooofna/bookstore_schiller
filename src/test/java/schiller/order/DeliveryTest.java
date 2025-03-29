package schiller.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import schiller.AbstractIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
class DeliveryTest extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderManagementService orderManagementService;





	@Test
	void testDeliveryConstructorAndInitialization() {
		Delivery delivery = new Delivery("Haus");
		assertThat(delivery.getShippingAddress()).isEqualTo("Haus");
		assertThat(delivery.getDeliveryStatus()).isEqualTo(Delivery.DeliveryStatus.READY_FOR_SHIPPING);

		assertThrows(IllegalArgumentException.class, () -> new Delivery(null));
	}

	@Test
	void testDeliveryGetters() {
		Delivery delivery = new Delivery("Haus");
		assertThat(delivery.getShippingAddress()).isEqualTo("Haus");
		assertThat(delivery.getDeliveryStatus()).isEqualTo(Delivery.DeliveryStatus.READY_FOR_SHIPPING);
	}

	@Test
	void testSetDeliveryStatus() {
		Delivery delivery = new Delivery("Haus");
		delivery.setDeliveryStatus(Delivery.DeliveryStatus.SHIPPED);
		assertThat(delivery.getDeliveryStatus()).isEqualTo(Delivery.DeliveryStatus.SHIPPED);

		assertThrows(IllegalArgumentException.class, () -> delivery.setDeliveryStatus(null));
	}

	@Test
	void testReadyForCompletion() {
		Delivery delivery = new Delivery("Haus");
		delivery.setDeliveryStatus(Delivery.DeliveryStatus.DELIVERED);
		assertThat(delivery.readyForCompletion()).isTrue();

		delivery.setDeliveryStatus(Delivery.DeliveryStatus.SHIPPED);
		assertThat(delivery.readyForCompletion()).isFalse();
	}

	@Test
	void testEqualsAndHashCode() {
		Delivery delivery1 = new Delivery("Haus");
		Delivery delivery2 = new Delivery("Haus");
		Delivery delivery3 = new Delivery(" Elm St");

		assertThat(delivery1).isEqualTo(delivery2);
		assertThat(delivery1).isNotEqualTo(delivery3);

		assertThat(delivery1.hashCode()).isEqualTo(delivery2.hashCode());
		assertThat(delivery1.hashCode()).isNotEqualTo(delivery3.hashCode());
	}

	@Test
	void testToString() {
		Delivery delivery = new Delivery("Haus");
		assertThat(delivery.toString()).isEqualTo("Delivery()");
	}



}