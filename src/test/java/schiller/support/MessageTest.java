package schiller.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.useraccount.UserAccount;
import schiller.user.User;


import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class MessageTest {

	@Mock
	private User author;

	@Mock
	private UserAccount userAccount;

    @BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testMessageCreation() {
		when(author.getUserAccount()).thenReturn(userAccount);
		String content = "Hello World";
		Message message = new Message(content, author, LocalDateTime.now());

		assertEquals(content, message.getContent());
		assertEquals(author, message.getAuthor());
		assertNotNull(message.getMessageTime());
	}

	@Test
	void testIsCustomerTrue() {
		when(author.getUserAccount()).thenReturn(userAccount);
		when(userAccount.hasRole(Message.CUSTOMER_ROLE)).thenReturn(true);

		Message message = new Message("Test", author, LocalDateTime.now());

		assertTrue(message.isCustomer());
	}

	@Test
	void testIsCustomerFalse() {
		when(author.getUserAccount()).thenReturn(userAccount);
		when(userAccount.hasRole(Message.CUSTOMER_ROLE)).thenReturn(false);

		Message message = new Message("Test", author, LocalDateTime.now());

		assertFalse(message.isCustomer());
	}
}
