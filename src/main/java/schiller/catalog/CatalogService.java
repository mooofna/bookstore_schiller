package schiller.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Range;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.data.util.Streamable;

import org.springframework.ui.Model;
import org.springframework.util.Assert;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.inventory.productSpecialization.book.author.Author;
import schiller.inventory.productSpecialization.productExtension.category.Category;
import schiller.inventory.productSpecialization.productExtension.category.CategoryRepository;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.book.genre.GenreRepository;
import schiller.inventory.productSpecialization.comment.Comment;
import schiller.inventory.productSpecialization.productExtension.ProductExtension;
import schiller.inventory.productSpecialization.productExtension.ProductExtensionCatalog;
import schiller.inventory.productSpecialization.ProductSpecialization;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CatalogService {

	private static final Quantity NONE = Quantity.of(0);

	private final BookCatalog bookCatalog;
	private final ProductExtensionCatalog productExtensionCatalog;

	private final GenreRepository genreRepository;

	private final CategoryRepository categoryRepository;

	private final UniqueInventory<UniqueInventoryItem> inventory;

	private final BusinessTime businessTime;

	public CatalogService(BookCatalog bookCatalog,
						  ProductExtensionCatalog productExtensionCatalog,
                          GenreRepository genreRepository,
                          CategoryRepository categoryRepository,
                          UniqueInventory<UniqueInventoryItem> inventory,
                          BusinessTime businessTime) {

        Assert.notNull(bookCatalog, "BookCatalog must not be Null");
		Assert.notNull(productExtensionCatalog, "ProductExtensionCatalog must not be Null");
		Assert.notNull(genreRepository, "GenreRepository must not be Null");
		Assert.notNull(categoryRepository, "CategoryRepository must not be Null");
		Assert.notNull(inventory, "Inventory must not be Null");
		Assert.notNull(businessTime, "BusinessTime must not be Null");

		this.bookCatalog = bookCatalog;
		this.productExtensionCatalog = productExtensionCatalog;
		this.genreRepository = genreRepository;
		this.categoryRepository = categoryRepository;
		this.inventory = inventory;
		this.businessTime = businessTime;
	}

	public Streamable<? extends ProductSpecialization> getCatalog(Long genreId, String categoryId) {
		if (null == categoryId) {
			return catalogByGenreId(genreId);
		}
		return productExtensionCatalog.findByCategory(categoryRepository.findById(categoryId).orElseThrow());
	}

	public Streamable<Book> catalogByGenreId(Long genreId) {
		if(null == genreId) {
			return bookCatalog.findAll();
		}
		return bookCatalog.findAllByGenresContains(genreRepository.findById(genreId).orElseThrow());
	}

	public Streamable<Book> catalogByAuthor(Author author) {
		Assert.notNull(author, "Author must not be Null");

		return bookCatalog.findAllByAuthor(author);
	}

	public Iterable<Genre> allGenre() {
		return genreRepository.findAll();
	}

	public Iterable<Category> allCategories() {
		return categoryRepository.findAll();
	}

	public Quantity quantityOfProduct(Product product) {
		return inventory.findByProductIdentifier(Objects.requireNonNull(product.getId()))
			.map(InventoryItem::getQuantity)
			.orElse(NONE);
	}

	public boolean productIsOrderable(Quantity quantity) {
		Assert.notNull(quantity, "quantity must not be Null");
		return quantity.isGreaterThan(NONE);
	}

	public void addCommentToProduct(ProductSpecialization product, @Valid CommentAndRating form, UserAccount userAccount) {
		Assert.notNull(product, "product must not be Null");
		Assert.notNull(userAccount, "userAccount must not be Null");

		product.addComment(form.toComment(businessTime.getTime(), userAccount));
		if (product instanceof Book book) {
			bookCatalog.save(book);
		}
		if (product instanceof ProductExtension productExtension) {
			productExtensionCatalog.save(productExtension);
		}
	}

	public interface CommentAndRating {

		@NotEmpty
		String getComment();

		@Range(min = 1, max = 5)
		Integer getRating();

		default Comment toComment(LocalDateTime time, UserAccount user) {
			return new Comment(getComment(), getRating(), time, user);
		}
	}

	public Streamable<Book> getOtherBooksOfSameGenre(ProductSpecialization product) {
		if (!(product instanceof Book book)) {
			return Streamable.of();
		}
		Streamable<Book> sameGenreBooks = Streamable.of();
		for (Genre genre : book.getGenres()) {
			Streamable<Book> booksByGenre = catalogByGenreId(genre.getId());
			sameGenreBooks = Streamable.of(Stream.concat(sameGenreBooks.stream(),
				booksByGenre.stream()).collect(Collectors.toList()));
		}

		return Streamable.of(sameGenreBooks.stream().filter(element ->
			!element.equals(book)).collect(Collectors.toList()));
	}

	public Streamable<Book> getOtherBooksOfSameAuthor(ProductSpecialization product) {
		if (!(product instanceof Book book)) {
			return Streamable.of();
		}
		Streamable<Book> sameAuthorBooks = catalogByAuthor(book.getAuthor());
		return Streamable.of(sameAuthorBooks.stream().filter(element ->
			!element.equals(book)).collect(Collectors.toList()));
	}

	public Streamable<ProductExtension> catalogByCategory(Category category) {
		Assert.notNull(category, "Category must not be Null");

		return productExtensionCatalog.findByCategory(category);
	}

	public Streamable<ProductExtension> getOtherProductExtensionsOfSameCategory(ProductSpecialization product) {
		if (!(product instanceof ProductExtension productExtension)) {
			return Streamable.of();
		}

		Streamable<ProductExtension> sameCategoryProductExtensions = catalogByCategory(productExtension.getCategory());
		return Streamable.of(sameCategoryProductExtensions.stream().filter(element ->
			!element.equals(productExtension)).collect(Collectors.toList()));
	}

	public Streamable<? extends ProductSpecialization> getFilteredCatalog(String productType, Integer price) {
		Streamable<? extends ProductSpecialization> catalog = Streamable.of();
		if(productType.equals("Book")) {
			catalog = bookCatalog.findAll();
		}
		if(productType.equals("ProductExtension")) {
			catalog = productExtensionCatalog.findAll();
		}
		return null == price ? catalog : this.filterCatalogByPrice(catalog, price);
	}

	private Streamable<? extends ProductSpecialization>
		filterCatalogByPrice(Streamable<? extends ProductSpecialization> catalog,
							 int price) {

		return catalog.filter(productSpecialization ->
			productSpecialization.getPrice().isGreaterThanOrEqualTo(Money.of(price, "EUR")) &&
			(price == 20 || productSpecialization.getPrice().isLessThan(Money.of(2 * price, "EUR"))));
	}

	public void prepareCatalogTemplate(Model model, Streamable<? extends ProductSpecialization> catalog) {
		Iterable<Genre> genres = this.allGenre();
		Iterable<Category> categories = this.allCategories();

		model.addAttribute("catalog", catalog);
		model.addAttribute("genres", genres);
		model.addAttribute("categories", categories);
		model.addAttribute("categoriesEmpty", categories.spliterator().getExactSizeIfKnown() == 0);
		model.addAttribute("title", "catalog.title");
	}
}
