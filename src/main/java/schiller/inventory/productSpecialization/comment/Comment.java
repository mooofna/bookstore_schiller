package schiller.inventory.productSpecialization.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "COMMENTS")
public class Comment implements Serializable {

	private static final long serialVersionUID = -7114101035786254953L;

	@Id
	@GeneratedValue
	private long id;

	private String text;

	private int rating;

	private LocalDateTime date;

	@Getter
	@ManyToOne
	private UserAccount author;


	@SuppressWarnings("unused")
	public Comment() {}

	public Comment(String text, int rating, LocalDateTime dateTime){
		Assert.hasLength(text, "text must not be Null and must not be the empty String");
		Assert.isTrue(1 <= rating && 5 >= rating, "rating must be between 1 and 5");
		Assert.notNull(dateTime, "dateTime must not be Null");

		this.text = text;
		this.rating = rating;
		this.date = dateTime;
	}

	public Comment(String text, int rating, LocalDateTime dateTime, UserAccount author) {

        Assert.hasLength(text, "text must not be Null and must not be the empty String");
		Assert.isTrue(1 <= rating && 5 >= rating, "rating must be between 1 and 5");
		Assert.notNull(dateTime, "dateTime must not be Null");
		Assert.notNull(author, "author must not be Null");

		this.text = text;
		this.rating = rating;
		this.date = dateTime;
		this.author = author;
	}

	public String getStringRating() {
		return "⭐".repeat(Math.max(0, rating));
	}

	@Override
	public String toString() {
		return text;
	}

}
