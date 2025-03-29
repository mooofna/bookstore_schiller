package schiller.inventory.productSpecialization.book;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import schiller.inventory.productSpecialization.book.genre.Genre;

import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Genre, Long> {
	@Override
	Iterable<Genre> findAll();

	@Override
	Optional<Genre> findById(@NonNull Long id);
}
