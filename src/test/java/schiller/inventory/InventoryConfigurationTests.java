package schiller.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
	"salespoint.inventory.restock-threshold=10",
	"salespoint.inventory.quantity-threshold=5",
	"salespoint.inventory.price-threshold=100"
})
class InventoryConfigurationTest {

	@Autowired
	private InventoryConfiguration inventoryConfiguration;

	@Test
	void testRestockThreshold() {
		assertThat(inventoryConfiguration.getRestockThreshold()).isEqualTo(10);
	}

	@Test
	void testQuantityThreshold() {
		assertThat(inventoryConfiguration.getQuantityThreshold()).isEqualTo(5);
	}

	@Test
	void testPriceThreshold() {
		assertThat(inventoryConfiguration.getPriceThreshold()).isEqualTo(100);
	}
}
