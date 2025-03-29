package schiller.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import schiller.AbstractIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc

class BillTest extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderManagementService orderManagementService;


	@Test
	void testBillConstructorAndInitialization() {
		Bill bill = new Bill("Haus");
		assertThat(bill.getBillingAddress()).isEqualTo("Haus");
		assertThat(bill.isSent()).isFalse();

		assertThrows(IllegalArgumentException.class, () -> new Bill(null));


		assertThrows(IllegalArgumentException.class, () -> new Bill(""));
	}

	@Test
	void testBillGetters() {

		Bill bill = new Bill("Haus");
		assertThat(bill.getBillingAddress()).isEqualTo("Haus");


		assertThat(bill.getId()).isNull();
	}

	@Test
	void testIsSent() {

		Bill bill = new Bill("Haus");
		assertThat(bill.isSent()).isFalse();

		bill.send();
		assertThat(bill.isSent()).isTrue();
	}

	@Test
	void testSendMethod() {

		Bill bill = new Bill("Haus");
		bill.send();
		assertThat(bill.isSent()).isTrue();
	}

	@Test
	void testSetIdMethod() {

		Bill bill = new Bill("Haus");
		bill.setId(10L);
		assertThat(bill.getId()).isEqualTo(10L);
	}

	@Test
	void testToStringMethod() {
		// Test toString
		Bill bill = new Bill("Haus");
		assertThat(bill.toString()).isEqualTo("Bill()");
	}

}
