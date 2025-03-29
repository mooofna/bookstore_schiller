package schiller.inventory.addItem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import schiller.AbstractIntegrationTests;
import schiller.inventory.storage.FileSystemStorageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookFormTest extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FileSystemStorageService fileSystemStorageService;

	@Test
	void testGetterAndSetter() {

		BookForm bookForm = new BookForm();
		String testName = "test";
		MultipartFile testImage = fileSystemStorageService.convertResourceToMultipartFile(fileSystemStorageService.loadAsResource("dun.png"));
		Double testPrice = 10D;
		String testIsbn = "test";
		String testPublisher = "test";
		String testCovertext = "test";
		String testAuthor = "test";
		ArrayList<Long> testGenres = new ArrayList<>(Collections.singletonList(1L));
		int testQuantity = 3;


		bookForm.setName(testName);
		bookForm.setImage(testImage);
		bookForm.setPrice(testPrice);
		bookForm.setIsbn(testIsbn);
		bookForm.setPublisher(testPublisher);
		bookForm.setCoverText(testCovertext);
		bookForm.setAuthor(testAuthor);
		bookForm.setGenres(testGenres);
		bookForm.setQuantity(testQuantity);

		String retrievedName = bookForm.getName();
		MultipartFile retrievedImage = bookForm.getImage();
		Double retrievedPrice = bookForm.getPrice();
		String retrievedIsbn = bookForm.getIsbn();
		String retrievedPulisher = bookForm.getPublisher();
		String retrievedCovertext = bookForm.getCoverText();
		String retrievedAuthor = bookForm.getAuthor();
		List<Long> retrievedGenres = bookForm.getGenres();
		int retrievedQuantity = bookForm.getQuantity();

		assertEquals(testName, retrievedName, "The getter or setter for name is not working as expected.");
		assertEquals(testImage, retrievedImage, "The getter or setter for image is not working as expected.");
		assertEquals(testPrice, retrievedPrice, "The getter or setter for price is not working as expected.");
		assertEquals(testIsbn, retrievedIsbn, "The getter or setter for isbn is not working as expected.");
		assertEquals(testPublisher, retrievedPulisher, "The getter or setter for publisher is not working as expected.");
		assertEquals(testCovertext, retrievedCovertext, "The getter or setter for covertext is not working as expected.");
		assertEquals(testAuthor, retrievedAuthor, "The getter or setter for author is not working as expected.");
		assertEquals(testGenres, retrievedGenres, "The getter or setter for genres is not working as expected.");
		assertEquals(testQuantity, retrievedQuantity, "The getter or setter for quantity is not working as expected.");
	}

}