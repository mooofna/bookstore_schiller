package schiller.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;

@AutoConfigureMockMvc
@SpringBootTest
class SupportControllerTest {


	@InjectMocks
	@Autowired
	private SupportController supportController;


	@MockBean
	private IssueManagement issueManagement;

	@Mock
	private Model model;

	@Mock
	private UserAccount userAccount;

	@Mock
	private Issue issue;

	@Mock
	private Errors errors;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void testSupportUser() {
		MessageForm form = new MessageForm();
		String viewName = supportController.supportUser(model, form);

		verify(model).addAttribute("form", form);
		assertEquals("support_user", viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void testSupportChat() {
		Long issueId = 1L;
		Issue mockIssue = mock(Issue.class);
		when(issueManagement.findIssue(issueId)).thenReturn(mockIssue);
		String viewName = supportController.supportChat(issueId, model);
		verify(model).addAttribute("issue", mockIssue);
		verify(model).addAttribute(eq("messages"), any());
		verify(model).addAttribute(eq("messageForm"), any(MessageForm.class));
		assertEquals("support_chat", viewName);
		when(issueManagement.findIssue(issueId)).thenReturn(null);
		String viewNameWithoutIssue = supportController.supportChat(issueId, model);
		assertEquals("redirect:/error-page", viewNameWithoutIssue);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void testSupportUserOverview() {
		Optional<UserAccount> userAccount = Optional.of(mock(UserAccount.class));

		String viewName = supportController.supportOverview(model, userAccount);

		assertEquals("support_user_overview", viewName);

		String viewNameNoUserAccount = supportController.supportOverview(model,Optional.empty());

		assertEquals("index",viewNameNoUserAccount);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "BOSS")
	void testSupportAdmin() {
		when(issueManagement.getAllIssues()).thenReturn(mock(Iterable.class));
		when(issueManagement.getAverageRating()).thenReturn(5.0);

		String viewName = supportController.supportAdmin(model);

		verify(model).addAttribute(eq("issues"), any());
		verify(model).addAttribute("averageRating", 5.0);
		assertEquals("support_admin", viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "BOSS")
	void testListIssues() {
		when(issueManagement.getAllIssues()).thenReturn(mock(Iterable.class));
		when(issueManagement.getAverageRating()).thenReturn(5.0);

		String viewName = supportController.listIssues(model);

		verify(model).addAttribute(eq("issues"), any());
		verify(model).addAttribute("averageRating", 5.0);
		assertEquals("support_admin", viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "BOSS")
	void testViewIssue() {
		Long issueId = 2L;
		Issue mockIssue = mock(Issue.class);
		when(issueManagement.findIssue(issueId)).thenReturn(mockIssue);

		String viewName = supportController.viewIssue(issueId, model);

		verify(model).addAttribute("issue", mockIssue);
		verify(model).addAttribute(eq("messages"), any());
		verify(model).addAttribute(eq("messageForm"), any(MessageForm.class));
		assertEquals("admin_support_chat", viewName);
		when(issueManagement.findIssue(issueId)).thenReturn(null);
		String viewNameWithoutIssue = supportController.viewIssue(issueId, model);
		assertEquals("redirect:/admin/support", viewNameWithoutIssue);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "BOSS")
	void testCloseIssue() {
		Long issueId = 3L;
		Issue mockIssue = mock(Issue.class);
		when(issueManagement.findIssue(issueId)).thenReturn(mockIssue);
		when(mockIssue.isClosed()).thenReturn(false);

		String viewName = supportController.closeIssue(issueId, model);

		verify(issueManagement).closeIssue(mockIssue);
		verify(model).addAttribute(eq("issues"), any());
		verify(model).addAttribute(eq("averageRating"), anyDouble());
		assertEquals("support_admin", viewName);
		when(issue.isClosed()).thenReturn(true);
		String viewNameClosedIssue = supportController.closeIssue(issueId, model);
		assertEquals("support_admin", viewNameClosedIssue);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void testOpenIssue() {
		MessageForm form = new MessageForm();
		form.setContent("Content");
		form.setTitle("Title");
		String viewNameWithoutUserAccount = supportController.openIssue(form, errors, model, Optional.empty());
		assertEquals("support_user", viewNameWithoutUserAccount);
		when(errors.hasErrors()).thenReturn(true);
		String viewNameWithErrors = supportController.openIssue(form, errors, model, Optional.of(userAccount));
		assertEquals("support_user", viewNameWithErrors);
		when(errors.hasErrors()).thenReturn(false);
		String viewName = supportController.openIssue(form, errors, model, Optional.of(userAccount));
		assertEquals("support_user", viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void testOpenIssueUserNotPresent() {
		MessageForm form = new MessageForm();

		String viewName = supportController.openIssue(form, errors, model, Optional.empty());

		verify(errors, never()).reject(anyString());
		assertEquals("support_user", viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void testOpenIssueWithErrorsShouldReturnSupportUserView() {
		// Mocking the MessageForm
		MessageForm form = new MessageForm();
		form.setContent("Content");
		form.setTitle("Title");

		// Mocking that there are errors
		when(errors.hasErrors()).thenReturn(true);

		// Invoke the method
		String viewName = supportController.openIssue(form, errors, model, Optional.empty());

		// Verify that the method returns the expected view name
		assertEquals("support_user", viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "BOSS")
	void testAdminMessage() {
		MessageForm form = new MessageForm();
		form.setContent("Content");
		form.setTitle("Title");
		long issueId = 1L;

		String viewNameWithoutIssueManagement = supportController.adminMessage(issueId,form, errors, model, Optional.empty());
		assertEquals("support_admin", viewNameWithoutIssueManagement);
		when(errors.hasErrors()).thenReturn(true);
		String viewNameWithErrors = supportController.adminMessage(issueId,form, errors, model, Optional.empty());
		assertEquals("redirect:/support_admin", viewNameWithErrors);
		when(issueManagement.findIssue(issueId)).thenReturn(issue);
		when(errors.hasErrors()).thenReturn(false);
		when(issue.isClosed()).thenReturn(true);
		String viewNameWithClosedIssue = supportController.adminMessage(issueId,form, errors, model, Optional.empty());
		assertEquals("support_admin", viewNameWithClosedIssue);
		when(issue.isClosed()).thenReturn(false);
		String viewName = supportController.adminMessage(issueId,form, errors, model, Optional.empty());
		assertEquals("redirect:/admin/support_chat/" + issueId, viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "BOSS")
	void testAdminMessageWithErrorsShouldReturnSupportUserView() {
		MessageForm form = new MessageForm();
		form.setContent("Content");
		form.setTitle("Title");
		Long issueId = 1L;

		// Mocking that there are errors
		when(errors.hasErrors()).thenReturn(true);

		// Invoke the method
		String viewName = supportController.adminMessage(issueId,form, errors, model, Optional.empty());
		verify(model).addAttribute("issueId",issueId);
		assertEquals("redirect:/support_admin", viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void testRateSupport(){
		Long issueId = 1L;
		String viewNameWithoutUserAccount = supportController.rateSupport(issueId,1, model, Optional.empty());
		assertEquals("index", viewNameWithoutUserAccount);
		when(issueManagement.findIssue(issueId)).thenReturn(issue);
		String viewName = supportController.rateSupport(issueId,1, model, Optional.of(userAccount));
		assertEquals("redirect:/support_chat/"+issueId, viewName);
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void testMessage(){
		MessageForm form = new MessageForm();
		form.setContent("Content");
		Long issueId = 1L;
		when(errors.hasErrors()).thenReturn(true);
		String viewNameWithErrors = supportController.message(issueId, model, Optional.of(userAccount), form, errors);
		assertEquals("index", viewNameWithErrors);
		when(errors.hasErrors()).thenReturn(false);
		when(issueManagement.findIssue(issueId)).thenReturn(issue);
		when(issue.isClosed()).thenReturn(true);
		String viewNameWithClosedIssue = supportController.message(issueId, model, Optional.of(userAccount), form, errors);
		verify(model).addAttribute("issueId",issueId);
		assertEquals("support_user_overview", viewNameWithClosedIssue);
		when(issue.isClosed()).thenReturn(false);
		String viewNameWithoutUserAccount = supportController.message(issueId, model, Optional.empty(), form, errors);
		assertEquals("redirect:/support_chat/" + issueId, viewNameWithoutUserAccount);
		String viewNameWithoutUser = supportController.message(issueId, model, Optional.of(userAccount), form, errors);
		assertEquals("redirect:/support_chat/" + issueId, viewNameWithoutUser);
	}

}
