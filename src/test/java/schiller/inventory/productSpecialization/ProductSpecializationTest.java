package schiller.inventory.productSpecialization;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import schiller.AbstractIntegrationTests;
import schiller.inventory.productSpecialization.comment.Comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.time.LocalDateTime;

@SpringBootTest
class ProductSpecializationTest extends AbstractIntegrationTests {

	@Test
	void testGetAverageRating() throws Exception {

		ProductSpecialization productSpecialization = new ProductSpecialization("test", Money.of(5, "EUR"));

		LocalDateTime timeOfComment = LocalDateTime.now();
		Comment comment = new Comment("test", 5, timeOfComment);

		productSpecialization.addComment(comment);

		assertEquals("unlucky", productSpecialization.getAverageRating(), "⭐⭐⭐⭐⭐");
	}
}
