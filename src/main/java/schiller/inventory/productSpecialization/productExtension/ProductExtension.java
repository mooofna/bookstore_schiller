package schiller.inventory.productSpecialization.productExtension;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import org.javamoney.moneta.Money;

import lombok.Getter;
import lombok.Setter;
import schiller.inventory.productSpecialization.productExtension.category.Category;
import schiller.inventory.productSpecialization.ProductSpecialization;

@Getter
@Setter
@Entity
public class ProductExtension extends ProductSpecialization {

	private String image, info;

	@ManyToOne(cascade = CascadeType.ALL)
	private Category category;

	public ProductExtension() {}

	public ProductExtension(String name, String image, Money price, String info, Category category) {

		super(name, price);

		this.image = image;
		this.info = info;
		this.category = category;
	}
}
