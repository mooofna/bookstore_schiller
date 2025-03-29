package schiller.associationRuleMining;

import ch.qos.logback.classic.spi.LoggerComparator;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssociationRuleGenerator {
	public List<AssociationRule>
	mineAssociationRules(FrequentItemsetData data,
						 double minimumConfidence) {
		Objects.requireNonNull(data, "The frequent itemset data is null.");
		checkMinimumConfidence(minimumConfidence);

		Set<AssociationRule> resultSet = new HashSet<>();

		for (Set<ProductIdentifier> itemset : data.getFrequentItemsetList()) {
			if (itemset.size() < 2) {
				continue;
			}

			Set<AssociationRule> basicAssociationRuleSet =
				generateAllBasicAssociationRules(itemset, data);

			generateAssociationRules(itemset,
				basicAssociationRuleSet,
				data,
				minimumConfidence,
				resultSet);
		}

		List<AssociationRule> ret = new ArrayList<>(resultSet);

		ret.sort((a1, a2) -> {
            return Double.compare(a2.getConfidence(),
                    a1.getConfidence());
        });

		return ret;
	}

	public void generateAssociationRules(Set<ProductIdentifier> itemset,
								  Set<AssociationRule> ruleSet,
								  FrequentItemsetData data,
								  double minimumConfidence,
								  Set<AssociationRule> collector) {
		if (ruleSet.isEmpty()) {
			return;
		}

		int k = itemset.size();
		int m = ruleSet.iterator().next().getConsequent().size();

		if (k > m + 1) {
			Set<AssociationRule> nextRules =
				moveOneItemToConsequents(itemset, ruleSet, data);

			Iterator<AssociationRule> iterator = nextRules.iterator();

			while (iterator.hasNext()) {
				AssociationRule rule = iterator.next();

				if (rule.getConfidence() >= minimumConfidence) {
					collector.add(rule);
				} else {
					iterator.remove();
				}
			}

			generateAssociationRules(itemset,
				nextRules,
				data,
				minimumConfidence,
				collector);
		}
	}

	private Set<AssociationRule>
	moveOneItemToConsequents(Set<ProductIdentifier> itemset,
							 Set<AssociationRule> ruleSet,
							 FrequentItemsetData data) {
		Set<AssociationRule> output = new HashSet<>();
		Set<ProductIdentifier> antecedent = new HashSet<>();
		Set<ProductIdentifier> consequent = new HashSet<>();
		double itemsetSupportCount = data.getSupportCountMap().get(itemset);

		for (AssociationRule rule : ruleSet) {
			antecedent.clear();
			consequent.clear();
			antecedent.addAll(rule.getAntecedent());
			consequent.addAll(rule.getConsequent());

			for (ProductIdentifier item : rule.getAntecedent()) {
				antecedent.remove(item);
				consequent.add(item);

				int antecedentSupportCount = data.getSupportCountMap()
					.get(antecedent);
				AssociationRule newRule =
					new AssociationRule(
						antecedent,
						consequent,
						itemsetSupportCount / antecedentSupportCount);

				output.add(newRule);

				antecedent.add(item);
				consequent.remove(item);
			}
		}

		return output;
	}

	private Set<AssociationRule>
	generateAllBasicAssociationRules(Set<ProductIdentifier> itemset,
									 FrequentItemsetData data) {
		Set<AssociationRule> basicAssociationRuleSet =
			new HashSet<>(itemset.size());

		Set<ProductIdentifier> antecedent = new HashSet<>(itemset);
		Set<ProductIdentifier> consequent = new HashSet<>(1);

		for (ProductIdentifier item : itemset) {
			antecedent.remove(item);
			consequent.add(item);

			int itemsetSupportCount = data.getSupportCountMap().get(itemset);
			int antecedentSupportCount = data.getSupportCountMap()
				.get(antecedent);

			double confidence = 1.0 * itemsetSupportCount
				/ antecedentSupportCount;

			basicAssociationRuleSet.add(new AssociationRule(antecedent,
				consequent,
				confidence));
			antecedent.add(item);
			consequent.remove(item);
		}

		return basicAssociationRuleSet;
	}

	public void checkMinimumConfidence(double minimumConfidence) {
		if (Double.isNaN(minimumConfidence)) {
			throw new IllegalArgumentException(
				"The input minimum confidence is NaN.");
		}

		if (minimumConfidence < 0.0) {
			throw new IllegalArgumentException(
				"The input minimum confidence is negative: " +
					minimumConfidence + ". " +
					"Must be at least zero.");
		}

		if (minimumConfidence > 1.0) {
			throw new IllegalArgumentException(
				"The input minimum confidence is too large: " +
					minimumConfidence + ". " +
					"Must be at most 1.");
		}
	}
}