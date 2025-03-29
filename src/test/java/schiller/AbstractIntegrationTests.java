package schiller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
public abstract class AbstractIntegrationTests {
}