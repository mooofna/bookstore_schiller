package schiller.inventory.addItem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.AbstractIntegrationTests;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenreFormTest extends AbstractIntegrationTests {

	@Test
	void testGetterAndSetter() {

		GenreForm genreForm = new GenreForm();
		String testName = "Fantasy";


		genreForm.setName(testName);
		String retrievedName = genreForm.getName();

		assertEquals(testName, retrievedName, "The getter or setter for name is not working as expected.");
	}
}
