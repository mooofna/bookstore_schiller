package schiller.inventory.productSpecialization.book.genre;

import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("GenreDataInitializer")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GenreDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(GenreDataInitializer.class);

	private final GenreRepository genreRepository;

	GenreDataInitializer(GenreRepository genreRepository) {

		Assert.notNull(genreRepository, "GenreRepository must not be null!");

		this.genreRepository = genreRepository;
	}

	@Override
	public void initialize() {

		if (genreRepository.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default Genre entries.");

		genreRepository.save(new Genre("FIKTION"));
		genreRepository.save(new Genre("SACHBUCH"));
		genreRepository.save(new Genre("UNTERHALTUNG"));
		genreRepository.save(new Genre("RATGEBER"));
	}
}
