package schiller.inventory.storage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StoragePropertiesTest {

	@Test
	void testRootLocationProperty() {
		StorageProperties properties = new StorageProperties();
		String testRootLocation = "test/root/location";

		properties.setRootLocation(testRootLocation);

		assertThat(properties.getRootLocation()).isEqualTo(testRootLocation);
	}

	@Test
	void testMockLocationProperty() {
		StorageProperties properties = new StorageProperties();
		String testMockLocation = "test/mock/location";

		properties.setMockLocation(testMockLocation);

		assertThat(properties.getMockLocation()).isEqualTo(testMockLocation);
	}

	@Test
	void defaultValues() {
		StorageProperties properties = new StorageProperties();

		assertThat(properties.getRootLocation()).isEqualTo("src/main/resources/static/resources/img/productimg");
		assertThat(properties.getMockLocation()).isEqualTo("src/main/resources/static/resources/img/mock");
	}
}
