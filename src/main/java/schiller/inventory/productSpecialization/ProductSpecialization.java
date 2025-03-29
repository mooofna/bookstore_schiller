package schiller.inventory.productSpecialization;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import schiller.inventory.productSpecialization.comment.Comment;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class ProductSpecialization extends Product {

	@OneToMany(cascade = CascadeType.ALL)
	private final List<Comment> comments = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public ProductSpecialization() {}

	public ProductSpecialization(String name, Money price) {
		super(name, price);
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public String getAverageRating() {
		int i = 0;
		for (Comment comment : comments) {
			i += comment.getRating();
		}
			int average = i / comments.size();

		return "⭐".repeat(Math.max(0, average));
	}
}
