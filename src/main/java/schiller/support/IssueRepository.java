package schiller.support;

import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface IssueRepository extends CrudRepository<Issue, Long> {

	@Override
	Streamable<Issue> findAll();

	@Query("SELECT i FROM Issue i JOIN FETCH i.customer")
	Streamable<Issue> findAllEagerly();

}
