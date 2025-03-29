package schiller.cart;

import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.stereotype.Service;
import schiller.inventory.productSpecialization.book.Book;
import schiller.order.DeliveryMethod;
import schiller.order.SchillerOrder;
import schiller.user.Customer;
import schiller.user.UserManagement;

import java.util.*;

@Service
public class CartManagementService {

	private final OrderManagement<SchillerOrder> orderManagement;

	private final UniqueInventory<UniqueInventoryItem> inventory;

	private final UserManagement userManagement;

	CartManagementService(OrderManagement<SchillerOrder> orderManagement,
						  UniqueInventory<UniqueInventoryItem> inventory,
						  UserManagement userManagement) {

		this.orderManagement = orderManagement;
		this.inventory = inventory;
		this.userManagement = userManagement;
	}

	Map<Product, Integer> getProductAvailability(Cart cart) {
		Map<Product, Integer> productAvailability = new HashMap<>();
		for (CartItem item:cart) {
			Optional<UniqueInventoryItem> inventoryItem = inventory.findByProductIdentifier(
				Objects.requireNonNull(item.getProduct().getId())
			);
			inventoryItem.ifPresent(uniqueInventoryItem ->
				productAvailability.put(
					item.getProduct(),
					uniqueInventoryItem
						.getQuantity()
						.getAmount()
						.intValue())
			);
		}
		return productAvailability;
	}

	public Book getBookFromCartItem(CartItem item) {
		Product product = item.getProduct();
		if (product instanceof Book) {
			return (Book) product;
		}
		return null;
	}


	void addOrUpdateCartItem(Product product, long number, Cart cart, Boolean inCart) {
		String productID = Objects.requireNonNull(product.getId()).toString();
		Optional<CartItem> cartItem = findItemByProductId(cart, Objects.requireNonNull(productID));
		Optional<UniqueInventoryItem> inventoryItem = inventory
			.findByProductIdentifier(Objects.requireNonNull(product.getId()));
		long availableQuantity;
        availableQuantity = inventoryItem.map(uniqueInventoryItem ->
			uniqueInventoryItem.getQuantity().getAmount().longValue()).orElse(0L);
		long currentAmount = cartItem.map( item -> item.getQuantity().getAmount().longValue()).orElse((long) 0);
		long availableCatalogQuantity = availableQuantity-currentAmount;
		long amount = Math.max(0, Math.min(number, availableQuantity));
		if(Boolean.TRUE.equals(inCart)){
			cart.addOrUpdateItem(product, Quantity.of(amount - currentAmount));
		} else {
			cart.addOrUpdateItem(product,Quantity.of(Math.min(amount,availableCatalogQuantity)));
		}
	}

	void deleteCartItem(String pid, Cart cart) {
		Optional<CartItem> cartItem = findItemByProductId(cart, pid);
		cartItem.ifPresent(item -> cart.removeItem(item.getId()));
	}

	private Optional<CartItem> findItemByProductId(Cart cart, String productId) {
		org.springframework.util.Assert.notNull(cart, "Cart must not be null!");
		org.springframework.util.Assert.notNull(productId, "Product ID must not be null!");

		return cart.stream()
			.filter(cartItem -> productId.equals(Objects.requireNonNull(cartItem.getProduct().getId()).toString()))
			.findFirst();
	}

	String getBillingAddressFromUser(UserAccount userAccount) {
		Optional<Customer> user = userManagement.findCustomers()
			.filter(possibleUser -> possibleUser.getUserAccount().equals(userAccount))
			.stream().findFirst();
		if (user.isEmpty()){
			throw new IllegalArgumentException("No customer accounts");
		} else {return user.get().getAddress();
		}
	}

	SchillerOrder createOrder(Cart cart,
							  PaymentMethod paymentMethod,
							  DeliveryMethod deliveryMethod,
							  UserAccount account) {
		SchillerOrder order = new SchillerOrder(account.getId(), paymentMethod, deliveryMethod);
		cart.addItemsTo(order);
		cart.clear();
		orderManagement.save(order);

		for (OrderLine orderLine : order.getOrderLines()) {
			Optional<UniqueInventoryItem> productOptional =
				inventory.findByProductIdentifier(Objects.requireNonNull(orderLine.getProductIdentifier()));

			if (productOptional.isPresent()) {
				UniqueInventoryItem item = productOptional.get();
				item.decreaseQuantity(orderLine.getQuantity());
				inventory.save(item);
			}
		}

		return order;
	}

boolean isCartValid(Cart cart) {
    Map<Product, Integer> productAvailability = getProductAvailability(cart);
    for (CartItem item : cart) {
        if (!isItemValid(item, productAvailability, cart)) {
            return false;
        }
    }
    return true;
}

private boolean isItemValid(CartItem item, Map<Product, Integer> productAvailability, Cart cart) {
    int availableQuantity = productAvailability.get(item.getProduct());
    int requestedQuantity = item.getQuantity().getAmount().intValue();
    if (requestedQuantity > availableQuantity) {
        if (availableQuantity == 0) {
            cart.removeItem(item.getId());
        } else {
            addOrUpdateCartItem(item.getProduct(), availableQuantity, cart, true);
        }
        return false;
    }
    return true;
}


	Set<ProductIdentifier> getProductIdentifiers(Cart cart) {
		Set<ProductIdentifier> productIdentifiers = new HashSet<>();
		for (CartItem item:cart) {
			productIdentifiers.add(Objects.requireNonNull(item.getProduct().getId()));
		}
		return productIdentifiers;
	}
}
