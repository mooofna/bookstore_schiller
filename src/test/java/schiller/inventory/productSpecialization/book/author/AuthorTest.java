package schiller.inventory.productSpecialization.book.author;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.AbstractIntegrationTests;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
public class AuthorTest extends AbstractIntegrationTests {

	@Test
	public void testGetter() {

		Author author = new Author("test");

		String retrievedName = author.getName();
		long retrievedId = author.getId();

		assertEquals("unlucky", "test", retrievedName);
		assertEquals("unlucky", retrievedId, retrievedId);
	}
}
