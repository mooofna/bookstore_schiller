package schiller.inventory.productSpecialization.book;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.Money;

import schiller.inventory.productSpecialization.book.author.Author;
import org.springframework.util.Assert;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.ProductSpecialization;

@Getter
@Setter
@Entity
public class Book extends ProductSpecialization {

	private String image, isbn, publisher, coverText;

	@ManyToOne(cascade=CascadeType.ALL)
	private Author author;

	@ManyToMany(cascade=CascadeType.ALL)
	private List<Genre> genres = new ArrayList<>();

	public Book(String name, String image, Money price, String isbn, String publisher, String coverText,
				Author author, ArrayList<Genre> genres) {

		super(name, price);

		Assert.hasLength(image, "image must not be Null and must not be the empty String");
		Assert.hasLength(isbn, "isbn must not be Null and must not be the empty String");
		Assert.hasLength(publisher, "publisher must not be Null and must not be the empty String");
		Assert.hasLength(coverText, "coverText must not be Null and must not be the empty String");
		Assert.notNull(author, "author must not be Null");
		Assert.notEmpty(genres, "genres must not be empty");

		this.image = image;
		this.isbn = isbn;
		this.publisher = publisher;
		this.coverText = coverText;
		this.author = author;
		this.genres = genres;
	}

	public Book() {}
}
