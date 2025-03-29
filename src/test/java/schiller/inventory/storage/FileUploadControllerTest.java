package schiller.inventory.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FileUploadControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FileSystemStorageService fileSystemStorageService;

	@BeforeEach
	void setUp() {
		fileSystemStorageService.deleteAll();
		fileSystemStorageService.init();
		fileSystemStorageService.copyMockImg();
	}

	@Test
	void testFileServing() throws Exception {
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());
		fileSystemStorageService.store(mockFile);

		mockMvc.perform(get("/files/test.txt"))
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.txt\""))
			.andExpect(content().string("Test content"));
	}

	@Test
	void testFileUpload() throws Exception {
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());

		mockMvc.perform(multipart("/FileUpload").file(mockFile))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	void testHandleStorageFileNotFound() throws Exception {
		mockMvc.perform(get("/files/nonexistent.txt"))
			.andExpect(status().isNotFound());
	}
}