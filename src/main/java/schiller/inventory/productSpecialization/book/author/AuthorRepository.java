package schiller.inventory.productSpecialization.book.author;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
	@Override
	Iterable<Author> findAll();

	@Override
	Optional<Author> findById(@NonNull Long id);
}
