package schiller.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.useraccount.UserAccount;
import schiller.user.Customer;
import org.springframework.data.util.Streamable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IssueManagementTest {

	@Mock
	private IssueRepository issueRepository;

	private IssueManagement issueManagement;

	@Mock
	private Customer customer;
	@Mock
	private UserAccount userAccount;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		issueManagement = new IssueManagement(issueRepository);
	}

	@Test
	void testCreateIssue() {
		Issue issue = new Issue("Test Issue", customer);
		when(issueRepository.save(any(Issue.class))).thenReturn(issue);

		Issue createdIssue = issueManagement.createIssue("Test Issue", "Test Content", customer);

		assertNotNull(createdIssue);
		assertEquals("Test Issue", createdIssue.getTitle());
		assertEquals(customer, createdIssue.getCustomer());
	}

	@Test
	void testCloseIssue() {
		Issue issue = new Issue("Test Issue", customer);
		issueManagement.closeIssue(issue);
		assertTrue(issue.isClosed());
	}

	@Test
	void testRateSupport() {
		Issue issue = new Issue("Test Issue", customer);
		issueManagement.rateSupport(issue, 5);
		assertEquals(5, issue.getRating());
	}



	@Test
	void testFindIssue() {
		Issue issue = new Issue("Test Issue", customer);
		when(issueRepository.findById(anyLong())).thenReturn(Optional.of(issue));

		Issue foundIssue = issueManagement.findIssue(1L);

		assertNotNull(foundIssue);
		assertEquals("Test Issue", foundIssue.getTitle());
	}



	@Test
	void testSaveIssue() {
		Issue issue = new Issue("Test Issue", customer);
		when(issueRepository.save(any(Issue.class))).thenReturn(issue);

		Issue savedIssue = issueManagement.saveIssue(issue);

		assertNotNull(savedIssue);
		assertEquals("Test Issue", savedIssue.getTitle());
	}

}
