package schiller.order;

import jakarta.validation.Valid;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import schiller.user.Customer;
import schiller.user.RegistrationForm;
import schiller.user.User;
import schiller.user.UserManagement;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
@Controller
public class OrderManagementController {

	private final OrderManagementService orderManagementService;

	private final UserManagement userManagement;

	@Autowired
	public OrderManagementController(OrderManagementService orderManagementService,
									 UserManagement userManagement) {

		this.orderManagementService = orderManagementService;
		this.userManagement = userManagement;
	}

	@GetMapping("/orders")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE') or hasRole('BOSS')")
	public String orders(Model model, Pageable pageable, @LoggedIn UserAccount userAccount) {
		List<SchillerOrder> orders = new ArrayList<>();
		if(userAccount.hasRole(Role.of("BOSS")) || userAccount.hasRole(Role.of("EMPLOYEE"))){
			Page<SchillerOrder> orderPage = orderManagementService.getAllOrders(pageable);
			orders = orderManagementService.updatedOrders(orderPage);
		} else {
			Streamable<SchillerOrder> orderStream = orderManagementService.getOrdersByUserAccount(userAccount);
			orders = orderManagementService.updatedOrders(orderStream);
		}

		model.addAttribute("orders", orders);
		return "orders";
	}

	@PostMapping("/orders/cancel")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String cancelOrder(@RequestParam String orderId) {
		orderManagementService.cancelOrder(orderId);
		return "redirect:/orders";
	}

	@PostMapping("/orders/pickup")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String markAsPickedUp(@RequestParam Order.OrderIdentifier orderId) {
		orderManagementService.markAsPickedUp(orderId);
		return "redirect:/orders";
	}

	@PostMapping("/orders/shipOrder")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String markAsShipped(@RequestParam Order.OrderIdentifier orderId) {
		orderManagementService.markAsShipped(orderId);
		return "redirect:/orders";
	}

	@PostMapping("/orders/deliverOrder")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String markAsDelivered(@RequestParam Order.OrderIdentifier orderId) {
		orderManagementService.markAsDelivered(orderId);
		return "redirect:/orders";
	}

	@PostMapping("/orders/pay/cash/markPaid")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String markCashOrderAsPaid(@RequestParam Order.OrderIdentifier orderId) {
		orderManagementService.markCashOrderAsPaid(orderId);
		return "redirect:/orders";
	}

	@PostMapping("/orders/pay/bill/sendBill")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String markBillAsSent(@RequestParam Order.OrderIdentifier orderId) {
		orderManagementService.markBillAsSent(orderId);
		return "redirect:/orders";
	}

	@PostMapping("/orders/pay/bill/markPaid")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String markBillAsPaid(@RequestParam Order.OrderIdentifier orderId) {
		orderManagementService.markBillAsPaid(orderId);
		return "redirect:/orders";
	}

	@GetMapping("/showSearchOrder")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String showSearchForm(Model model) {

		model.addAttribute("searchModel", new SearchModel());

		return "searchorder.html";
	}

	@GetMapping("/orderdetail")
	public String orderDetail(@RequestParam String orderId, Model model) {
		Optional<SchillerOrder> orderOptional = orderManagementService.getOrderById(orderId);

		if (orderOptional.isEmpty()){
			throw new IllegalArgumentException("No such order");
		}
		Order order = orderOptional.get();
		String userAccountId = order.getUserAccountIdentifier().toString();
		List<String> products = orderManagementService.getProducts(order);
		String email = orderManagementService.getEmail(order);
		String userName = orderManagementService.getName(order);
		String deliveryMethod = orderManagementService.getDeliveryMethod(order);
		User user = userManagement.findAll().stream()
			.filter(u -> Objects.requireNonNull(u.getUserAccount().getId()).toString().equals(userAccountId))
			.findFirst()
			.orElse(null);

		model.addAttribute("deliveryMethod", deliveryMethod);
		model.addAttribute("order", order);
		model.addAttribute("userName", userName);
		model.addAttribute("user", user);
		model.addAttribute("products", products);
		model.addAttribute("email", email);

		return "orderdetail";
	}

	@GetMapping("/orders/open")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String showOpenOrders(Model model) {
		Streamable<SchillerOrder> openOrders = orderManagementService.getOpenedOrders();
		model.addAttribute("orders", openOrders);
		return "orders";
	}

	@GetMapping("/orders/completed")
	@PreAuthorize("hasRole('BOSS') or hasRole('EMPLOYEE')")
	public String showCompletedOrders(Model model) {
		Streamable<SchillerOrder> completedOrders = orderManagementService.getCompletedOrders();
		model.addAttribute("orders", completedOrders);
		return "orders";
	}

	@GetMapping("/orderdetail/edit")
	public String editOrderDetail(@RequestParam String orderId, Model model) {
		Optional<SchillerOrder> orderOptional = orderManagementService.getOrderById(orderId);

		if (orderOptional.isEmpty()) {
			throw new IllegalArgumentException("No such order");
		}

		Order order = orderOptional.get();
		model.addAttribute("order", order);
		model.addAttribute("userName", orderManagementService.getName(order));
		model.addAttribute("email", orderManagementService.getEmail(order));

		return "edit_order";
	}

	@PostMapping("/orderdetail/edit")
	public String saveEditedOrder(@ModelAttribute SchillerOrder editedOrder) {
		orderManagementService.saveOrUpdateOrder(editedOrder);
		return "redirect:/orderdetail?orderId=" + editedOrder.getId();
	}

	@PostMapping("/orderdetail/update")
	public String updateOrderDetail(@RequestParam String orderId, @ModelAttribute Order updatedOrder, Model model) {
		Optional<SchillerOrder> orderOptional = orderManagementService.getOrderById(orderId);

		if (orderOptional.isEmpty()) {
			throw new IllegalArgumentException("No such order");
		}

		SchillerOrder order = orderOptional.get();


		return "redirect:/orderdetail?orderId=" + orderId;
	}

}

