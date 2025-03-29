package schiller.order;

import org.salespointframework.catalog.Product;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import schiller.inventory.productSpecialization.book.BookCatalog;
import schiller.user.UserManagement;

import java.util.*;

@Component
@Order(20)
class OrderDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(OrderDataInitializer.class);

	private final OrderManagement<SchillerOrder> orderManagement;

	private final UserManagement userManagement;

	private final BookCatalog bookCatalog;

	private final UniqueInventory<UniqueInventoryItem> inventory;


	OrderDataInitializer(OrderManagement<SchillerOrder> orderManagement,
						 UserManagement userManagement,
						 BookCatalog bookCatalog,
						 UniqueInventory<UniqueInventoryItem> inventory) {

		Assert.notNull(orderManagement, "OrderManagement must not be null!");
		Assert.notNull(bookCatalog, "Book catalog must not be null!");

		this.orderManagement = orderManagement;
		this.userManagement = userManagement;
		this.bookCatalog = bookCatalog;
		this.inventory = inventory;
	}

	@Override
	public void initialize() {

		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<SchillerOrder> orders = orderManagement.findAll(pageRequest);

		if (orders.hasContent()) {
			return;
		}

		Map<Product, Quantity> allProducts = getAllProducts();
		List<UserAccount.UserAccountIdentifier> allUserIds = getAllUserIds();

		for (int i = 0; i < 5; i++) {
			SchillerOrder schillerOrder = createOrder(allUserIds.get(new Random().nextInt(allUserIds.size())),
				Cash.CASH, new Pickup(), allProducts);
			orderManagement.save(schillerOrder);
		}
		SchillerOrder schillerOrder = createOrder(allUserIds.get(new Random().nextInt(allUserIds.size())),
				Cash.CASH, new Pickup(), allProducts);
			orderManagement.save(schillerOrder);

		LOG.info("Creating default order entries.");

	}

	private SchillerOrder createOrder(UserAccount.UserAccountIdentifier userAccountIdentifier,
									  PaymentMethod paymentMethod,
									  DeliveryMethod deliveryMethod,
									  Map<Product, Quantity> products) {
		SchillerOrder schillerOrder = new SchillerOrder(userAccountIdentifier, paymentMethod, deliveryMethod);

		for (Product product : products.keySet()) {
			schillerOrder.addOrderLine(product, products.get(product));
			Optional<UniqueInventoryItem> productOptional = inventory
				.findByProductIdentifier(Objects.requireNonNull(product.getId()));

			if (productOptional.isPresent()) {
				UniqueInventoryItem item = productOptional.get();
				item.decreaseQuantity(products.get(product));
				inventory.save(item);
			}
		}

		return schillerOrder;
	}

	Map<Product, Quantity> getAllProducts() {
		Map<Product, Quantity> allProducts = new HashMap<>();
		bookCatalog.findAll().forEach(book -> {
			if (inventory.findByProduct(book).isPresent()) {
				allProducts.put(book, Quantity.of(1));
			}
		});
		return allProducts;
	}

	List<UserAccount.UserAccountIdentifier> getAllUserIds() {
		List<UserAccount.UserAccountIdentifier> allUserIds = new ArrayList<>();
		userManagement.findAll().forEach(user -> {
			allUserIds.add(user.getUserAccount().getId());
		});
		return allUserIds;
	}

}

