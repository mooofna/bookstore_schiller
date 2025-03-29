package schiller.inventory.productSpecialization.book.genre;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Genre {

	private @Id @GeneratedValue long id;

	private String name;

	@SuppressWarnings({ "unused", "deprecation" })
	private Genre() {}

	public Genre(String name) {
		this.name = name.toUpperCase();
	}

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}
}
