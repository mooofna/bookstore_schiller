package schiller.catalog;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import schiller.AbstractIntegrationTests;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.inventory.productSpecialization.book.genre.GenreRepository;
import schiller.inventory.productSpecialization.productExtension.ProductExtension;
import schiller.inventory.productSpecialization.productExtension.ProductExtensionCatalog;
import schiller.inventory.productSpecialization.productExtension.category.Category;
import schiller.inventory.productSpecialization.productExtension.category.CategoryRepository;
import schiller.user.User;
import schiller.user.UserRepository;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class CatalogControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	@Qualifier("BookCatalog")
	private BookCatalog bookCatalog;

	@Autowired
	@Qualifier("ProductExtensionCatalog")
	private ProductExtensionCatalog productExtensionCatalog;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private GenreRepository genreRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void testCatalogViewAndModelAttributes() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("catalog");
		assertThat(model).containsKey("genres");
		assertThat(model).containsKey("categories");
		assertThat(model).containsKey("categoriesEmpty");
		assertThat(model).containsKey("title");

		assertThat(model.get("genres")).isEqualTo(genreRepository.findAll());

		assertThat(model.get("title")).isEqualTo("catalog.title");
	}

	@Test
	void testFilteredCatalogViewAndModelAttributes() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog/filter")
				.param("ProductType", "Book"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("catalog");
		assertThat(model).containsKey("genres");
		assertThat(model).containsKey("categories");
		assertThat(model).containsKey("categoriesEmpty");
		assertThat(model).containsKey("title");

		assertThat(model.get("genres")).isEqualTo(genreRepository.findAll());

		assertThat(model.get("title")).isEqualTo("catalog.title");
	}

	@Test
	void testCatalogGenreFiction() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog")
				.param("genreId", "1"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(2);
	}

	@Test
	void testCatalogGenreNonFiction() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog")
			.param("genreId", "1"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(2);
	}

	@Test
	void testCatalogGenreEntertainment() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog")
				.param("genreId", "1"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(2);
	}

	@Test
	void testCatalogGenreAdvisor() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog")
				.param("genreId", "1"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(2);
	}

	@Test
	void testBookDetailPage() throws Exception {

		Book book = bookCatalog.findAll().iterator().next();

		MvcResult result = mockMvc.perform(get("/product/" + book.getId()))
			.andExpect(status().isOk())
			.andExpect(view().name("product_details"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("form");
		assertThat(model).containsKey("product");
		assertThat(model).containsKey("quantity");
		assertThat(model).containsKey("orderable");

		assertThat(model).containsKey("associatedProducts");
		assertThat(model).containsKey("associatedProductsEmpty");
		assertThat(model).containsKey("sameGenreBooks");
		assertThat(model).containsKey("sameGenreBooksEmpty");
		assertThat(model).containsKey("sameCategoryProductExtensions");
		assertThat(model).containsKey("sameCategoryProductExtensionsEmpty");
	}

	@Test
	@WithMockUser(username = "user", roles = "CUSTOMER")
	void testPostCommentWithErrorRatingTooHigh() throws Exception {

		Book book = bookCatalog.findAll().iterator().next();

		mockMvc.perform(post("/product/" + book.getId() + "/comments")
				.param("comment", "Great Book!")
				.param("rating", "6"))
			.andExpect(status().isOk())
			.andExpect(view().name("product_details"))
			.andReturn();

		assertThat(book.getComments().isEmpty()).isTrue();
	}

	@Test
	@WithMockUser(username = "user", roles = "CUSTOMER")
	void testPostCommentWithErrorRatingTooLow() throws Exception {

		Book book = bookCatalog.findAll().iterator().next();

		mockMvc.perform(post("/product/" + book.getId() + "/comments")
				.param("comment", "Great Book!")
				.param("rating", "0"))
			.andExpect(status().isOk())
			.andExpect(view().name("product_details"))
			.andReturn();

		assertThat(book.getComments().isEmpty()).isTrue();
	}

	@Test
	@WithMockUser(username = "user", roles = "CUSTOMER")
	void testPostCommentWithErrorCommentEmpty() throws Exception {

		Book book = bookCatalog.findAll().iterator().next();
		
		mockMvc.perform(post("/product/" + book.getId() + "/comments")
				.param("comment", "")
				.param("rating", "6"))
			.andExpect(status().isOk())
			.andExpect(view().name("product_details"))
			.andReturn();

		assertThat(book.getComments().isEmpty()).isTrue();
	}


	@Test
	@WithMockUser(username = "user", roles = "CUSTOMER")
	void testPostCommentOk() throws Exception {

		Book book = bookCatalog.findAll().iterator().next();

		mockMvc.perform(post("/product/" + book.getId() + "/comments")
				.param("comment", "Great book!")
				.param("rating", "5"))
			.andExpect(status().isOk())
			.andExpect(view().name("/login"))
			.andReturn();
	}

	@Test
	void testProductExtensionCatalog() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("test", "test", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		ProductExtension productExtension = productExtensionCatalog.findAll().iterator().next();

		MvcResult result = mockMvc.perform(get("/catalog")
				.param("categoryId", "TEST"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> productExtensions = (Streamable<?>) model.get("catalog");

		assertThat(productExtensions).hasSize(1);
	}

	@Test
	@WithMockUser(username = "user", roles = "CUSTOMER")
	void testPostCommentOnProductExtension() throws Exception {


		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("test", "test", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		ProductExtension productExtension = productExtensionCatalog.findAll().iterator().next();

		mockMvc.perform(post("/product/" + productExtension.getId() + "/comments")
				.param("comment", "Great book!")
				.param("rating", "5"))
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	void testProductExtensionDetailPage() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("test", "test", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		ProductExtension productExtension = productExtensionCatalog.findAll().iterator().next();

		MvcResult result = mockMvc.perform(get("/product/" + productExtension.getId()))
			.andExpect(status().isOk())
			.andExpect(view().name("product_details"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("form");
		assertThat(model).containsKey("product");
		assertThat(model).containsKey("quantity");
		assertThat(model).containsKey("orderable");

		assertThat(model).containsKey("associatedProducts");
		assertThat(model).containsKey("associatedProductsEmpty");
		assertThat(model).containsKey("sameGenreBooks");
		assertThat(model).containsKey("sameGenreBooksEmpty");
		assertThat(model).containsKey("sameCategoryProductExtensions");
		assertThat(model).containsKey("sameCategoryProductExtensionsEmpty");
	}

	@Test
	void testFilteredCatalogWithProductExtensionViewAndModelAttributes() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog/filter")
				.param("ProductType", "ProductExtension"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("catalog");
		assertThat(model).containsKey("genres");
		assertThat(model).containsKey("categories");
		assertThat(model).containsKey("categoriesEmpty");
		assertThat(model).containsKey("title");

		assertThat(model.get("genres")).isEqualTo(genreRepository.findAll());

		assertThat(model.get("title")).isEqualTo("catalog.title");
	}

	@Test
	void testFilteredCatalogWithPrice5() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog/filter")
				.param("ProductType", "Book")
				.param("Price", "5"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(8);
	}

	@Test
	void testFilteredCatalogWithPrice10() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog/filter")
				.param("ProductType", "Book")
				.param("Price", "10"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(0);
	}

	@Test
	void testFilteredCatalogWithPrice20() throws Exception {

		MvcResult result = mockMvc.perform(get("/catalog/filter")
				.param("ProductType", "Book")
				.param("Price", "20"))
			.andExpect(status().isOk())
			.andExpect(view().name("catalog"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		Streamable<?> books = (Streamable<?>) model.get("catalog");

		assertThat(books).hasSize(0);
	}
}
