package schiller.inventory.productSpecialization.productExtension;

import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Component;
import schiller.inventory.productSpecialization.productExtension.category.Category;

@Component("ProductExtensionCatalog")
public interface ProductExtensionCatalog extends Catalog<ProductExtension> {

	Sort DEFAULT_SORT = Sort.sort(ProductExtension.class).by(ProductExtension::getId).descending();

	Streamable<ProductExtension> findByCategory(Category category, Sort sort);

	default Streamable<ProductExtension> findByCategory(Category category) {
		return findByCategory(category, DEFAULT_SORT);
	}
}
