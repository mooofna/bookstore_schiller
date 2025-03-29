package schiller.inventory.storage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StorageExceptionTest {

	@Test
	void testExceptionWithMessage() {

		String expectedMessage = "Test storage exception message";


		Exception exception = assertThrows(StorageException.class, () -> {
			throw new StorageException(expectedMessage);
		});

		assertEquals(expectedMessage, exception.getMessage(), "The exception message does not match the expected value.");
	}

	@Test
	void testExceptionWithMessageAndCause() {

		String expectedMessage = "Test storage exception message";
		Throwable expectedCause = new Throwable("Cause of the exception");


		Exception exception = assertThrows(StorageException.class, () -> {
			throw new StorageException(expectedMessage, expectedCause);
		});


		assertEquals(expectedMessage, exception.getMessage(), "The exception message does not match the expected value.");
		assertEquals(expectedCause, exception.getCause(), "The cause of the exception does not match the expected value.");
	}
}
