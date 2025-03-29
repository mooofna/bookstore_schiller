package schiller.associationRuleMining;

import lombok.Getter;
import org.salespointframework.catalog.Product.ProductIdentifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FrequentItemsetData {

	@Getter
	private final List<Set<ProductIdentifier>> frequentItemsetList;
	@Getter
	private final Map<Set<ProductIdentifier>, Integer> supportCountMap;
	@Getter
	private final double minimumSupport;
	private final int numberOfTransactions;

	FrequentItemsetData(List<Set<ProductIdentifier>> frequentItemsetList,
						Map<Set<ProductIdentifier>, Integer> supportCountMap,
						double minimumSupport,
						int transactionNumber) {
		this.frequentItemsetList = frequentItemsetList;
		this.supportCountMap = supportCountMap;
		this.minimumSupport = minimumSupport;
		this.numberOfTransactions = transactionNumber;
	}

	public int getTransactionNumber() {
		return numberOfTransactions;
	}

	public double getSupport(Set<ProductIdentifier> itemset) {
		return 1.0 * supportCountMap.get(itemset) / numberOfTransactions;
	}
}

