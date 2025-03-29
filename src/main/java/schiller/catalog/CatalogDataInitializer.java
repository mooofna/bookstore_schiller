package schiller.catalog;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import schiller.inventory.productSpecialization.book.author.Author;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.book.genre.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.UUID;

@Component("CatalogDataInitializer")
@Order(10)
class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);

	private final BookCatalog bookCatalog;

	private final GenreRepository genreRepository;

	CatalogDataInitializer(BookCatalog bookCatalog, GenreRepository genreRepository) {

		Assert.notNull(bookCatalog, "BookCatalog must not be null!");
		Assert.notNull(genreRepository, "GenreRepository must not be null!");

		this.bookCatalog = bookCatalog;
		this.genreRepository = genreRepository;
	}

	@Override
	public void initialize() {

		if (bookCatalog.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");

		List<Genre> genres = new ArrayList<>();
		Iterable<Genre> genresIt = genreRepository.findAll();
		genresIt.forEach(genres::add);

		bookCatalog.save(new Book("The Hobbit", "th.png", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"George Allen & Unwin", "A fantasy adventure story of a hobbit on a quest to reclaim a treasure from a dragon.",
			new Author("J.R.R Tolkien"),
			new ArrayList<>(Collections.singletonList(genres.get(0)))));

		bookCatalog.save(new Book("Ender's Game", "eg.png", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"Tor Books", "A science fiction novel about a young boy trained to defend humanity against alien attacks.",
			new Author("Orson Scott Card"),
			new ArrayList<>(Collections.singletonList(genres.get(0)))));

		bookCatalog.save(new Book("Normal People", "np.png", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"Faber and Faber", "A contemporary story exploring the complexities of love, life, and social dynamics.",
			new Author("Sally Rooney"),
			new ArrayList<>(Collections.singletonList(genres.get(2)))));

		bookCatalog.save(new Book("Dune", "dun.png", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"Chilton Books", "A science fiction saga set in a distant future amidst a huge interstellar empire.",
			new Author("Frank Herbert"),
			new ArrayList<>(Collections.singletonList(genres.get(1)))));

		bookCatalog.save(new Book("Norwegian Wood", "nw.png", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"Kodansha", "A poignant story of one man's deep longing for love and his journey to maturity.",
			new Author("Haruki Murakami"),
			new ArrayList<>(Collections.singletonList(genres.get(2)))));

		bookCatalog.save(new Book("Pride and Prejudice", "pap.png", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"T. Egerton, Whitehall", "A classic novel of manners, love, and marriage in early 19th-century England.",
			new Author("Jane Austen"),
			new ArrayList<>(Collections.singletonList(genres.get(1)))));

		bookCatalog.save(new Book("Tokyo Ghoul", "tg.png", Money.of(9.99, "EUR"), UUID.randomUUID().toString(),
			"Shueisha", "A dark fantasy manga about a college student who becomes a half-ghoul after a deadly encounter.",
			new Author("Sui Ishida"),
			new ArrayList<>(Collections.singletonList(genres.get(3)))));

		bookCatalog.save(new Book("The Knife of Never Letting Go", "tkonlg.png", Money.of(9.99, "EUR"),
			UUID.randomUUID().toString(),
			"Walker Books", "A dystopian novel following a boy and his dog as they discover a world of lies.",
			new Author("Patrick Ness"),
			new ArrayList<>(Collections.singletonList(genres.get(3)))));
	}
}
