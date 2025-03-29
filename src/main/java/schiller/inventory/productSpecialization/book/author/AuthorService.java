package schiller.inventory.productSpecialization.book.author;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

	private final AuthorRepository authorRepository;

	@Autowired
	public AuthorService(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}

	public Author getOrCreateAuthor(String name) {
		Iterable<Author> authors = authorRepository.findAll();
		for (Author author : authors) {
			if (author.getName().trim().equals(name.trim())) {
				return author;
			}
		}
		Author author = new Author(name);
		authorRepository.save(author);
		return author;
	}
}
