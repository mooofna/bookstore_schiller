package schiller.support;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schiller.user.User;

import static org.junit.jupiter.api.Assertions.*;

class MessageFormTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testMessageFormCreation() {
		User author = new User();
		MessageForm messageForm = new MessageForm("Title", "Content", author);

		assertEquals("Title", messageForm.getTitle());
		assertEquals("Content", messageForm.getContent());
		assertEquals(author, messageForm.getAuthor());
	}

	@Test
	void testEmptyTitle() {
		MessageForm messageForm = new MessageForm("", "Content", new User());

		var constraintViolations = validator.validate(messageForm);

		assertFalse(constraintViolations.isEmpty());
		assertTrue(constraintViolations.stream()
			.anyMatch(violation -> violation.getPropertyPath().toString().equals("title")));
	}

	@Test
	void testEmptyContent() {
		MessageForm messageForm = new MessageForm("Title", "", new User());

		var constraintViolations = validator.validate(messageForm);

		assertFalse(constraintViolations.isEmpty());
		assertTrue(constraintViolations.stream()
			.anyMatch(violation -> violation.getPropertyPath().toString().equals("content")));
	}

	@Test
	void testValidMessageForm() {
		User author = new User();
		MessageForm messageForm = new MessageForm("Title", "Content", author);

		var constraintViolations = validator.validate(messageForm);

		assertTrue(constraintViolations.isEmpty());
	}
}
