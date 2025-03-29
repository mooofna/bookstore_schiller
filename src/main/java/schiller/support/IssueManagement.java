package schiller.support;

import com.mysema.commons.lang.Assert;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schiller.user.Customer;

import java.util.*;


@Service
@Transactional
public class IssueManagement {

	private final IssueRepository issues;

	public IssueManagement(IssueRepository issues) {
		this.issues = issues;
	}


	public Issue createIssue(String title, String content, Customer customer)  {
		Assert.notNull(title, "Title must not be null!");
		Assert.notNull(content, "Content must not be null!");
		Assert.notNull(customer, "Customer must not be null!");

		var issue = new Issue(title, customer);
		issue.setContent(content);

		return issues.save(issue);
	}


	public void closeIssue(Issue issue){
		issue.closeIssue();
	}

	public void rateSupport(Issue issue, int rating){
		issue.setRating(rating);
		saveIssue(issue);
	}

	public Double getAverageRating() {
		Iterable<Issue> allIssues =issues.findAll();
		double averageRating = 0;
		int size = 0;
		for(Issue issue : allIssues){
			if (issue.getRating()!=null){
			averageRating = averageRating + issue.getRating();
			}
			size++;
		}
		if(size!=0){
			averageRating = averageRating / size;
		}

		return averageRating;
	}

	public Issue findIssue(Long issueId){
		Optional<Issue> issue = issues.findById(issueId);
        return issue.orElse(null);

    }
	@Transactional
	public Iterable<Issue> getAllIssues() {
		return issues.findAllEagerly();
	}

	public Issue saveIssue(Issue issue) {
		Assert.notNull(issue, "Issue must not be null!");
		return issues.save(issue);
	}

	public List<Issue> findIssueByUserAccount(UserAccount userAccount){
		Iterable<Issue> allIssues = issues.findAll();
		List<Issue> usersIssues = new ArrayList<>();
		for(Issue i:allIssues){
			if(i.getCustomer().getUserAccount().equals(userAccount)){
                usersIssues.add(i);
			}
		}
		return usersIssues;
	}

	public Map<Issue,Integer> quantityOfNewMessages(List<Issue> issues, UserAccount userAccount){
		Map<Issue,Integer> newMessages = new HashMap<>();
		for(Issue i : issues){
			int z = 0;
			List<Message> messages = i.getMessages();
			for(Message message : messages){
				if (message.getAuthor().getUserAccount().equals(userAccount)){
					z=0;
				} else{
					z++;
				}
			}
			newMessages.put(i,z);
		}
		return newMessages;
	}

}
