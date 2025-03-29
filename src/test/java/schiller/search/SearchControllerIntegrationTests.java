package schiller.search;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import schiller.AbstractIntegrationTests;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.inventory.productSpecialization.book.author.Author;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.book.genre.GenreRepository;
import schiller.inventory.productSpecialization.productExtension.ProductExtension;
import schiller.inventory.productSpecialization.productExtension.ProductExtensionCatalog;
import schiller.inventory.productSpecialization.productExtension.category.Category;
import schiller.inventory.productSpecialization.productExtension.category.CategoryRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc
@SpringBootTest
public class SearchControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private GenreRepository genreRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	@Qualifier("BookCatalog")
	private BookCatalog bookCatalog;

	@Autowired
	@Qualifier("ProductExtensionCatalog")
	private ProductExtensionCatalog productExtensionCatalog;

	@Test
	void testSearchModelandModelAttributes() throws Exception {
		MvcResult result = mockMvc.perform(get("/search"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("catalog");
		assertThat(model).containsKey("genres");
		assertThat(model).containsKey("title");
		assertThat(model).containsKey("categories");
		assertThat(model).containsKey("categoriesEmpty");

		assertThat(model.get("genres")).isEqualTo(genreRepository.findAll());

		assertThat(model.get("title")).isEqualTo("search.title");
	}

	@Test
	void testSearchWithSearchTermIncludesBookName() throws Exception {
		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "hobbit"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithSearchTermIncludesAuthorName() throws Exception {
		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "tolkien"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithSearchTermIncludesGenreName() throws Exception {
		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "unterhaltung"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(2);
	}

	@Test
	void testSearchWithBookNameMatchAndGenreMatch() throws Exception {

		genreRepository.save(new Genre("test"));
		bookCatalog.save(new Book("test", "test", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"test", "test",
			new Author("boss"),
			new ArrayList<>(Collections.singletonList(genreRepository.findByName("TEST").orElseThrow()))));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithBookNameMatchAndAuthorMatch() throws Exception {

		genreRepository.save(new Genre("test"));
		bookCatalog.save(new Book("boss", "test", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"test", "test",
			new Author("boss"),
			new ArrayList<>(Collections.singletonList(genreRepository.findByName("TEST").orElseThrow()))));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "boss"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithBookGenreMatchAndAuthorMatch() throws Exception {

		genreRepository.save(new Genre("test"));
		bookCatalog.save(new Book("abc", "test", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"test", "test",
			new Author("test"),
			new ArrayList<>(Collections.singletonList(genreRepository.findByName("TEST").orElseThrow()))));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithBookNameMatchAndGenreMatchAndAuthorMatch() throws Exception {

		genreRepository.save(new Genre("test"));
		bookCatalog.save(new Book("test", "test", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"test", "test",
			new Author("test"),
			new ArrayList<>(Collections.singletonList(genreRepository.findByName("TEST").orElseThrow()))));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithoutSearchTerm() throws Exception {
		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(8);
	}

	@Test
	void testSearchWithProductExtensionsNameMatch() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("abc", "test", Money.of(5, "EUR"), "boss", categoryRepository.findById("TEST").orElseThrow()));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "abc"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithProductExtensionsCategoryMatch() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("abc", "test", Money.of(5, "EUR"), "boss", categoryRepository.findById("TEST").orElseThrow()));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithProductExtensionsInfoMatch() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("abc", "test", Money.of(5, "EUR"), "boss", categoryRepository.findById("TEST").orElseThrow()));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "boss"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithProductExtensionsNameMatchAndCategoryMatch() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("test", "test", Money.of(5, "EUR"), "boss", categoryRepository.findById("TEST").orElseThrow()));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithProductExtensionsNameMatchAndCategoryMatchAndInfoMatch() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("test", "test", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithProductExtensionsCategoryMatchAndInfoMatch() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("abc", "test", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testSearchWithProductExtensionsNameMatchAndInfoMatch() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("abc", "test", Money.of(5, "EUR"), "abc", categoryRepository.findById("TEST").orElseThrow()));

		MvcResult result = mockMvc.perform(get("/search")
				.param("searchTerm", "abc"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(1);
	}

	@Test
	void testOrdersSearchModelAndModelAttributes() throws Exception {
		MvcResult result = mockMvc.perform(get("/orders/search")
				.param("searchTerm", "a"))
			.andExpect(status().isOk())
			.andExpect(view().name("orders"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("orders");
	}

	@Test
	void testOrdersSearchWithSearchTermIncludesUsername() throws Exception {
		MvcResult result = mockMvc.perform(get("/orders/search")
				.param("searchTerm", "user"))
			.andExpect(status().isOk())
			.andExpect(view().name("orders"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		List<?> user = (List<?>) model.get("orders");

		assertThat(user).hasSize(0);
	}
}
