package schiller.associationRuleMining;

import org.salespointframework.catalog.Product;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import schiller.order.SchillerOrder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Order(30)
public class AssociationRuleDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(AssociationRuleMiningService.class);

	private final OrderManagement<SchillerOrder> orderManagement;

	private final AssociationRuleMiningService associationRuleMiningService;

	AssociationRuleDataInitializer(OrderManagement<SchillerOrder> orderManagement,
								   AssociationRuleMiningService associationRuleMiningService) {

		Assert.notNull(orderManagement, "Order management must not be null!");
		Assert.notNull(associationRuleMiningService, "AssociationRuleMiningService must not be null!");

		this.orderManagement = orderManagement;
		this.associationRuleMiningService = associationRuleMiningService;
	}

	@Override
	public void initialize() {

		PageRequest pageRequest = PageRequest.of(0, 10);

		List<SchillerOrder> orders = orderManagement.findAll(pageRequest).getContent();

		LOG.info("Creating default association rule entries.");

		for (SchillerOrder order: orders) {
			Set<Product.ProductIdentifier> itemSet = new HashSet<>();
            for (OrderLine orderLine : order.getOrderLines()) {
				itemSet.add(orderLine.getProductIdentifier());
            }
			associationRuleMiningService.updateItemSetList(itemSet);
		}

		associationRuleMiningService.updateAssociationRuleList();
	}
}
