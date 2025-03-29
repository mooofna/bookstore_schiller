package schiller.associationRuleMining;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.catalog.Product;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AprioriFrequentItemsetGeneratorTest {

	private AprioriFrequentItemsetGenerator generator;
	private List<Set<Product.ProductIdentifier>> transactionList;

	@BeforeEach
	void setUp() {
		generator = new AprioriFrequentItemsetGenerator();
		transactionList = new ArrayList<>();
	}

	@Test
	void testEmptyTransactionList() {
		AprioriFrequentItemsetGenerator generator = new AprioriFrequentItemsetGenerator();
		List<Set<Product.ProductIdentifier>> transactionList = new ArrayList<>();

		FrequentItemsetData result = generator.generate(transactionList, 0.5);
		assertNull(result, "Expected result to be null for an empty transaction list.");
	}

	@Test
	void testGetNextItemsetsWithValidSupport() {
		double minimumSupport = 0.5;

		FrequentItemsetData result = generator.generate(new ArrayList<>(), minimumSupport);

		assertNull(result, "Expected non-null result from generate method");
	}

	@Test
	void testGetNextItemsetsWithValidAndUnvalidItemset() {
		AprioriFrequentItemsetGenerator generator = new AprioriFrequentItemsetGenerator();

		Product.ProductIdentifier mockIdentifier = Mockito.mock(Product.ProductIdentifier.class);
		Set<Product.ProductIdentifier> itemset = new HashSet<>();
		itemset.add(mockIdentifier);

		Map<Set<Product.ProductIdentifier>, Integer> supportCountMap = new HashMap<>();
		supportCountMap.put(itemset, 3);

		List<Set<Product.ProductIdentifier>> candidateList = Collections.singletonList(itemset);

		List<Set<Product.ProductIdentifier>> result = generator.getNextItemsets(candidateList, supportCountMap,
			0.2,
			5);
		assertTrue(result.contains(itemset),
			"Itemset with sufficient support should be included in the result");

		result = generator.getNextItemsets(candidateList, supportCountMap, 0.8, 5);
		assertFalse(result.contains(itemset),
			"Itemset with insufficient support should not be included in the result");

		Set<Product.ProductIdentifier> nonExistingItemset = new HashSet<>();
		nonExistingItemset.add(Mockito.mock(Product.ProductIdentifier.class));
		candidateList = Collections.singletonList(nonExistingItemset);
		result = generator.getNextItemsets(candidateList, supportCountMap, 0.2, 5);
		assertFalse(result.contains(nonExistingItemset),
			"Non-existing itemset should not be included in the result");
	}

	@SuppressWarnings("unchecked")
	@Test
	void testTryMergeItemsetsForEquality() throws Exception {
		AprioriFrequentItemsetGenerator generator = new AprioriFrequentItemsetGenerator();
		Method mergeMethod = AprioriFrequentItemsetGenerator.class.getDeclaredMethod("tryMergeItemsets",
			List.class,
			List.class);
		mergeMethod.setAccessible(true);

		Product.ProductIdentifier item1 = Mockito.mock(Product.ProductIdentifier.class);
		Product.ProductIdentifier item2 = Mockito.mock(Product.ProductIdentifier.class);
		Product.ProductIdentifier item3 = Mockito.mock(Product.ProductIdentifier.class);

		List<Product.ProductIdentifier> itemset1 = Arrays.asList(item1, item2);
		List<Product.ProductIdentifier> itemset2 = Arrays.asList(item1, item3);

		Set<Product.ProductIdentifier> result = (Set<Product.ProductIdentifier>) mergeMethod.invoke(generator,
			itemset1,
			itemset2);
		assertNotNull(result, "Result should not be null for different last items");
		assertEquals(3, result.size(), "Merged set should have correct size");

		List<Product.ProductIdentifier> itemset3 = Arrays.asList(item1, item2);
		List<Product.ProductIdentifier> itemset4 = Arrays.asList(item1, item2);

		result = (Set<Product.ProductIdentifier>) mergeMethod.invoke(generator, itemset3, itemset4);
		assertNull(result, "Result should be null for identical last items");
	}

	@Test
	void testItemComparatorWithNonProductIdentifiers() {
		assertThrows(IllegalArgumentException.class,
			() -> AprioriFrequentItemsetGenerator.ITEM_COMPARATOR.compare(new Object(), new Object()),
			"Should throw IllegalArgumentException for non ProductIdentifier objects.");
	}

	@Test
	void testItemComparatorWithOneNonProductIdentifier() {
		Comparator<Object> comparator = AprioriFrequentItemsetGenerator.ITEM_COMPARATOR;

		Product.ProductIdentifier id1 = Mockito.mock(Product.ProductIdentifier.class);
		Object nonProductId = new Object();

		assertThrows(IllegalArgumentException.class,
			() -> comparator.compare(id1, nonProductId),
			"Comparator should throw IllegalArgumentException when one object is not a ProductIdentifier");
	}

	@Test
	void testItemComparatorWithBothNonProductIdentifiers() {
		Comparator<Object> comparator = AprioriFrequentItemsetGenerator.ITEM_COMPARATOR;

		Object nonProductId1 = new Object();
		Object nonProductId2 = new Object();

		assertThrows(IllegalArgumentException.class,
			() -> comparator.compare(nonProductId1, nonProductId2),
			"Comparator should throw IllegalArgumentException when both objects are not ProductIdentifiers");
	}


	@Test
	void testFindFrequentItemsWithValidSupport() {
		Product.ProductIdentifier item1 = Mockito.mock(Product.ProductIdentifier.class);
		Product.ProductIdentifier item2 = Mockito.mock(Product.ProductIdentifier.class);
		Product.ProductIdentifier item3 = Mockito.mock(Product.ProductIdentifier.class);

		transactionList.add(new HashSet<>(Arrays.asList(item1, item2)));
		transactionList.add(new HashSet<>(Arrays.asList(item2, item3)));
		transactionList.add(new HashSet<>(Arrays.asList(item1, item3)));
		transactionList.add(new HashSet<>(Arrays.asList(item1, item2, item3)));

		double minimumSupport = 0.5;

		FrequentItemsetData result = generator.generate(transactionList, minimumSupport);

		assertNotNull(result, "Expected non-null FrequentItemsetData");
		assertFalse(result.getFrequentItemsetList().isEmpty(),
			"Expected non-empty list of frequent itemsets");

		result.getFrequentItemsetList().forEach(itemset -> {
			int count = (int) transactionList.stream().filter(transaction ->
				transaction.containsAll(itemset)).count();
			double support = (double) count / transactionList.size();
			assertTrue(support >= minimumSupport,
				"Itemset does not meet the minimum support requirement");
		});
	}

	@Test
	void testCheckSupportWithNaN() {
		AprioriFrequentItemsetGenerator generator = new AprioriFrequentItemsetGenerator();
		assertThrows(IllegalArgumentException.class,
			() -> generator.checkSupport(Double.NaN),
			"Should throw IllegalArgumentException for NaN support value.");
	}

	@Test
	void testCheckSupportWithTooLargeValue() {
		AprioriFrequentItemsetGenerator generator = new AprioriFrequentItemsetGenerator();
		assertThrows(IllegalArgumentException.class,
			() -> generator.checkSupport(1.1),
			"Should throw IllegalArgumentException for support value greater than 1.0.");
	}

	@Test
	void testCheckSupportWithNegativeValue() {
		AprioriFrequentItemsetGenerator generator = new AprioriFrequentItemsetGenerator();
		assertThrows(IllegalArgumentException.class,
			() -> generator.checkSupport(-0.1),
			"Should throw IllegalArgumentException for negative support value.");
	}

	@Test
	void testFindFrequentItems() {
		AprioriFrequentItemsetGenerator generator = new AprioriFrequentItemsetGenerator();

		Product.ProductIdentifier id1 = Mockito.mock(Product.ProductIdentifier.class);
		Product.ProductIdentifier id2 = Mockito.mock(Product.ProductIdentifier.class);
		Product.ProductIdentifier id3 = Mockito.mock(Product.ProductIdentifier.class);

		List<Set<Product.ProductIdentifier>> transactionList = new ArrayList<>();
		transactionList.add(new HashSet<>(Arrays.asList(id1, id3)));
		transactionList.add(new HashSet<>(Arrays.asList(id1, id3)));
		transactionList.add(new HashSet<>(Arrays.asList(id1, id2)));

		double minimumSupport = 0.5;

		FrequentItemsetData result = generator.generate(transactionList, minimumSupport);

		assertNotNull(result, "The result should not be null");

		boolean id2IsFrequent = result.getFrequentItemsetList().stream()
			.anyMatch(set -> set.contains(id2) && set.size() == 1);

		assertFalse(id2IsFrequent, "Itemset with id2 should not be frequent");
	}

}
