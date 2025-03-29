package schiller.inventory.productSpecialization.book;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import schiller.inventory.addItem.BookForm;
import schiller.inventory.productSpecialization.book.author.Author;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.comment.Comment;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
public class BookTest {

	@Test
	void testGetterAndSetter() {

		Book testBook = new Book();
		String testName = "test";
		String testImage = "test";
		Money testPrice = Money.of(5, "EUR");
		String testIsbn = "test";
		String testPublisher = "test";
		String testCovertext = "test";
		Author testAuthor = new Author("test");
		List<Genre> testGenres = new ArrayList<>(Collections.singletonList(new Genre("test")));
		int testQuantity = 3;


		testBook.setName(testName);
		testBook.setImage(testImage);
		testBook.setPrice(testPrice);
		testBook.setIsbn(testIsbn);
		testBook.setPublisher(testPublisher);
		testBook.setCoverText(testCovertext);
		testBook.setAuthor(testAuthor);
		testBook.setGenres(testGenres);

		String retrievedName = testBook.getName();
		String retrievedImage = testBook.getImage();
		MonetaryAmount retrievedPrice = testBook.getPrice();
		String retrievedIsbn = testBook.getIsbn();
		String retrievedPulisher = testBook.getPublisher();
		String retrievedCovertext = testBook.getCoverText();
		Author retrievedAuthor = testBook.getAuthor();
		List<Genre> retrievedGenres = testBook.getGenres();


		Assertions.assertEquals(testName, retrievedName, "The getter or setter for name is not working as expected.");
		Assertions.assertEquals(testImage, retrievedImage, "The getter or setter for image is not working as expected.");
		Assertions.assertEquals(testPrice, retrievedPrice, "The getter or setter for price is not working as expected.");
		Assertions.assertEquals(testIsbn, retrievedIsbn, "The getter or setter for isbn is not working as expected.");
		Assertions.assertEquals(testPublisher, retrievedPulisher, "The getter or setter for publisher is not working as expected.");
		Assertions.assertEquals(testCovertext, retrievedCovertext, "The getter or setter for covertext is not working as expected.");
		Assertions.assertEquals(testAuthor, retrievedAuthor, "The getter or setter for author is not working as expected.");
		Assertions.assertEquals(testGenres, retrievedGenres, "The getter or setter for genres is not working as expected.");
	}
}
