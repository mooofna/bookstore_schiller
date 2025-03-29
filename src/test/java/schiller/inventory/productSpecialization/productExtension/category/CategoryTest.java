package schiller.inventory.productSpecialization.productExtension.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CategoryTest {

	@Test
	public void testCategoryConstructor() {
		String name = "category";
		Category category = new Category(name);

		assertEquals(name.toUpperCase(), category.getName(), "The name should be converted to upper case");
	}

	@Test
	public void testNoArgConstructor() {
		Category category = new Category();
		assertEquals(null, category.getName(), "The name should be null for no-arg constructor");
	}
}
