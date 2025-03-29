package schiller.inventory;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import schiller.AbstractIntegrationTests;
import schiller.inventory.addItem.BookForm;
import schiller.inventory.addItem.GenreForm;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.inventory.productSpecialization.book.author.Author;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.book.genre.GenreRepository;
import schiller.inventory.productSpecialization.productExtension.ProductExtension;
import schiller.inventory.productSpecialization.productExtension.ProductExtensionCatalog;
import schiller.inventory.productSpecialization.productExtension.category.Category;
import schiller.inventory.productSpecialization.productExtension.category.CategoryRepository;
import schiller.inventory.storage.FileSystemStorageService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
public class InventoryControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UniqueInventory<UniqueInventoryItem> inventory;

	@Autowired
	@Qualifier("BookCatalog")
	private BookCatalog bookCatalog;

	@Autowired
	@Qualifier("ProductExtensionCatalog")
	private ProductExtensionCatalog productExtensionCatalog;

	@Autowired
	private GenreRepository genreRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private FileSystemStorageService fileSystemStorageService;

	@Autowired
	private InventoryService inventoryService;

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testStockViewAndModelAttributes() throws Exception {
		MvcResult result = mockMvc.perform(get("/inventory"))
			.andExpect(status().isOk())
			.andExpect(view().name("inventory"))
			.andReturn();

		ModelMap model = result.getModelAndView().getModelMap();

		assertThat(model).containsKey("inventory");
		assertThat(model).containsKey("inventoryService");
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testHandleReorder() throws Exception {
		Book book = bookCatalog.findAll().iterator().next();

		mockMvc.perform(post("/inventory/reorder")
				.param("pid", String.valueOf(book.getId()))
				.param("reorder-quantity", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/inventory"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryBookFormViewAndModelAttributes() throws Exception {

		MvcResult result = mockMvc.perform(get("/inventory/addBookView"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_book"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("bookForm");
		assertThat(model).containsKey("defaultPrice");
		assertThat(model).containsKey("defaultQuantity");
		assertThat(model).containsKey("alreadyExists");

		assertThat(model.get("genres")).isEqualTo(genreRepository.findAll());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryProductExtensionFormViewAndModelAttributes() throws Exception {

		MvcResult result = mockMvc.perform(get("/inventory/addProductExtensionView"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_productExtension"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("productExtensionForm");
		assertThat(model).containsKey("defaultPrice");
		assertThat(model).containsKey("defaultQuantity");
		assertThat(model).containsKey("alreadyExists");

		assertThat(model.get("categories")).isEqualTo(categoryRepository.findAll());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryEditItemFormViewAndModelAttributes() throws Exception {

		Book book = bookCatalog.findAll().iterator().next();

		MvcResult result = mockMvc.perform(get("/inventory/editBookView")
				.param("pid", String.valueOf(book.getId())))
			.andExpect(status().isOk())
			.andExpect(view().name("edit_book"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("bookForm");
		assertThat(model).containsKey("defaultPrice");
		assertThat(model).containsKey("defaultQuantity");
		assertThat(model).containsKey("pid");

		assertThat(model.get("genres")).isEqualTo(genreRepository.findAll());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryEditProductExtensionFormViewAndModelAttributes() throws Exception {

		categoryRepository.save(new Category("test"));
		productExtensionCatalog.save(new ProductExtension("test", "test", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		ProductExtension productExtension = productExtensionCatalog.findAll().iterator().next();
		inventory.save(new UniqueInventoryItem(productExtension, Quantity.of(5)));

		MvcResult result = mockMvc.perform(get("/inventory/editProductExtensionView")
				.param("pid", String.valueOf(productExtension.getId())))
			.andExpect(status().isOk())
			.andExpect(view().name("edit_productExtension"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("productExtensionForm");
		assertThat(model).containsKey("defaultPrice");
		assertThat(model).containsKey("defaultQuantity");
		assertThat(model).containsKey("pid");

		assertThat(model.get("categories")).isEqualTo(categoryRepository.findAll());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryGenreFormViewAndModelAttributes() throws Exception {

		MvcResult result = mockMvc.perform(get("/inventory/addGenreView"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_genre"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("genreForm");
		assertThat(model).containsKey("alreadyExists");
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryCategoryFormViewAndModelAttributes() throws Exception {

		MvcResult result = mockMvc.perform(get("/inventory/addCategoryView"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_category"))
			.andReturn();

		ModelMap model = Objects.requireNonNull(result.getModelAndView()).getModelMap();

		assertThat(model).containsKey("categoryForm");
		assertThat(model).containsKey("alreadyExists");
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddBookViewRedirection() throws Exception {

		MockMultipartFile image = new MockMultipartFile(
			"image",
			"dun.png",
			"image/png",
			"<<png data>>".getBytes()
		);

		MvcResult result = mockMvc.perform(multipart("/inventory/addBook")
				.file(image)
				.param("name", "test")
				.param("price", "20")
				.param("isbn", UUID.randomUUID().toString())
				.param("coverText", "test")
				.param("publisher", "test")
				.param("author", "test")
				.param("genres", "1")
				.param("quantity", "20"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/inventory"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddBookOkBookExists() throws Exception {

		MockMultipartFile image = new MockMultipartFile(
			"image",
			"dun.png",
			"image/png",
			"<<png data>>".getBytes()
		);

		Book book = bookCatalog.findByName("The Hobbit").stream().toList().get(0);
		String isbn = book.getIsbn();

		MvcResult result = mockMvc.perform(multipart("/inventory/addBook")
				.file(image)
				.param("name", "test")
				.param("price", "20")
				.param("isbn", isbn)
				.param("coverText", "test")
				.param("publisher", "test")
				.param("author", "test")
				.param("genres", "1")
				.param("quantity", "20"))
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddBookOkErrors() throws Exception {

		MvcResult result = mockMvc.perform(multipart("/inventory/addBook")
				.param("image", "dun.png")
				.param("name", "test")
				.param("price", "20")
				.param("isbn", UUID.randomUUID().toString())
				.param("coverText", "test")
				.param("publisher", "test")
				.param("author", "test")
				.param("genres", "1")
				.param("quantity", "20"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_book"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleEditBookViewRedirection() throws Exception {

		MockMultipartFile image = new MockMultipartFile(
			"image",
			"dun.png",
			"image/png",
			"<<png data>>".getBytes()
		);

		Book book = bookCatalog.findAll().iterator().next();
		Product.ProductIdentifier pid = book.getId();

		MvcResult result = mockMvc.perform(multipart("/inventory/editBook")
				.file(image)
				.param("name", "test")
				.param("price", "20")
				.param("isbn", UUID.randomUUID().toString())
				.param("coverText", "test")
				.param("publisher", "test")
				.param("author", "test")
				.param("genres", "1")
				.param("quantity", "20")
				.param("pid", "pid"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/inventory"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleEditBookOkErrors() throws Exception {

		Book book = bookCatalog.findAll().iterator().next();
		Product.ProductIdentifier pid = book.getId();

		MvcResult result = mockMvc.perform(multipart("/inventory/editBook")
				.param("image", "dun.png")
				.param("name", "test")
				.param("price", "20")
				.param("isbn", UUID.randomUUID().toString())
				.param("coverText", "test")
				.param("publisher", "test")
				.param("author", "test")
				.param("genres", "1")
				.param("quantity", "20")
				.param("pid", "pid"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_book"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddProductExtensionViewRedirection() throws Exception {

		MockMultipartFile image = new MockMultipartFile(
			"image",
			"dun.png",
			"image/png",
			"<<png data>>".getBytes()
		);

		categoryRepository.save(new Category("test"));

		MvcResult result = mockMvc.perform(multipart("/inventory/addProductExtension")
				.file(image)
				.param("name", "test")
				.param("price", "20")
				.param("info", "test")
				.param("Category", "TEST")
				.param("quantity", "20"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/inventory"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddProductExtensionOkProductExtensionExists() throws Exception {

		MockMultipartFile image = new MockMultipartFile(
			"image",
			"dun.png",
			"image/png",
			"<<png data>>".getBytes()
		);

		Category category = new Category("test");
		categoryRepository.save(category);
		productExtensionCatalog.save(new ProductExtension("test", "dun.png", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		MvcResult result = mockMvc.perform(multipart("/inventory/addProductExtension")
				.file(image)
				.param("name", "test")
				.param("price", "20")
				.param("info", "test")
				.param("Category", "TEST")
				.param("quantity", "20"))
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddProductExtensionOkErrors() throws Exception {

		MvcResult result = mockMvc.perform(multipart("/inventory/addProductExtension")
				.param("image", "dun.png")
				.param("name", "test")
				.param("price", "20")
				.param("info", "test")
				.param("Category", "TEST")
				.param("quantity", "20"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_productExtension"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleEditProductExtensionRedirection() throws Exception {

		MockMultipartFile image = new MockMultipartFile(
			"image",
			"dun.png",
			"image/png",
			"<<png data>>".getBytes()
		);

		Category category = new Category("test");
		categoryRepository.save(category);
		productExtensionCatalog.save(new ProductExtension("test", "dun.png", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		ProductExtension productExtension = productExtensionCatalog.findAll().iterator().next();
		Product.ProductIdentifier pid = productExtension.getId();

		MvcResult result = mockMvc.perform(multipart("/inventory/editProductExtension")
				.file(image)
				.param("name", "test")
				.param("price", "20")
				.param("info", "test")
				.param("Category", "TEST")
				.param("quantity", "20")
				.param("pid", "pid"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/inventory"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleEditProductExtensionOkErrors() throws Exception {


		Category category = new Category("test");
		categoryRepository.save(category);
		productExtensionCatalog.save(new ProductExtension("test", "dun.png", Money.of(5, "EUR"), "test", categoryRepository.findById("TEST").orElseThrow()));

		ProductExtension productExtension = productExtensionCatalog.findAll().iterator().next();
		Product.ProductIdentifier pid = productExtension.getId();

		MvcResult result = mockMvc.perform(multipart("/inventory/editProductExtension")
				.param("image", "dun.png")
				.param("name", "test")
				.param("price", "20")
				.param("info", "test")
				.param("Category", "TEST")
				.param("quantity", "20")
				.param("pid", "pid"))
			.andExpect(status().isOk())
			.andExpect(view().name("add_productExtension"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddGenreRedirection() throws Exception {

		MvcResult result = mockMvc.perform(post("/inventory/addGenre")
				.param("name", "test"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/inventory"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddGenreOk() throws Exception {

		MvcResult result = mockMvc.perform(post("/inventory/addGenre")
				.param("name", "FIKTION"))
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddCategoryRedirection() throws Exception {

		MvcResult result = mockMvc.perform(post("/inventory/addCategory")
				.param("name", "test"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/inventory"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testInventoryHandleAddCategoryOk() throws Exception {

		categoryRepository.save(new Category("test"));

		MvcResult result = mockMvc.perform(post("/inventory/addCategory")
				.param("name", "test"))
			.andExpect(status().isOk())
			.andReturn();
	}
}
