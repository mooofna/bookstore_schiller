package schiller.search;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import schiller.inventory.productSpecialization.ProductSpecialization;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.book.genre.GenreRepository;
import schiller.inventory.productSpecialization.productExtension.ProductExtension;
import schiller.inventory.productSpecialization.productExtension.ProductExtensionCatalog;
import schiller.inventory.productSpecialization.productExtension.category.Category;
import schiller.inventory.productSpecialization.productExtension.category.CategoryRepository;
import schiller.order.OrderManagementService;
import schiller.order.SchillerOrder;
import schiller.user.User;
import schiller.user.UserManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchService {

	private final BookCatalog bookCatalog;

	private final ProductExtensionCatalog productExtensionCatalog;

	private final GenreRepository genreRepository;

	private final CategoryRepository categoryRepository;

	private final OrderManagementService orderManagementService;

	private final UserManagement userManagement;

	SearchService(BookCatalog bookCatalog,
				  ProductExtensionCatalog productExtensionCatalog,
				  GenreRepository genreRepository,
				  CategoryRepository categoryRepository,
				  OrderManagementService orderManagementService,
				  UserManagement userManagement) {

		Assert.notNull(bookCatalog, "bookCatalog must not be null");
		Assert.notNull(productExtensionCatalog, "productExtensionCatalog must not be null");
		Assert.notNull(genreRepository, "genreRepository must not be null");
		Assert.notNull(categoryRepository, "categoryRepository must not be null");
		Assert.notNull(orderManagementService, "orderManagementService must not be null");
		Assert.notNull(userManagement, "userManagement must not be null");

		this.bookCatalog = bookCatalog;
		this.productExtensionCatalog = productExtensionCatalog;
		this.genreRepository = genreRepository;
		this.categoryRepository = categoryRepository;
		this.orderManagementService = orderManagementService;
		this.userManagement = userManagement;
	}

	public Iterable<Genre> allGenre() {
		return genreRepository.findAll();
	}

	public Iterable<Category> allCategories() {
		return categoryRepository.findAll();
	}

	public Streamable<ProductSpecialization> evaluateSearchTerm(String searchTerm) {
		if(null == searchTerm) {
			return null;
		}
		return doSearch(searchTerm);
	}

    private Streamable<ProductSpecialization> doSearch(String searchTerm) {
		Streamable<Book> bookResults = this.searchBooks(searchTerm);
        Streamable<ProductExtension> productExtensionResults = this.searchProductExtensions(searchTerm);
		return Streamable.of(Stream.concat(bookResults.stream(), productExtensionResults.stream()).toList());
    }

	private Streamable<Book> searchBooks(String searchTerm) {
		Streamable<Book> catalogStream = bookCatalog.findAll();

		Streamable<Book> titleMatch = catalogStream.filter( book ->
			book.getName().toLowerCase().contains( searchTerm.toLowerCase() ));

		Streamable<Book> authorMatch = catalogStream.filter( book ->
			book.getAuthor().getName().toLowerCase().contains( searchTerm.toLowerCase() ) &&
				!titleMatch.toList().contains(book));

		Streamable<Book> genreMatch = catalogStream.filter( book ->
			book.getGenres().stream().map(genre ->
				genre.getName().toLowerCase()).toList().contains( searchTerm.toLowerCase() ) &&
				!titleMatch.toList().contains(book) && !authorMatch.toList().contains(book));

		return Streamable.of(Stream.concat(Stream.concat(titleMatch.stream(), authorMatch.stream()),
			genreMatch.stream()).toList());
	}

	private Streamable<ProductExtension> searchProductExtensions(String searchTerm) {
		Streamable<ProductExtension> catalogStream = productExtensionCatalog.findAll();

		Streamable<ProductExtension> titleMatch = catalogStream.filter( productExtension ->
			productExtension.getName().toLowerCase().contains( searchTerm.toLowerCase() ));

		Streamable<ProductExtension> categoryMatch = catalogStream.filter( productExtension ->
			productExtension.getCategory().getName().toLowerCase().contains( searchTerm.toLowerCase() ) &&
			!titleMatch.toList().contains(productExtension));

		Streamable<ProductExtension> infoMatch = catalogStream.filter( productExtension ->
			productExtension.getInfo().toLowerCase().contains( searchTerm.toLowerCase() ) &&
			!titleMatch.toList().contains(productExtension) && !categoryMatch.toList().contains(productExtension));

		return Streamable.of(Stream.concat(Stream.concat(titleMatch.stream(), categoryMatch.stream()),
			infoMatch.stream()).toList());
	}

	public List<SchillerOrder> searchOrders(String searchTerm) {
		Assert.hasText(searchTerm, "Search term must not be empty!");

		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		Streamable<SchillerOrder> orders = Streamable.of(orderManagementService.getAllOrders(pageable).stream()
			.filter(order -> orderManagementService.getName(order).contains(searchTerm))
			.collect(Collectors.toList()));

		return orderManagementService.updatedOrders(orders);
	}

	public List<User> searchUsers(String searchTerm) {
		Assert.hasText(searchTerm, "Search term must not be empty!");

		return userManagement.findAll().stream()
			.filter(user -> user.getUserAccount().getUsername().contains(searchTerm))
			.collect(Collectors.toList());
	}

	Map<UserAccount, Integer> getUserOpenOrdersCount(List<User> users) {
		Map<UserAccount, Integer> userOpenOrdersCount = new HashMap<>();

		for (User user : users) {
			List<SchillerOrder> orders = orderManagementService.getOpenedOrdersByUser(user.getUserAccount());
			userOpenOrdersCount.put(user.getUserAccount(), orders.size());
		}

		return userOpenOrdersCount;
	}

}
