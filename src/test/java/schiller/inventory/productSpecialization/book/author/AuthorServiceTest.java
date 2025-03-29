package schiller.inventory.productSpecialization.book.author;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.AbstractIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthorServiceTest extends AbstractIntegrationTests {

	@Autowired
	private AuthorService authorService;

	@Autowired
	private AuthorRepository authorRepository;

	@Test
	public void testGetOrCreateAuthorNewAuthor() {

		authorService.getOrCreateAuthor("test");

		assertThat(authorRepository.findAll()).hasSize(9);
	}

	@Test
	public void testGetOrCreateAuthorNoNewAuthor() {

		authorService.getOrCreateAuthor("J.R.R Tolkien");

		assertThat(authorRepository.findAll()).hasSize(8);
	}
}
