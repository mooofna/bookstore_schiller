package schiller.event;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface EventRepository extends CrudRepository<Event, Long>{


	@Override
	@NonNull
	Streamable<Event> findAll();

	@Override
	@NonNull
	Optional<Event> findById(@NonNull Long id);

}
