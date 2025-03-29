package schiller.inventory.storage;

import org.junit.jupiter.api.*;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class FileSystemStorageServiceTest {

	private FileSystemStorageService service;
	private Path rootLocation;

	@BeforeEach
	void setUp() {
		StorageProperties properties = new StorageProperties();
		properties.setRootLocation("test-upload-dir");
		properties.setMockLocation("test-mock-dir");

		rootLocation = Paths.get(properties.getRootLocation());
		service = new FileSystemStorageService(properties);
		service.deleteAll();
		service.init();
	}

	@AfterEach
	void tearDown() {
		service.deleteAll();
	}

	@Test
	void storeAndLoadFile() {
		MultipartFile mockFile = new MockMultipartFile("testfile.txt", "testfile.txt", "text/plain", "Test content".getBytes());

		service.store(mockFile);
		Path expectedPath = rootLocation.resolve("testfile.txt");

		assertThat(Files.exists(expectedPath)).isTrue();

		Path loadedPath = service.load("testfile.txt");
		assertThat(loadedPath).isEqualTo(expectedPath);
	}

	@Test
	void loadAllFiles() {
		service.store(new MockMultipartFile("file1.txt", "file1.txt", "text/plain", "content1".getBytes()));
		service.store(new MockMultipartFile("file2.txt", "file2.txt", "text/plain", "content2".getBytes()));

		Stream<Path> files = service.loadAll();
		assertThat(files.collect(Collectors.toList())).hasSize(2);
	}

	@Test
	void loadAsResource() {
		service.store(new MockMultipartFile("testfile.txt", "testfile.txt", "text/plain", "Test content".getBytes()));

		Resource resource = service.loadAsResource("testfile.txt");
		assertThat(resource.exists()).isTrue();
		assertThat(resource.isReadable()).isTrue();
	}

	@Test
	void deleteAllFiles() {
		service.store(new MockMultipartFile("file1.txt", "file1.txt", "text/plain", "content1".getBytes()));
		service.deleteAll();

		assertThat(Files.exists(rootLocation)).isFalse();
	}

	@Test
	void initializeStorage() {
		service.init();
		assertThat(Files.exists(rootLocation)).isTrue();
	}

	@Test
	void convertResourceToMultipartFile() {
		service.store(new MockMultipartFile("testfile.txt", "testfile.txt", "text/plain", "Test content".getBytes()));
		Resource resource = service.loadAsResource("testfile.txt");

		MultipartFile multipartFile = service.convertResourceToMultipartFile(resource);
		assertThat(multipartFile.getOriginalFilename()).isEqualTo("testfile.txt");
	}
}
