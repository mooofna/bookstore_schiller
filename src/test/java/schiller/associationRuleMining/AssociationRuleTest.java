package schiller.associationRuleMining;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.catalog.Product.ProductIdentifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AssociationRuleTest {

	@Test
	void testConstructorWithTwoArguments() {
		ProductIdentifier mockAntecedent = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier mockConsequent = Mockito.mock(ProductIdentifier.class);

		Set<ProductIdentifier> antecedentSet = new HashSet<>();
		antecedentSet.add(mockAntecedent);

		Set<ProductIdentifier> consequentSet = new HashSet<>();
		consequentSet.add(mockConsequent);

		AssociationRule rule = new AssociationRule(antecedentSet, consequentSet);

		assertEquals(antecedentSet, rule.getAntecedent());
		assertEquals(consequentSet, rule.getConsequent());
		assertTrue(Double.isNaN(rule.getConfidence()), "Confidence should be NaN for two-argument constructor");
	}

	@Test
	void testToString() {
		ProductIdentifier mockAntecedent = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier mockConsequent = Mockito.mock(ProductIdentifier.class);

		Set<ProductIdentifier> antecedentSet = new HashSet<>();
		antecedentSet.add(mockAntecedent);

		Set<ProductIdentifier> consequentSet = new HashSet<>();
		consequentSet.add(mockConsequent);

		AssociationRule rule = new AssociationRule(antecedentSet, consequentSet, 0.8);

		String expectedString = "[" + mockAntecedent.toString() + "] -> [" + mockConsequent.toString() + "]: 0.8";
		assertEquals(expectedString, rule.toString());
	}

	@Test
	void testEquals() {
		ProductIdentifier mockAntecedent1 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier mockConsequent1 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier mockAntecedent2 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier mockConsequent2 = Mockito.mock(ProductIdentifier.class);

		Set<ProductIdentifier> antecedentSet1 = new HashSet<>();
		antecedentSet1.add(mockAntecedent1);
		Set<ProductIdentifier> consequentSet1 = new HashSet<>();
		consequentSet1.add(mockConsequent1);

		Set<ProductIdentifier> antecedentSet2 = new HashSet<>();
		antecedentSet2.add(mockAntecedent2);
		Set<ProductIdentifier> consequentSet2 = new HashSet<>();
		consequentSet2.add(mockConsequent2);

		AssociationRule rule1 = new AssociationRule(antecedentSet1, consequentSet1, 0.8);
		AssociationRule rule2 = new AssociationRule(antecedentSet1, consequentSet1, 0.8);
		AssociationRule rule3 = new AssociationRule(antecedentSet2, consequentSet2, 0.8);

		assertEquals(rule1, rule2, "Rules with same antecedent and consequent should be equal");
		assertNotEquals(rule1, rule3, "Rules with different antecedent or consequent should not be equal");
	}

	@Test
	void testEqualsWithNull() {
		ProductIdentifier antecedentId = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet = Collections.singleton(antecedentId);
		AssociationRule rule = new AssociationRule(antecedentSet, antecedentSet, 0.8);

		assertNotEquals(null, rule, "Rule should not be equal to null.");
	}

	@Test
	void testEqualsWithDifferentType() {
		ProductIdentifier antecedentId = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet = Collections.singleton(antecedentId);
		AssociationRule rule = new AssociationRule(antecedentSet, antecedentSet, 0.8);

		assertNotEquals("some string", rule, "Rule should not be equal to an object of a different type.");
	}

	@Test
	void testEqualsWithDifferentConfidence() {
		ProductIdentifier antecedentId = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet = Collections.singleton(antecedentId);
		AssociationRule rule1 = new AssociationRule(antecedentSet, antecedentSet, 0.8);
		AssociationRule rule2 = new AssociationRule(antecedentSet, antecedentSet, 0.9);

		assertEquals(rule1, rule2, "Rules with same antecedent and consequent but different confidence should be equal");
	}

	@Test
	void testEqualsWithDifferentObject() {
		ProductIdentifier antecedentId = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet = Collections.singleton(antecedentId);
		AssociationRule rule = new AssociationRule(antecedentSet, antecedentSet);

		assertNotEquals(new Object(), rule, "Rule should not be equal to an object of a completely different type.");
	}

	@Test
	void testEqualsWithItself() {
		ProductIdentifier antecedentId = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet = Collections.singleton(antecedentId);
		AssociationRule rule = new AssociationRule(antecedentSet, antecedentSet);

		assertEquals(rule, rule, "Rule should be equal to itself.");
	}

	@Test
	void testEqualsWithDifferentAntecedent() {
		ProductIdentifier antecedentId1 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier antecedentId2 = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet1 = Collections.singleton(antecedentId1);
		Set<ProductIdentifier> antecedentSet2 = Collections.singleton(antecedentId2);
		AssociationRule rule1 = new AssociationRule(antecedentSet1, antecedentSet1);
		AssociationRule rule2 = new AssociationRule(antecedentSet2, antecedentSet1);

		assertNotEquals(rule1, rule2, "Rules with different antecedents should not be equal.");
	}

	@Test
	void testEqualsWithDifferentConsequent() {
		ProductIdentifier antecedentId = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier consequentId1 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier consequentId2 = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet = Collections.singleton(antecedentId);
		Set<ProductIdentifier> consequentSet1 = Collections.singleton(consequentId1);
		Set<ProductIdentifier> consequentSet2 = Collections.singleton(consequentId2);
		AssociationRule rule1 = new AssociationRule(antecedentSet, consequentSet1);
		AssociationRule rule2 = new AssociationRule(antecedentSet, consequentSet2);

		assertNotEquals(rule1, rule2, "Rules with different consequents should not be equal.");
	}

	@Test
	void testEqualsWithDifferentAntecedentAndConsequent() {
		ProductIdentifier antecedentId1 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier antecedentId2 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier consequentId1 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier consequentId2 = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet1 = Collections.singleton(antecedentId1);
		Set<ProductIdentifier> antecedentSet2 = Collections.singleton(antecedentId2);
		Set<ProductIdentifier> consequentSet1 = Collections.singleton(consequentId1);
		Set<ProductIdentifier> consequentSet2 = Collections.singleton(consequentId2);
		AssociationRule rule1 = new AssociationRule(antecedentSet1, consequentSet1);
		AssociationRule rule2 = new AssociationRule(antecedentSet2, consequentSet2);

		assertNotEquals(rule1, rule2, "Rules with different antecedents and consequents should not be equal.");
	}

	@Test
	void testEqualsWithSameElementsDifferentOrder() {
		ProductIdentifier antecedentId1 = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier antecedentId2 = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet1 = new HashSet<>(Arrays.asList(antecedentId1, antecedentId2));
		Set<ProductIdentifier> antecedentSet2 = new HashSet<>(Arrays.asList(antecedentId2, antecedentId1));
		AssociationRule rule1 = new AssociationRule(antecedentSet1, antecedentSet1);
		AssociationRule rule2 = new AssociationRule(antecedentSet2, antecedentSet2);

		assertEquals(rule1, rule2, "Rules with same elements in different order should be equal.");
	}
}
