package schiller.inventory;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.web.multipart.MultipartFile;
import schiller.inventory.addItem.BookForm;
import schiller.inventory.addItem.GenreForm;
import schiller.inventory.productSpecialization.book.author.Author;
import schiller.inventory.productSpecialization.book.author.AuthorService;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.book.genre.GenreRepository;
import schiller.inventory.productSpecialization.productExtension.ProductExtensionCatalog;
import schiller.inventory.productSpecialization.productExtension.category.CategoryRepository;
import schiller.inventory.storage.StorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

	@Mock
	private UniqueInventory<UniqueInventoryItem> inventory;
	@Mock
	private InventoryConfiguration inventoryConfiguration;
	@Mock
	private StorageService storageService;
	@Mock
	private BookCatalog bookCatalog;
	@Mock
	private AuthorService authorService;
	@Mock
	private GenreRepository genreRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ProductExtensionCatalog productExtensionCatalog;

	private InventoryService inventoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		inventoryService = new InventoryService(inventory, inventoryConfiguration, storageService, bookCatalog, productExtensionCatalog, authorService, genreRepository, categoryRepository);
	}

	@Test
	void updateInventoryItemsBelowThresholdWithEmptyInventory() {
		when(inventory.findAll()).thenReturn(Streamable.empty());
		when(inventoryConfiguration.getRestockThreshold()).thenReturn(10);

		inventoryService.updateInventoryItemsBelowThreshold();

		assertTrue(inventoryService.getProductsBelowRestockThreshold().isEmpty());
	}

	@Test
	void updateInventoryItemsBelowThresholdWithItemsAboveThreshold() {
		UniqueInventoryItem itemAboveThreshold = mock(UniqueInventoryItem.class);
		when(itemAboveThreshold.getQuantity()).thenReturn(Quantity.of(15));
		when(inventory.findAll()).thenReturn(Streamable.of(itemAboveThreshold));
		when(inventoryConfiguration.getRestockThreshold()).thenReturn(10);

		inventoryService.updateInventoryItemsBelowThreshold();

		assertTrue(inventoryService.getProductsBelowRestockThreshold().isEmpty());
	}

	@Test
	void testGetProductsBelowRestockThresholdWithEmptyInventory() {
		when(inventory.findAll()).thenReturn(Streamable.empty());
		Streamable<UniqueInventoryItem> result = inventoryService.getProductsBelowRestockThreshold();
		assertTrue(result.isEmpty());
	}

	@Test
	void testGetProductsBelowRestockThresholdWithAllItemsAboveThreshold() {
		UniqueInventoryItem item1 = mock(UniqueInventoryItem.class);
		UniqueInventoryItem item2 = mock(UniqueInventoryItem.class);
		when(item1.getQuantity()).thenReturn(Quantity.of(15));
		when(item2.getQuantity()).thenReturn(Quantity.of(20));
		when(inventory.findAll()).thenReturn(Streamable.of(item1, item2));
		when(inventoryConfiguration.getRestockThreshold()).thenReturn(10);

		Streamable<UniqueInventoryItem> result = inventoryService.getProductsBelowRestockThreshold();
		assertTrue(result.isEmpty());
	}

	@Test
	void testGetProductsBelowRestockThresholdWithSomeItemsBelowThreshold() {
		UniqueInventoryItem item1 = mock(UniqueInventoryItem.class);
		UniqueInventoryItem item2 = mock(UniqueInventoryItem.class);
		when(item1.getQuantity()).thenReturn(Quantity.of(5));
		when(item2.getQuantity()).thenReturn(Quantity.of(20));
		when(inventory.findAll()).thenReturn(Streamable.of(item1, item2));
		when(inventoryConfiguration.getRestockThreshold()).thenReturn(10);

		Streamable<UniqueInventoryItem> result = inventoryService.getProductsBelowRestockThreshold();
		assertEquals(1, result.stream().count());
		assertTrue(result.stream().anyMatch(item -> item.equals(item1)));
	}

	@Test
	void testIsProductBelowRestockThresholdWhenBelow() {
		Product.ProductIdentifier pid = mock(Product.ProductIdentifier.class);
		UniqueInventoryItem item = mock(UniqueInventoryItem.class);
		Product product = mock(Product.class);

		when(product.getId()).thenReturn(pid);
		when(item.getProduct()).thenReturn(product);
		when(item.getQuantity()).thenReturn(Quantity.of(5));
		when(inventory.findAll()).thenReturn(Streamable.of(item));
		when(inventoryConfiguration.getRestockThreshold()).thenReturn(10);

		inventoryService.updateInventoryItemsBelowThreshold();

		assertTrue(inventoryService.isProductBelowRestockThreshold(pid));
	}

	@Test
	void testIsProductBelowRestockThresholdWhenNotBelow() {
		Product.ProductIdentifier pid = mock(Product.ProductIdentifier.class);
		UniqueInventoryItem item = mock(UniqueInventoryItem.class);
		Product product = mock(Product.class);

		when(product.getId()).thenReturn(pid);
		when(item.getProduct()).thenReturn(product);
		when(item.getQuantity()).thenReturn(Quantity.of(15));
		when(inventory.findAll()).thenReturn(Streamable.of(item));
		when(inventoryConfiguration.getRestockThreshold()).thenReturn(10);

		inventoryService.updateInventoryItemsBelowThreshold();

		assertFalse(inventoryService.isProductBelowRestockThreshold(pid));
	}

	@Test
	void testIsProductBelowRestockThresholdWhenNullInventory() {
		Product.ProductIdentifier pid = mock(Product.ProductIdentifier.class);
		assertFalse(inventoryService.isProductBelowRestockThreshold(pid));
	}

	@Test
	void testAddBookSuccessfully() {
		Author mockAuthor = mock(Author.class);
		when(authorService.getOrCreateAuthor(anyString())).thenReturn(mockAuthor);

		Genre mockGenre = mock(Genre.class);
		when(mockGenre.getId()).thenReturn(1L);
		when(genreRepository.findAllById(any())).thenReturn(new ArrayList<>(List.of(mockGenre)));

		BookForm form = new BookForm();
		form.setName("Test Book");
		form.setPrice(20.0);
		form.setIsbn("1234567890");
		form.setPublisher("Test Publisher");
		form.setCoverText("Test Cover Text");
		form.setAuthor("Test Author");
		form.setGenres(new ArrayList<>(List.of(1L)));
		form.setQuantity(5);

		MultipartFile image = mock(MultipartFile.class);
		form.setImage(image);

		when(bookCatalog.findByIsbn(anyString())).thenReturn(Streamable.empty());
		when(storageService.store(any())).thenReturn("test-image.jpg");

		boolean result = inventoryService.addBook(form);
		assertTrue(result);
	}

	@Test
	void testEditBookSuccessfully() {
		Author mockAuthor = mock(Author.class);
		when(authorService.getOrCreateAuthor(anyString())).thenReturn(mockAuthor);

		Genre mockGenre = mock(Genre.class);
		when(mockGenre.getId()).thenReturn(1L);
		when(genreRepository.findAllById(any())).thenReturn(new ArrayList<>(List.of(mockGenre)));

		Product.ProductIdentifier pid = mock(Product.ProductIdentifier.class);
		UniqueInventoryItem mockItem = mock(UniqueInventoryItem.class);
		Book mockBook = mock(Book.class);
		when(mockItem.getProduct()).thenReturn(mockBook);
		when(mockItem.getQuantity()).thenReturn(Quantity.of(10));

		when(inventory.findByProductIdentifier(pid)).thenReturn(Optional.of(mockItem));
		when(bookCatalog.save(any(Book.class))).thenReturn(mockBook);

		BookForm form = new BookForm();
		form.setName("Edited Book");
		form.setPrice(25.0);
		form.setIsbn("0987654321");
		form.setPublisher("Edited Publisher");
		form.setCoverText("Edited Cover Text");
		form.setAuthor("Edited Author");
		form.setGenres(new ArrayList<>(List.of(1L)));
		form.setQuantity(15);

		MultipartFile image = mock(MultipartFile.class);
		form.setImage(image);

		boolean result = inventoryService.editBook(form, pid);
		assertTrue(result);

		verify(mockBook).setName("Edited Book");
		verify(mockBook).setPrice(Money.of(25.0, "EUR"));
		verify(mockBook).setIsbn("0987654321");
		verify(mockBook).setPublisher("Edited Publisher");
		verify(mockBook).setCoverText("Edited Cover Text");
		verify(mockBook).setAuthor(mockAuthor);
		verify(mockBook).setGenres(new ArrayList<>(List.of(mockGenre)));

		verify(mockItem).increaseQuantity(Quantity.of(15));
	}

	@Test
	void testAddGenreSuccessfully() {
		GenreForm genreForm = new GenreForm();
		genreForm.setName("Test Genre");

		when(genreRepository.findByName("TEST GENRE")).thenReturn(Optional.empty());

		ArgumentCaptor<Genre> genreCaptor = ArgumentCaptor.forClass(Genre.class);

		when(genreRepository.save(genreCaptor.capture())).thenAnswer(invocation -> invocation.<Genre>getArgument(0));

		boolean result = inventoryService.addGenre(genreForm);

		assertTrue(result);

		verify(genreRepository).findByName("TEST GENRE");

		Genre savedGenre = genreCaptor.getValue();

		assertEquals("TEST GENRE", savedGenre.getName());
	}

	@Test
	void testAddGenreAlreadyExists() {
		GenreForm genreForm = new GenreForm();
		genreForm.setName("Test Genre");

		when(genreRepository.findByName("TEST GENRE")).thenReturn(Optional.of(new Genre("Test Genre")));

		boolean result = inventoryService.addGenre(genreForm);

		assertFalse(result);

		verify(genreRepository).findByName("TEST GENRE");

		verify(genreRepository, never()).save(any());
	}
}
