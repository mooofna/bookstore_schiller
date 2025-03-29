package schiller.associationRuleMining;

import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import schiller.inventory.InventoryService;

import java.util.*;

@Service
public class AssociationRuleMiningService {

	private final AprioriFrequentItemsetGenerator generator = new AprioriFrequentItemsetGenerator();
	private final AssociationRuleMiningConfiguration associationRuleMiningConfiguration;
	private final List<Set<ProductIdentifier>> itemsetList = new ArrayList<>();
	private List<AssociationRule> associationRuleList;
	private final AssociationRuleGenerator associationRuleGenerator = new AssociationRuleGenerator();
	private final InventoryService inventoryService;

	public AssociationRuleMiningService(InventoryService inventoryService,
										AssociationRuleMiningConfiguration associationRuleMiningConfiguration) {
		Assert.notNull(inventoryService, "InventoryService must not be Null");
		Assert.notNull(associationRuleMiningConfiguration,
			"AssociationRuleMiningConfiguration must not be Null");

		this.inventoryService = inventoryService;
		this.associationRuleMiningConfiguration = associationRuleMiningConfiguration;
	}

	public void update(Set<ProductIdentifier> itemset) {
		updateItemSetList(itemset);
		updateAssociationRuleList();
	}

	public void updateItemSetList(Set<ProductIdentifier> itemset) {
		itemsetList.add(itemset);
	}

	public void updateAssociationRuleList() {

		FrequentItemsetData data = generator.generate(itemsetList,
			associationRuleMiningConfiguration.getMinimumSupport());
		associationRuleList = associationRuleGenerator.mineAssociationRules(data,
			associationRuleMiningConfiguration.getMinimumConfidence());
	}

	public Set<ProductIdentifier> getAssociatedProductIdentifiers(Set<ProductIdentifier> itemSet) {
		for (AssociationRule associationRule: associationRuleList) {
			Set<ProductIdentifier> antecedent = associationRule.getAntecedent();
			if (antecedent.equals(itemSet)) {
				return associationRule.getConsequent();
			}
		}
		return new HashSet<>();
	}

	public Streamable<Product> getAssociatedProducts(Product product) {
		Set<ProductIdentifier> associatedProductIdentifiers =
			this.getAssociatedProductIdentifiers(Set.of(Objects.requireNonNull(product.getId())));
		return inventoryService.getProductItemSet(associatedProductIdentifiers);
	}

}
