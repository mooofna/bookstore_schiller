package schiller.inventory.productSpecialization.book;


import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Component;
import schiller.inventory.productSpecialization.book.author.Author;
import schiller.inventory.productSpecialization.book.genre.Genre;

@Component("BookCatalog")
public interface BookCatalog extends Catalog<Book> {

	Sort DEFAULT_SORT = Sort.sort(Book.class).by(Book::getId).descending();

	Streamable<Book> findAllByGenresContains(Genre genre, Sort sort);

	Streamable<Book> findAllByAuthor(Author author, Sort sort);

	default Streamable<Book> findAllByAuthor(Author author) {
		return findAllByAuthor(author, DEFAULT_SORT);
	}

	Streamable<Book> findByIsbn(String isbn, Sort sort);

	default Streamable<Book> findAllByGenresContains(Genre genre) {
		return findAllByGenresContains(genre, DEFAULT_SORT);
	}

	default Streamable<Book> findByIsbn(String isbn) {
		return findByIsbn(isbn, DEFAULT_SORT);
	}
}
