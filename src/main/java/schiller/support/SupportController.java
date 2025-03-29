package schiller.support;

import org.hibernate.Hibernate;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import schiller.user.Customer;
import schiller.user.User;
import schiller.user.UserManagement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class SupportController {

	private final schiller.support.IssueManagement issueManagement;
	private final UserManagement userManagement;
	private final BusinessTime businessTime;

	@Autowired
	public SupportController(IssueManagement issueManagement, UserManagement userManagement, BusinessTime businessTime) {
		this.issueManagement = issueManagement;
		this.userManagement = userManagement;
        this.businessTime = businessTime;
    }


	@GetMapping("/support_user")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String supportUser(Model model, MessageForm form) {

		model.addAttribute("form",form);
		return "support_user";
	}

	@PostMapping("/support_user")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String openIssue(@Valid MessageForm form, Errors result, Model model,
							@LoggedIn Optional<UserAccount> userAccount) {

		if (result.hasErrors()) {
			return "support_user";
		}

		if (userAccount.isPresent()) {
			var account = userAccount.get();

			Customer user = userManagement.findCustomers().stream()
				.filter(customer -> customer.getUserAccount().equals(account))
				.findFirst().orElse(null);

			if (user != null) {
				Issue issue = issueManagement.createIssue(form.getTitle(), form.getContent(), user);

				model.addAttribute("customer", user);
				return "redirect:/support_chat/" + issue.getId();
			}
		}

		return "support_user";
	}



	@GetMapping("/support_chat/{issueId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String supportChat(@PathVariable Long issueId, Model model) {
		Issue issue = issueManagement.findIssue(issueId);
		if (issue == null) {
			return "redirect:/error-page";
		}

		model.addAttribute("issue", issue);
		model.addAttribute("messages", issue.getMessages());
		model.addAttribute("messageForm", new MessageForm());

		return "support_chat";
	}

	@PostMapping("/support_chat/{issueId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String message(@PathVariable Long issueId, Model model, @LoggedIn Optional <UserAccount> userAccount,
						  MessageForm form, Errors result) {

		if(result.hasErrors()){
			return "index";
		}
		Issue issue = issueManagement.findIssue(issueId);
		model.addAttribute("issueId", issueId);

		if (issue.isClosed()) {
			return "support_user_overview";
		}

		if(userAccount.isPresent()){

			var account = userAccount.get();

			Optional<Customer> user = userManagement.findCustomers().filter(
				customer ->
					customer.getUserAccount().equals(account)

			).stream().findFirst();
			if (user.isEmpty()){
				return "redirect:/support_chat/" + issueId;
			}
			Message message = new Message(form.getContent(), user.get(), businessTime.getTime());

			issue.addMessage(message);
			issueManagement.saveIssue(issue);

			model.addAttribute("customer", user);


			return "redirect:/support_chat/" + issueId;
		}

		return "redirect:/support_chat/" + issueId;

	}

	@GetMapping("/support_admin")
	@PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE')")
	@Transactional(readOnly = true)
	public String supportAdmin(Model model) {
		Iterable<Issue> issues = issueManagement.getAllIssues();
		Double rating = issueManagement.getAverageRating();
		issues.forEach(issue ->
			Hibernate.initialize(issue.getCustomer())
		);

		model.addAttribute("issues", issues);
		model.addAttribute("averageRating", rating);
		return "support_admin";
	}
	@GetMapping("/admin/support")
	@PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE')")
	public String listIssues(Model model) {
		Iterable<Issue> issues = issueManagement.getAllIssues();
		Double rating = issueManagement.getAverageRating();
		issues.forEach(issue -> {
			Hibernate.initialize(issue.getCustomer());
			Hibernate.initialize(issue.getContent());
		});
		model.addAttribute("issues", issues);
		model.addAttribute("averageRating", rating);
		return "support_admin";
	}

	@GetMapping("/admin/support_chat/{issueId}")
	@PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE')")
	public String viewIssue(@PathVariable Long issueId, Model model) {
		Issue issue = issueManagement.findIssue(issueId);
		if (issue == null) {
			return "redirect:/admin/support";
		}
		model.addAttribute("issue", issue);
		model.addAttribute("messages", issue.getMessages());
		model.addAttribute("messageForm", new MessageForm());
		return "admin_support_chat";
	}

	@PostMapping("/admin/support_chat/{issueId}")
	@PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE')")
	public String adminMessage(@PathVariable Long issueId, MessageForm form, Errors result, Model model,
							   @LoggedIn Optional<UserAccount> userAccount) {

		Issue issue = issueManagement.findIssue(issueId);
		model.addAttribute("issueId", issueId);
		model.addAttribute("issue", issue);
		if (result.hasErrors()) {
			return "redirect:/support_admin";
		}

		if (issue==null||issue.isClosed()) {
			return "support_admin";
		}

		if (userAccount.isPresent()) {
			User boss=null;
			for(User user:userManagement.findAll()){
				if(user.getUserAccount().hasRole(Role.of("BOSS"))){
					boss=user;
				}
			}
				if (boss != null) {
					Message message = new Message(form.getContent(), boss, businessTime.getTime());
					issue.addMessage(message);
					issueManagement.saveIssue(issue);
				}

		}
		return "redirect:/admin/support_chat/" + issueId;
	}

	@GetMapping("/support_user_overview")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Transactional(readOnly = true)
	public String supportOverview(Model model,@LoggedIn Optional<UserAccount> userAccount) {
		if(userAccount.isEmpty()){
			return "index";
		}
		List<Issue> issues = issueManagement.findIssueByUserAccount(userAccount.get());
		Map<Issue,Integer> newMessages = issueManagement.quantityOfNewMessages(issues, userAccount.get());
		model.addAttribute("newMessages", newMessages);
		model.addAttribute("issues", issues);
		return "support_user_overview";
	}

	@PostMapping("/admin/support_chat/closeIssue")
	@PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE')")
	public String closeIssue(@RequestParam("issueId") Long issueId, Model model) {

		Issue issue = issueManagement.findIssue(issueId);
		Double rating = issueManagement.getAverageRating();
		Iterable<Issue> issues = issueManagement.getAllIssues();

		model.addAttribute("issues", issues);
		model.addAttribute("averageRating", rating);

		if (issue.isClosed()) {
			return "support_admin";
		}
		issueManagement.closeIssue(issue);
		return "support_admin";
	}

	@PostMapping("/support_chat/rate_support")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String rateSupport(@RequestParam("issueId") Long issueId, @RequestParam("rating") int rating, Model model,
							  @LoggedIn Optional<UserAccount> userAccount){
		if(userAccount.isEmpty()){
			return "index";
		}
		Issue issue = issueManagement.findIssue(issueId);
		issueManagement.rateSupport(issue,rating);
		List<Issue> issues = issueManagement.findIssueByUserAccount(userAccount.get());
		model.addAttribute("issues", issues);
		return "redirect:/support_chat/"+issueId;
	}
}






