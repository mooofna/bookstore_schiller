package schiller.support;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import schiller.user.Customer;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Issue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;

	@Setter
	@Lob
	private String content;

	private boolean closed = false;

	private Integer rating = null;

	public Issue() {}
	@Setter
	@ManyToOne(cascade=CascadeType.ALL)
	private Customer customer;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Message> messages = new ArrayList<>();

	public Issue(String title, Customer customer) {
		this.title = title;
		this.customer = customer;
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	public void closeIssue(){
		closed = true;
	}

	public void setRating(int rating) {
		if (getRating()==null && (rating < 6 && rating > -1)){
			this.rating = rating;
		}
	}

}
