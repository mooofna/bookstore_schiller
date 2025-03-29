package schiller.inventory.addItem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import schiller.AbstractIntegrationTests;
import schiller.inventory.productSpecialization.productExtension.category.Category;
import schiller.inventory.storage.FileSystemStorageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class ProductExtensionFormTest extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FileSystemStorageService fileSystemStorageService;

	@Test
	void testGetterAndSetter() {

		ProductExtensionForm productExtensionForm = new ProductExtensionForm();
		String testName = "test";
		MultipartFile testImage = fileSystemStorageService.convertResourceToMultipartFile(fileSystemStorageService.loadAsResource("dun.png"));
		Double testPrice = 10D;
		String testInfo = "test";
		Category testCategory = new Category("test");
		int testQuantity = 3;


		productExtensionForm.setName(testName);
		productExtensionForm.setImage(testImage);
		productExtensionForm.setPrice(testPrice);
		productExtensionForm.setInfo(testInfo);
		productExtensionForm.setCategory(testCategory);
		productExtensionForm.setQuantity(testQuantity);

		String retrievedName = productExtensionForm.getName();
		MultipartFile retrievedImage = productExtensionForm.getImage();
		Double retrievedPrice = productExtensionForm.getPrice();
		String retrievedInfo = productExtensionForm.getInfo();
		Category retrievedCategory = productExtensionForm.getCategory();
		int retrievedQuantity = productExtensionForm.getQuantity();

		assertEquals(testName, retrievedName, "The getter or setter for name is not working as expected.");
		assertEquals(testImage, retrievedImage, "The getter or setter for image is not working as expected.");
		assertEquals(testPrice, retrievedPrice, "The getter or setter for price is not working as expected.");
		assertEquals(testInfo, retrievedInfo, "The getter or setter for info is not working as expected.");
		assertEquals(testCategory, retrievedCategory, "The getter or setter for category is not working as expected.");
		assertEquals(testQuantity, retrievedQuantity, "The getter or setter for quantity is not working as expected.");
	}

}