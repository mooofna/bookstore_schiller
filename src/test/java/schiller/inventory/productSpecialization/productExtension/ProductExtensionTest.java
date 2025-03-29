package schiller.inventory.productSpecialization.productExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.inventory.productSpecialization.productExtension.category.Category;

import javax.money.MonetaryAmount;

@SpringBootTest
public class ProductExtensionTest {

	@Test
	public void testProductExtensionConstructor() {
		String name = "Product";
		String image = "test.jpg";
		Money price = Money.of(9.99, "USD");
		String info = "Test";
		Category category = new Category("tCategory");

		ProductExtension productExtension = new ProductExtension(name, image, price, info, category);

		assertEquals(name, productExtension.getName());
		assertEquals(image, productExtension.getImage());
		assertEquals(price, productExtension.getPrice());
		assertEquals(info, productExtension.getInfo());
		assertEquals(category, productExtension.getCategory());
	}

	@Test
	public void testGetterAndSetter() {

		ProductExtension productExtension = new ProductExtension();

		String testName = "test";
		String testImage = "test";
		Money testPrice = Money.of(5, "EUR");
		String testInfo = "test";
		Category testCategory = new Category("test");

		productExtension.setName(testName);
		productExtension.setImage(testImage);
		productExtension.setPrice(testPrice);
		productExtension.setInfo(testInfo);
		productExtension.setCategory(testCategory);

		String retrievedName = productExtension.getName();
		String retrievedImage = productExtension.getImage();
		MonetaryAmount retrievedPrice = productExtension.getPrice();
		String retrievedInfo = productExtension.getInfo();
		Category retrievedCategory = productExtension.getCategory();

		assertEquals(testName, retrievedName, "unlucky");
		assertEquals(testImage, retrievedImage, "unlucky");
		assertEquals(testPrice, retrievedPrice, "unlucky");
		assertEquals(testInfo, retrievedInfo, "unlucky");
		assertEquals(testCategory, retrievedCategory, "unlucky");
	}

	@Test
	public void testNoArgConstructor() {
		ProductExtension productExtension = new ProductExtension();

		assertEquals(null, productExtension.getName());
		assertEquals(null, productExtension.getImage());
		assertEquals(null, productExtension.getPrice());
		assertEquals(null, productExtension.getInfo());
		assertEquals(null, productExtension.getCategory());
	}
}
