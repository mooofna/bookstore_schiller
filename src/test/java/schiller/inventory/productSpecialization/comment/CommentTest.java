package schiller.inventory.productSpecialization.comment;

import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import schiller.AbstractIntegrationTests;
import schiller.user.User;
import schiller.user.UserRepository;

import java.time.LocalDateTime;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
class CommentTest extends AbstractIntegrationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	void testGetterAndToString() throws Exception {

		LocalDateTime time = LocalDateTime.now();

		Streamable<User> allUsers = userRepository.findAll();
		User testUser = allUsers.stream().toList().get(0);
		UserAccount testUserAccount = testUser.getUserAccount();

		Comment comment = new Comment("test", 5,time, testUserAccount);

		Long retrievedId = comment.getId();
		String retrievedText = comment.getText();
		int retrievedRating = comment.getRating();
		LocalDateTime retrievedTime = comment.getDate();
		UserAccount retrievedUserAccount = comment.getAuthor();
		String retrievedStringRating = comment.getStringRating();
		String retrievedToString = comment.toString();

		assertEquals("unlucky", retrievedId, retrievedId);
		assertEquals("unlucky", "test", retrievedText);
		assertEquals("unlucky", 5, retrievedRating);
		assertEquals("unlucky", time, retrievedTime);
		assertEquals("unlucky", testUserAccount, retrievedUserAccount);
		assertEquals("unlucky", "⭐⭐⭐⭐⭐", retrievedStringRating);
		assertEquals("unlucky", "test", retrievedToString);
	}

	@Test
	void testConstructorWithoutUserAccountTooLowRating() {

		LocalDateTime time = LocalDateTime.now();

		try {
			new Comment("test", 0, time);
		} catch(IllegalArgumentException e) {
			assertTrue("unlucky", e.getMessage().equals("rating must be between 1 and 5"));
		}
	}

	@Test
	void testConstructorWithoutUserAccountTooHighRating() {

		LocalDateTime time = LocalDateTime.now();

		try {
			new Comment("test", 6, time);
		} catch(IllegalArgumentException e) {
			assertTrue("unlucky", e.getMessage().equals("rating must be between 1 and 5"));
		}
	}

	@Test
	void testConstructorWithUserAccountTooLowRating() {

		LocalDateTime time = LocalDateTime.now();

		Streamable<User> allUsers = userRepository.findAll();
		User testUser = allUsers.stream().toList().get(0);
		UserAccount testUserAccount = testUser.getUserAccount();

		try {
			new Comment("test", 0, time, testUserAccount);
		} catch(IllegalArgumentException e) {
			assertTrue("unlucky", e.getMessage().equals("rating must be between 1 and 5"));
		}
	}

	@Test
	void testConstructorWithUserAccountTooHighRating() {

		LocalDateTime time = LocalDateTime.now();

		Streamable<User> allUsers = userRepository.findAll();
		User testUser = allUsers.stream().toList().get(0);
		UserAccount testUserAccount = testUser.getUserAccount();

		try {
			new Comment("test", 6, time, testUserAccount);
		} catch(IllegalArgumentException e) {
			assertTrue("unlucky", e.getMessage().equals("rating must be between 1 and 5"));
		}
	}
}
