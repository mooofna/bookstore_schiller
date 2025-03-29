package schiller.inventory.productSpecialization.book.genre;

import org.junit.jupiter.api.Test;
import org.salespointframework.core.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.AbstractIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertFalse;

@SpringBootTest
public class GenreDataInitializerTest extends AbstractIntegrationTests {

	@Autowired
	private GenreRepository genreRepository;

	@Autowired
	@Qualifier("GenreDataInitializer")
	private DataInitializer genreDataInitializer;

	@Test
	public void testDoNotInitializeTwice() {

		genreDataInitializer.initialize();

		assertThat(genreRepository.findAll()).hasSize(4);
	}
}
