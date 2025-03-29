package schiller.inventory.addItem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.AbstractIntegrationTests;
import schiller.inventory.productSpecialization.productExtension.category.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CategoryFormTest extends AbstractIntegrationTests {

	@Test
	void testGetterAndSetter() {

		CategoryForm categoryForm = new CategoryForm();
		String testName = "Fantasy";


		categoryForm.setName(testName);
		String retrievedName = categoryForm.getName();

		assertEquals(testName, retrievedName, "The getter or setter for name is not working as expected.");
	}
}
