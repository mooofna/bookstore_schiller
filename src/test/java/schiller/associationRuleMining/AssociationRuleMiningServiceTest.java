package schiller.associationRuleMining;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.util.Streamable;
import schiller.inventory.InventoryService;
import java.util.Collections;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AssociationRuleMiningServiceTest {

	private AssociationRuleMiningService service;
	private InventoryService inventoryService;

	@BeforeEach
	void setUp() {
		inventoryService = Mockito.mock(InventoryService.class);
		AssociationRuleMiningConfiguration configuration = Mockito.mock(AssociationRuleMiningConfiguration.class);
		service = new AssociationRuleMiningService(inventoryService, configuration);
	}

	@Test
	void testGetAssociatedProductIdentifiers() {
		ProductIdentifier antecedentId = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier consequentId = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet = Collections.singleton(antecedentId);
		Set<ProductIdentifier> consequentSet = Collections.singleton(consequentId);
		AssociationRule rule = new AssociationRule(antecedentSet, consequentSet, 0.8);

		service.updateItemSetList(antecedentSet);
		service.updateAssociationRuleList();
		ReflectionTestUtils.setField(service, "associationRuleList", Collections.singletonList(rule));

		Set<ProductIdentifier> result = service.getAssociatedProductIdentifiers(antecedentSet);
		assertEquals(consequentSet, result, "Expected consequentSet does not match actual result.");
	}

	@Test
	void testGetAssociatedProducts() {
		ProductIdentifier productId = Mockito.mock(ProductIdentifier.class);
		Product mockProduct = Mockito.mock(Product.class);
		when(mockProduct.getId()).thenReturn(productId);
		Streamable<Product> mockStreamable = Mockito.mock(Streamable.class);

		when(inventoryService.getProductItemSet(any())).thenReturn(mockStreamable);

		ProductIdentifier antecedentId = Mockito.mock(ProductIdentifier.class);
		ProductIdentifier consequentId = Mockito.mock(ProductIdentifier.class);
		Set<ProductIdentifier> antecedentSet = Collections.singleton(antecedentId);
		Set<ProductIdentifier> consequentSet = Collections.singleton(consequentId);
		AssociationRule rule = new AssociationRule(antecedentSet, consequentSet, 0.8);

		service.updateItemSetList(antecedentSet);
		service.updateAssociationRuleList();
		ReflectionTestUtils.setField(service, "associationRuleList", Collections.singletonList(rule));

		Streamable<Product> result = service.getAssociatedProducts(mockProduct);

		assertNotNull(result, "Result should not be null.");
		assertEquals(mockStreamable, result, "Result does not match expected value.");
	}

}
