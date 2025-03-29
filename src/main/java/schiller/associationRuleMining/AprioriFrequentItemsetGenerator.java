package schiller.associationRuleMining;

import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.Product.ProductIdentifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AprioriFrequentItemsetGenerator {

	public FrequentItemsetData generate(List<Set<Product.ProductIdentifier>> transactionList, double minimumSupport) {
		Objects.requireNonNull(transactionList, "The itemset list is empty.");
		checkSupport(minimumSupport);

		if (transactionList.isEmpty()) {
			return null;
		}

		Map<Set<ProductIdentifier>, Integer> supportCountMap = new HashMap<>();

		List<Set<ProductIdentifier>> frequentItemList = findFrequentItems(transactionList,
			supportCountMap,
			minimumSupport);


		Map<Integer, List<Set<ProductIdentifier>>> map = new HashMap<>();
		map.put(1, frequentItemList);

		int k = 1;

		do {
			++k;

			List<Set<ProductIdentifier>> candidateList =
				generateCandidates(map.get(k - 1));

			for (Set<ProductIdentifier> transaction : transactionList) {
				List<Set<ProductIdentifier>> candidateList2 = subset(candidateList,
					transaction);

				for (Set<ProductIdentifier> itemset : candidateList2) {
					supportCountMap.put(itemset,
						supportCountMap.getOrDefault(itemset,
							0) + 1);
				}
			}

			map.put(k, getNextItemsets(candidateList,
				supportCountMap,
				minimumSupport,
				transactionList.size()));

		} while (!map.get(k).isEmpty());

		return new FrequentItemsetData(extractFrequentItemsets(map),
			supportCountMap,
			minimumSupport,
			transactionList.size());
	}

	private List<Set<ProductIdentifier>>
	extractFrequentItemsets(Map<Integer, List<Set<ProductIdentifier>>> map) {
		List<Set<ProductIdentifier>> ret = new ArrayList<>();

		for (List<Set<ProductIdentifier>> itemsetList : map.values()) {
			ret.addAll(itemsetList);
		}

		return ret;
	}

	public List<Set<ProductIdentifier>> getNextItemsets(List<Set<ProductIdentifier>> candidateList,
												 Map<Set<ProductIdentifier>, Integer> supportCountMap,
												 double minimumSupport,
												 int transactions) {
		List<Set<ProductIdentifier>> ret = new ArrayList<>(candidateList.size());

		for (Set<ProductIdentifier> itemset : candidateList) {
			if (supportCountMap.containsKey(itemset)) {
				int supportCount = supportCountMap.get(itemset);
				double support = 1.0 * supportCount / transactions;

				if (support >= minimumSupport) {
					ret.add(itemset);
				}
			}
		}

		return ret;
	}

	private List<Set<ProductIdentifier>> subset(List<Set<ProductIdentifier>> candidateList,
								Set<ProductIdentifier> transaction) {
		List<Set<ProductIdentifier>> ret = new ArrayList<>(candidateList.size());

		for (Set<ProductIdentifier> candidate : candidateList) {
			if (transaction.containsAll(candidate)) {
				ret.add(candidate);
			}
		}

		return ret;
	}

	private List<Set<ProductIdentifier>> generateCandidates(List<Set<ProductIdentifier>> itemsetList) {
		List<List<ProductIdentifier>> list = new ArrayList<>(itemsetList.size());

		for (Set<ProductIdentifier> itemset : itemsetList) {
			List<ProductIdentifier> l = new ArrayList<>(itemset);
			l.sort(ITEM_COMPARATOR);
			list.add(l);
		}

		int listSize = list.size();

		List<Set<ProductIdentifier>> ret = new ArrayList<>(listSize);

		for (int i = 0; i < listSize; ++i) {
			for (int j = i + 1; j < listSize; ++j) {
				Set<ProductIdentifier> candidate = tryMergeItemsets(list.get(i), list.get(j));

				if (candidate != null) {
					ret.add(candidate);
				}
			}
		}

		return ret;
	}

	private Set<ProductIdentifier> tryMergeItemsets(List<ProductIdentifier> itemset1,
													List<ProductIdentifier> itemset2) {
		int length = itemset1.size();

		for (int i = 0; i < length - 1; ++i) {
			if (!itemset1.get(i).equals(itemset2.get(i))) {
				return null;
			}
		}

		if (itemset1.get(length - 1).equals(itemset2.get(length - 1))) {
			return null;
		}

		Set<ProductIdentifier> ret = new HashSet<>(length + 1);

		for (int i = 0; i < length - 1; ++i) {
			ret.add(itemset1.get(i));
		}

		ret.add(itemset1.get(length - 1));
		ret.add(itemset2.get(length - 1));
		return ret;
	}

	public static final Comparator<Object> ITEM_COMPARATOR = (o1, o2) -> {
        if (o1 instanceof ProductIdentifier id1 && o2 instanceof ProductIdentifier id2) {
            return id1.toString().compareTo(id2.toString());
        }

        throw new IllegalArgumentException("Cannot compare non ProductIdentifier objects");
    };

	private List<Set<ProductIdentifier>> findFrequentItems(List<Set<ProductIdentifier>> itemsetList,
												   Map<Set<ProductIdentifier>, Integer> supportCountMap,
												   double minimumSupport) {
		Map<ProductIdentifier, Integer> map = new HashMap<>();

		for (Set<ProductIdentifier> itemset : itemsetList) {
			for (ProductIdentifier item : itemset) {
				Set<ProductIdentifier> tmp = new HashSet<>(1);
				tmp.add(item);

				supportCountMap.put(tmp,
					supportCountMap.getOrDefault(tmp, 0) + 1);

				map.put(item, map.getOrDefault(item, 0) + 1);
			}
		}

		List<Set<ProductIdentifier>> frequentItemsetList = new ArrayList<>();

		for (Map.Entry<ProductIdentifier, Integer> entry : map.entrySet()) {
			if (1.0 * entry.getValue() / itemsetList.size() >= minimumSupport) {
				Set<ProductIdentifier> itemset = new HashSet<>(1);
				itemset.add(entry.getKey());
				frequentItemsetList.add(itemset);
			}
		}

		return frequentItemsetList;
	}

	public void checkSupport(double support) {
		if (Double.isNaN(support)) {
			throw new IllegalArgumentException("The input support is NaN.");
		}

		if (support > 1.0) {
			throw new IllegalArgumentException(
				"The input support is too large: " + support + ", " +
					"should be at most 1.0");
		}

		if (support < 0.0) {
			throw new IllegalArgumentException(
				"The input support is too small: " + support + ", " +
					"should be at least 0.0");
		}
	}
}