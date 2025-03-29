package schiller.order;

import com.mysema.commons.lang.Assert;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import schiller.user.User;
import schiller.user.UserManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderManagementService {

	private final OrderManagement<SchillerOrder> orderManagement;
	private final UserManagement userManagement;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	private final SchillerOrderRepository schillerOrderRepository;


	@Autowired
	public OrderManagementService(OrderManagement<SchillerOrder> orderManagement,
								  UserManagement userManagement,
								  UniqueInventory<UniqueInventoryItem> inventory, SchillerOrderRepository schillerOrderRepository) {
		this.orderManagement = orderManagement;
		this.userManagement = userManagement;
		this.inventory = inventory;
		this.schillerOrderRepository = schillerOrderRepository;
	}

	public Page<SchillerOrder> getAllOrders(Pageable pageable) {
		return orderManagement.findAll(pageable);
	}

	public Streamable<SchillerOrder> getCompletedOrders() {
		return orderManagement.findBy(OrderStatus.COMPLETED);
	}

	public Streamable<SchillerOrder> getOpenedOrders() {
		return orderManagement.findBy(OrderStatus.OPEN);
	}

	public Streamable<SchillerOrder> getOrdersByUserAccount(UserAccount userAccount) {
		return orderManagement.findBy(userAccount);
	}

	public List<SchillerOrder> updatedOrders(Iterable<SchillerOrder> orders) {
		List<SchillerOrder> orderList = new ArrayList<>();

		for ( SchillerOrder order : orders ) {
			String name = getName(order);
			order.setUserName(name);
			orderList.add(order);
		}

		return orderList;
	}

	Delivery.DeliveryStatus getDeliveryStatus(Order.OrderIdentifier orderIdentifier) {
		Assert.notNull(orderIdentifier, "Order ID must not be null!");
		Optional<SchillerOrder> order = orderManagement.get(orderIdentifier);

		if (order.isPresent()) {
			SchillerOrder ord = order.get();
			if (ord.getDeliveryMethod() instanceof Delivery) {
				return ((Delivery) ord.getDeliveryMethod()).getDeliveryStatus();
			}
		}
		return null;
	}

	public void payOrder(String orderId) {
		Assert.notNull(orderId, "Order ID must not be null!");

		Order.OrderIdentifier orderIdentifier1 = Order.OrderIdentifier.of(orderId);
		Optional<SchillerOrder> order = orderManagement.get(orderIdentifier1);

		if (!order.isPresent()) {
			return;
		}

		SchillerOrder ord = order.get();

		if (ord.isOpen()) {
			orderManagement.payOrder(ord);
			DeliveryMethod deliveryMethod = ord.getDeliveryMethod();

			if (deliveryMethod instanceof Pickup) {
				completeOrderIfPossible(ord);
			} else if (deliveryMethod instanceof Delivery) {
				Delivery deliveryPayment = (Delivery) deliveryMethod;
				if (deliveryPayment.getDeliveryStatus() == Delivery.DeliveryStatus.DELIVERED) {
					completeOrderIfPossible(ord);
				}
			}
		}
	}

	public void deliverOrder(String orderId) {
		Assert.notNull(orderId, "Order ID must not be null!");

		Order.OrderIdentifier orderIdentifier1 = Order.OrderIdentifier.of(orderId);
		Optional<SchillerOrder> order = orderManagement.get(orderIdentifier1);

		if (!order.isPresent()) {
			return;
		}

		SchillerOrder ord = order.get();
		DeliveryMethod deliveryMethod = ord.getDeliveryMethod();

		if (deliveryMethod instanceof Delivery) {
			Delivery deliveryPayment = (Delivery) deliveryMethod;
			deliveryPayment.setDeliveryStatus(Delivery.DeliveryStatus.DELIVERED);

			completeOrderIfPossible(ord);
		}
	}

	public void cancelOrder(String orderId) {
		Assert.notNull(orderId, "Order ID must not be null!");

		Order.OrderIdentifier orderIdentifier = Order.OrderIdentifier.of(orderId);
		Optional<SchillerOrder> order = orderManagement.get(orderIdentifier);

		if (order.isPresent()) {
			SchillerOrder ord = order.get();
			if (ord.getOrderStatus() == OrderStatus.OPEN) {
				orderManagement.cancelOrder(ord, "Order cancelled by Boss");
			}
		}
	}

	public void markAsPickedUp(Order.OrderIdentifier orderId) {
		SchillerOrder order = getOrder(orderId);

		DeliveryMethod deliveryMethod = order.getDeliveryMethod();
		if (!(deliveryMethod instanceof Pickup pickup)) {
			throw new IllegalArgumentException("Order must be a Pick Up Order.");
		}
		if (pickup.isPickedUp()) {
			throw new IllegalArgumentException("Order has already been picked up.");
		}
		pickup.pickUp();
		orderManagement.save(order);
		completeOrderIfPossible(order);
	}

	public void markAsShipped(Order.OrderIdentifier orderId) {
		SchillerOrder order = getOrder(orderId);

		DeliveryMethod deliveryMethod = order.getDeliveryMethod();
		if (!(deliveryMethod instanceof Delivery delivery)) {
			throw new IllegalArgumentException("Order must be a Delivery Order");
		}
		if (!delivery.getDeliveryStatus().equals(Delivery.DeliveryStatus.READY_FOR_SHIPPING)) {
			throw new IllegalArgumentException("Order is not in the right state to be shipped.");
		}
		delivery.setDeliveryStatus(Delivery.DeliveryStatus.SHIPPED);
		orderManagement.save(order);
	}

	public void markAsDelivered(Order.OrderIdentifier orderId) {
		SchillerOrder order = getOrder(orderId);

		DeliveryMethod deliveryMethod = order.getDeliveryMethod();
		if (!(deliveryMethod instanceof Delivery delivery)) {
			throw new IllegalArgumentException("Order must be a Delivery Order");
		}
		if (!delivery.getDeliveryStatus().equals(Delivery.DeliveryStatus.SHIPPED)) {
			throw new IllegalArgumentException("Order has to be shipped to be delivered.");
		}
		delivery.setDeliveryStatus(Delivery.DeliveryStatus.DELIVERED);
		orderManagement.save(order);
		completeOrderIfPossible(order);
	}

	public void markCashOrderAsPaid(Order.OrderIdentifier orderId) {
		SchillerOrder order = getOrder(orderId);

		PaymentMethod paymentMethod = order.getPaymentMethod();
		if (!(paymentMethod instanceof Cash)) {
			throw new IllegalArgumentException("Order must be a Cash Order.");
		}
		if (order.isPaid() || order.isCompleted() || order.isCanceled()) {
			throw new IllegalArgumentException("Order is not in a state to be paid.");
		}
		orderManagement.payOrder(order);
		completeOrderIfPossible(order);
	}

	public void markBillAsSent(Order.OrderIdentifier orderId) {
		SchillerOrder order = getOrder(orderId);

		PaymentMethod paymentMethod = order.getPaymentMethod();
		if(!(paymentMethod instanceof Bill bill)) {
			throw new IllegalArgumentException("Order must be a Bill Order.");
		}
		if (bill.isSent()) {
			throw new IllegalArgumentException("Bill has already been sent.");
		}
		bill.send();
		orderManagement.save(order);
	}

	public void markBillAsPaid(Order.OrderIdentifier orderId) {
		SchillerOrder order = getOrder(orderId);

		PaymentMethod paymentMethod = order.getPaymentMethod();
		if (!(paymentMethod instanceof  Bill bill)) {
			throw new IllegalArgumentException("Order must be a Bill Order.");
		}
		if (order.isPaid() || order.isCanceled() || order.isCompleted()) {
			throw new IllegalArgumentException("Bill is not in a state to be paid.");
		}
		orderManagement.payOrder(order);
		completeOrderIfPossible(order);
	}

	public SchillerOrder getOrder(Order.OrderIdentifier orderId) {
		Optional<SchillerOrder> optionalOrder = orderManagement.get(orderId);
		if (optionalOrder.isEmpty()) {
			throw new IllegalArgumentException("OrderId not valid.");
		}
		return optionalOrder.get();
	}

	public void completeOrderIfPossible(SchillerOrder ord) {
		if (ord.isPaid() && ord.getDeliveryMethod().readyForCompletion()) {
			for (OrderLine orderLine : ord.getOrderLines()) {
				Optional<UniqueInventoryItem> productOptional =
					inventory.findByProductIdentifier(Objects.requireNonNull(orderLine.getProductIdentifier()));

				if (productOptional.isPresent()) {
					UniqueInventoryItem item = productOptional.get();
					item.increaseQuantity(orderLine.getQuantity());
					inventory.save(item);
				}
			}
			orderManagement.completeOrder(ord);
		}
	}

	public Optional<SchillerOrder> getOrderById(String orderId) {
		if (orderId == null || orderId.trim().isEmpty()) {
			return Optional.empty();
		}

		Order.OrderIdentifier orderIdentifier = Order.OrderIdentifier.of(orderId);
		return orderManagement.get(orderIdentifier);
	}

	public List<String> getProducts(Order order) {
		List<OrderLine> orderLines;

		orderLines = order.getOrderLines().stream().toList();
		List<String> products = new ArrayList<>();
		for (OrderLine orderLine:orderLines
		) {
			products.add(orderLine.getQuantity().getAmount().longValue() + "x" + orderLine.getProductName());
		}
		return products;
	}

	public String getEmail(Order order){
		Streamable<User> users = userManagement.findAll();
		for (User user:users
			 ) {
			if(Objects.equals(user.getUserAccount().getId(), order.getUserAccountIdentifier())){
				return user.getUserAccount().getEmail();
			}
		}
		return " ";
	}

	public String getName(Order order) {
		Streamable<User> users = userManagement.findAll();
		for (User user : users) {
			if(Objects.equals(user.getUserAccount().getId(), order.getUserAccountIdentifier())){
				return user.getUserAccount().getUsername();
			}
		}
		return " ";
	}

	public String getDeliveryMethod(Order order){
		if (order.getPaymentMethod().getClass().equals(Bill.class)){
			return "Delivery";
		} else{
			return "Pick Up";
		}
	}
	public SchillerOrder saveOrUpdateOrder(SchillerOrder order) {
		return schillerOrderRepository.save(order);
	}

	public List<SchillerOrder> getOpenedOrdersByUser(UserAccount userAccount) {
		Streamable<SchillerOrder> allOrders = getOrdersByUserAccount(userAccount);
		List<SchillerOrder> orderList = new ArrayList<>();
		orderList = allOrders.stream()
			.filter(order -> order.getOrderStatus() == OrderStatus.OPEN)
			.collect(Collectors.toList());

		return orderList;
	}

}