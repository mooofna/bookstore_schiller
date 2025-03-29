package schiller.associationRuleMining;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class AssociationRuleMiningConfigurationTest {

	@Test
	void testGetterAndSetter() {
		AssociationRuleMiningConfiguration config = new AssociationRuleMiningConfiguration();

		ReflectionTestUtils.setField(config, "minimumSupport", 0.5);
		ReflectionTestUtils.setField(config, "minimumConfidence", 0.8);

		assertEquals(0.5, config.getMinimumSupport(), "Getter not working correctly for minimumSupport.");
		assertEquals(0.8, config.getMinimumConfidence(), "Getter not working correctly for minimumConfidence.");

		config.setMinimumSupport(0.6);
		config.setMinimumConfidence(0.9);

		assertEquals(0.6, config.getMinimumSupport(), "Setter for minimumSupport not working correctly.");
		assertEquals(0.9, config.getMinimumConfidence(), "Setter for minimumConfidence not working correctly.");
	}
}
