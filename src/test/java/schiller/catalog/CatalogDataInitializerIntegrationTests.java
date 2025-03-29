package schiller.catalog;

import org.junit.jupiter.api.Test;
import org.salespointframework.core.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import schiller.AbstractIntegrationTests;
import schiller.inventory.productSpecialization.book.BookCatalog;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class CatalogDataInitializerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	@Qualifier("BookCatalog")
	private BookCatalog catalog;

	@Autowired
	@Qualifier("CatalogDataInitializer")
	private DataInitializer dataInitializer;

	@Test
	void testInitializeBookCatalog() {
		assertThat(catalog.findAll()).hasSize(8);
	}

	@Test
	void testInitializeBookCatalogNotEmpty() {

		dataInitializer.initialize();

		assertThat(catalog.findAll()).hasSize(8);
	}
}
