package schiller.inventory.storage;

import org.junit.jupiter.api.Test;
import schiller.AbstractIntegrationTests;

import static org.junit.jupiter.api.Assertions.*;

class StorageFileNotFoundExceptionTest extends AbstractIntegrationTests {

	@Test
	void testExceptionWithMessage() {
		String expectedMessage = "Test file not found exception message";


		Exception exception = assertThrows(StorageFileNotFoundException.class, () -> {
			throw new StorageFileNotFoundException(expectedMessage);
		});


		assertEquals(expectedMessage, exception.getMessage(), "The exception message does not match the expected value.");
	}

	@Test
	void testExceptionWithMessageAndCause() {

		String expectedMessage = "Test file not found exception message";
		Throwable expectedCause = new Throwable("Cause of the exception");


		Exception exception = assertThrows(StorageFileNotFoundException.class, () -> {
			throw new StorageFileNotFoundException(expectedMessage, expectedCause);
		});


		assertEquals(expectedMessage, exception.getMessage(), "The exception message does not match the expected value.");
		assertEquals(expectedCause, exception.getCause(), "The cause of the exception does not match the expected value.");
	}
}
