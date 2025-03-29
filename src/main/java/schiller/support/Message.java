package schiller.support;

import jakarta.persistence.*;
import lombok.Getter;
import org.salespointframework.useraccount.Role;
import schiller.user.User;

import java.time.LocalDateTime;

@Getter
@Entity
public class Message {

	public static final Role CUSTOMER_ROLE = Role.of("CUSTOMER");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Lob
	private String content;

	private LocalDateTime messageTime;

	@ManyToOne
	private User author;

	public Message() {}


	public Message(String content, User author, LocalDateTime localDateTime) {
		this.content = content;
		this.author = author;
		this.messageTime = localDateTime;
	}

	/**
	 * Checks whether author is customer -> Used to render chat correctly
	 */
	public boolean isCustomer() {
		return author.getUserAccount().hasRole(CUSTOMER_ROLE);
	}

}
