package schiller.inventory.productSpecialization.book.author;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
public class Author {

	@Getter
	@Id
	@GeneratedValue
	private long id;

	@Getter
	private String name;

	public Author(String name) {
		this.name = name;
	}

	public Author() {

	}
}
