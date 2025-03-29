package schiller.support;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import schiller.user.User;

public class MessageForm {
	private @NotEmpty String title;
	private @NotEmpty String content;
	private User author;

	public MessageForm() {
		this.title = "";
		this.content = "";
		this.author = null;
	}
	public MessageForm(String title, String content, User author) {
		this.title = title;
		this.content = content;
		this.author = author;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
}

