package schiller.inventory.productSpecialization.productExtension.category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, String> {

	@Override
	@NonNull
	Iterable<Category> findAll();

	@Override
	@NonNull
	Optional<Category> findById(@NonNull String Id);

}
