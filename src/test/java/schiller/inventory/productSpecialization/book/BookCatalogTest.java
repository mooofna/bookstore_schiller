package schiller.inventory.productSpecialization.book;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.AbstractIntegrationTests;
import schiller.inventory.productSpecialization.book.author.Author;
import schiller.inventory.productSpecialization.book.genre.GenreRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
public class BookCatalogTest extends AbstractIntegrationTests {

	@Autowired
	@Qualifier("BookCatalog")
	private BookCatalog bookCatalog;

	@Autowired
	private GenreRepository genreRepository;

	@Test
	public void testFindByIsbn() {

		String testIsbn = UUID.randomUUID().toString();
		Book testBook = new Book("test", "test", Money.of(9.99, "EUR"), testIsbn,
			"test", "test",
			new Author("test"),
			new ArrayList<>(Collections.singletonList(genreRepository.findByName("FIKTION").orElseThrow())));

		bookCatalog.save(testBook);

		Book retrievedBook = bookCatalog.findByIsbn(testIsbn).stream().toList().get(0);

		assertEquals("unlucky", testBook, retrievedBook);
	}
}
