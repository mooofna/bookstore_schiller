package schiller.inventory.productSpecialization.book.genre;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Long> {

	@Override
	@NonNull
	Iterable<Genre> findAll();

	@Override
	@NonNull
	Optional<Genre> findById(@NonNull Long id);

	Optional<Genre> findByName(String name);
}
