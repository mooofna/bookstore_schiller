package schiller.associationRuleMining;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.catalog.Product;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AssociationRuleGeneratorTest {


	@Test
	void testGenerateAssociationRulesWithEmptyRuleSet() {
		AssociationRuleGenerator generator = new AssociationRuleGenerator();
		FrequentItemsetData mockData = Mockito.mock(FrequentItemsetData.class);
		Set<AssociationRule> collector = new HashSet<>();

		generator.generateAssociationRules(Collections.emptySet(), Collections.emptySet(), mockData, 0.5, collector);
		assertTrue(collector.isEmpty());
	}

	@Test
	void testCheckMinimumConfidenceWithNaN() {
		AssociationRuleGenerator generator = new AssociationRuleGenerator();
		assertThrows(IllegalArgumentException.class, () -> generator.checkMinimumConfidence(Double.NaN));
	}

	@Test
	void testCheckMinimumConfidenceWithNegativeValue() {
		AssociationRuleGenerator generator = new AssociationRuleGenerator();
		assertThrows(IllegalArgumentException.class, () -> generator.checkMinimumConfidence(-0.1));
	}

	@Test
	void testCheckMinimumConfidenceWithTooLargeValue() {
		AssociationRuleGenerator generator = new AssociationRuleGenerator();
		assertThrows(IllegalArgumentException.class, () -> generator.checkMinimumConfidence(1.1));
	}

	@SuppressWarnings("unchecked")
	private Comparator<Object> getItemComparator() throws Exception {
		Field comparatorField = AprioriFrequentItemsetGenerator.class.getDeclaredField("ITEM_COMPARATOR");
		comparatorField.setAccessible(true);
		return (Comparator<Object>) comparatorField.get(null);
	}

	@Test
	void testItemComparatorWithProductIdentifiers() throws Exception {
		Comparator<Object> comparator = getItemComparator();

		Product.ProductIdentifier id1 = Mockito.mock(Product.ProductIdentifier.class);
		Product.ProductIdentifier id2 = Mockito.mock(Product.ProductIdentifier.class);
		when(id1.toString()).thenReturn("id1");
		when(id2.toString()).thenReturn("id2");

		int result = comparator.compare(id1, id2);
		assertTrue(result < 0, "Comparator should return a negative number when the first ID is lexicographically smaller");
	}

	@Test
	void testItemComparatorWithNonProductIdentifiers() throws Exception {
		Comparator<Object> comparator = getItemComparator();

		Object obj1 = new Object();
		Object obj2 = new Object();

		assertThrows(IllegalArgumentException.class,
			() -> comparator.compare(obj1, obj2),
			"Comparator should throw IllegalArgumentException for non ProductIdentifier objects");
	}
}
