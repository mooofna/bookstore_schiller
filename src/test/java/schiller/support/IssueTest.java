package schiller.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import schiller.user.Customer;
import org.springframework.data.util.Streamable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import schiller.AbstractIntegrationTests;

import static org.junit.jupiter.api.Assertions.*;

class IssueTest {

	private Issue issue;
	@Mock
	private Customer customer;
	@Mock
	private Message message;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		issue = new Issue("Test Issue", customer);
	}

	@Test
	void testIssueCreation() {
		assertNotNull(issue);
		assertEquals("Test Issue", issue.getTitle());
		assertEquals(customer, issue.getCustomer());
		assertFalse(issue.isClosed());
		assertNull(issue.getRating());
	}

	@Test
	void testAddMessage() {
		issue.addMessage(message);
		assertFalse(issue.getMessages().isEmpty());
		assertEquals(1, issue.getMessages().size());
		assertEquals(message, issue.getMessages().get(0));
	}

	@Test
	void testCloseIssue() {
		issue.closeIssue();
		assertTrue(issue.isClosed());
	}

	@Test
	void testSetRating() {
		issue.setRating(5);
		assertEquals(5, issue.getRating());


		issue.setRating(10);
		assertEquals(5, issue.getRating());
	}
}
