package schiller.inventory.productSpecialization.productExtension.category;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Category {

	@Id
	String name;

	public Category() {}

	public Category(String name) {
		this.name = name.toUpperCase();
	}
}
