package schiller.associationRuleMining;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.catalog.Product.ProductIdentifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FrequentItemsetDataTest {

	@Test
	void testGetters() {
		ProductIdentifier mockIdentifier = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> itemset = new HashSet<>();
		itemset.add(mockIdentifier);

		Map<Set<ProductIdentifier>, Integer> supportCountMap = new HashMap<>();
		supportCountMap.put(itemset, 3);

		List<Set<ProductIdentifier>> frequentItemsetList = Collections.singletonList(itemset);
		double minimumSupport = 0.5;
		int numberOfTransactions = 5;

		FrequentItemsetData data = new FrequentItemsetData(frequentItemsetList, supportCountMap, minimumSupport, numberOfTransactions);

		assertEquals(frequentItemsetList, data.getFrequentItemsetList(), "Getters should return correct values");
		assertEquals(supportCountMap, data.getSupportCountMap(), "Getters should return correct values");
		assertEquals(minimumSupport, data.getMinimumSupport(), "Getters should return correct values");
		assertEquals(numberOfTransactions, data.getTransactionNumber(), "Getters should return correct values");
	}

	@Test
	void testGetSupport() {
		ProductIdentifier mockIdentifier = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> itemset = new HashSet<>();
		itemset.add(mockIdentifier);

		Map<Set<ProductIdentifier>, Integer> supportCountMap = new HashMap<>();
		int supportCount = 3;
		supportCountMap.put(itemset, supportCount);

		int numberOfTransactions = 6;
		FrequentItemsetData data = new FrequentItemsetData(null, supportCountMap, 0.0, numberOfTransactions);

		double expectedSupport = (double) supportCount / numberOfTransactions;
		assertEquals(expectedSupport, data.getSupport(itemset), "GetSupport should return correct value");
	}
}
