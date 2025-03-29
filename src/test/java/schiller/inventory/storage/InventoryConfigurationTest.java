package schiller.inventory.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.inventory.InventoryConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InventoryConfigurationTest {

	@Autowired
	private InventoryConfiguration inventoryConfiguration;

	@Test
	public void testRestockThresholdGetterAndSetter() {
		int expectedRestockThreshold = 10;
		inventoryConfiguration.setRestockThreshold(expectedRestockThreshold);
		assertEquals(expectedRestockThreshold, inventoryConfiguration.getRestockThreshold());
	}

	@Test
	public void testQuantityThresholdGetterAndSetter() {
		int expectedQuantityThreshold = 20;
		inventoryConfiguration.setQuantityThreshold(expectedQuantityThreshold);
		assertEquals(expectedQuantityThreshold, inventoryConfiguration.getQuantityThreshold());
	}

	@Test
	public void testPriceThresholdGetterAndSetter() {
		int expectedPriceThreshold = 30;
		inventoryConfiguration.setPriceThreshold(expectedPriceThreshold);
		assertEquals(expectedPriceThreshold, inventoryConfiguration.getPriceThreshold());
	}
}
