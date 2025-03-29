package schiller.cart;

import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import schiller.inventory.productSpecialization.book.Book;
import schiller.associationRuleMining.AssociationRuleMiningService;
import schiller.order.Delivery;
import schiller.order.DeliveryMethod;
import schiller.order.Pickup;
import schiller.order.Bill;
import java.util.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@SessionAttributes("cart")
public class CartManagementController {

	private static final String[] bookNerdQuotes = {
		"\"A room without books is like a body without a soul.\" - Marcus Tullius Cicero",
		"\"There is no friend as loyal as a book.\" - Ernest Hemingway",
		"\"The only thing that you absolutely have to know, is the location of the library.\" - Albert Einstein",
		"\"Many people, myself among them, feel better at the mere sight of a book.\" - Jane Austen",
		"\"If you only read the books that everyone else is reading, you can " +
			"only think what everyone else is thinking.\" - Haruki Murakami",
		"\"That's the thing about books. They let you travel without moving your feet.\" - Jhumpa Lahiri",
		"\"Books are a uniquely portable magic.\" - Stephen King",
		"\"Let us read, and let us dance; these two amusements will never do any harm to the world.\" - Voltaire",
		"\"The reading of all good books is like a conversation with the finest minds of past centuries.\" - Rene Descartes",
		"\"I have always imagined that Paradise will be a kind of library.\" - Jorge Luis Borges",
		"\"Books are mirrors: you only see in them what you already have inside you.\" - Carlos Ruiz Zafon",
		"\"I find television very educating. Every time somebody turns " +
			"on the set, I go into the other room and read a book.\" - Groucho Marx",
		"\"Think before you speak. Read before you think.\" - Fran Lebowitz",
		"\"A book is a dream that you hold in your hands.\" - Neil Gaiman",
		"\"Books are the plane, and the train, and the road. " +
			"They are the destination and the journey. They are home.\" - Anna Quindlen",
		"\"In the end, we'll all become stories.\" - Margaret Atwood",
		"\"A good book is an event in my life.\" - Stendhal",
		"\"Reading brings us unknown friends\" - Honore de Balzac",
		"\"No two persons ever read the same book.\" - Edmund Wilson",
		"\"You can never get a cup of tea large enough or a book long enough to suit me.\" - C.S. Lewis",
		"\"Books are the treasured wealth of the world and the fit " +
			"inheritance of generations and nations.\" - Henry David Thoreau",
		"\"A great book should leave you with many experiences, and slightly " +
			"exhausted at the end. You live several lives while reading.\" - William Styron",
		"\"The world is a book, and those who do not travel read only one page.\" - Saint Augustine",
		"\"Books break the shackles of time, proof that humans can work magic.\" - Carl Sagan",
		"\"Some books leave us free and some books make us free.\" - Ralph Waldo Emerson",
		"\"A book must be the axe for the frozen sea within us.\" - Franz Kafka",
		"\"Books are the quietest and most constant of friends; they " +
			"are the most accessible and wisest of counselors, and the most patient of teachers.\" - Charles W. Eliot",
		"\"To acquire the habit of reading is to construct for " +
			"yourself a refuge from almost all the miseries of life.\" - W. Somerset Maugham",
		"\"Until I feared I would lose it, I never loved to read. One does not love breathing.\" - Harper Lee",
		"\"I owe everything I am and everything I will ever be to books.\" - Gary Paulsen",
		"\"There is no surer foundation for a beautiful friendship than a mutual taste in literature.\" - P.G. Wodehouse"
	};

	private final CartManagementService cartManagementService;
	private final AssociationRuleMiningService associationRuleMiningService;

	CartManagementController(CartManagementService cartManagementService,
							 AssociationRuleMiningService associationRuleMiningService) {
		Assert.notNull(cartManagementService, "Cart Management Service must not be null.");
		Assert.notNull(associationRuleMiningService, "Association Rule Mining Service must not be null.");

		this.cartManagementService = cartManagementService;
		this.associationRuleMiningService = associationRuleMiningService;
	}

	@ModelAttribute("cart")
	Cart initializeCart() { return new Cart(); }


	@GetMapping("/cart")
	String cartView(Model model, @ModelAttribute("cart") Cart cart) {
		Assert.notNull(cart,"Cart must not be null");
		Assert.notNull(model,"Model must not be null");
		Map<String, String> bookImages = new HashMap<>();
		Map<String, String> bookAuthors = new HashMap<>();
		Map<Product, Integer> productAvailability = cartManagementService.getProductAvailability(cart);
		for (CartItem item : cart) {
			Book book = cartManagementService.getBookFromCartItem(item);
			if (book != null) {
				bookImages.put(item.getId(), book.getImage());
				bookAuthors.put(item.getId(), book.getAuthor().getName());
			}
		}

		model.addAttribute("availability", productAvailability);
		model.addAttribute("bookImages", bookImages);
		model.addAttribute("bookAuthors", bookAuthors);
		if (!cartManagementService.isCartValid(cart)) {
			model.addAttribute("validationError", true);
		}
		return "cart";
	}

	@PostMapping("/cart/addOrUpdate")
	String addOrUpdate(@RequestParam("productId") Product product,
					   @RequestParam("amount") long number,
					   @ModelAttribute Cart cart,
					   @RequestParam("onCartSite") Boolean inCart){
		Assert.notNull(cart,"Cart must not be null");
		Assert.notNull(product,"Product must not be null");
		Assert.notNull(inCart,"Null Value for inCart should not be possible");
        cartManagementService.addOrUpdateCartItem(product, number, cart, inCart);

		if(Boolean.FALSE.equals(inCart)){
			return "redirect:/catalog";
		}
		return "redirect:/cart";
	}

	@PostMapping("/cart/deleteItem")
	String delete(@RequestParam("pid") String pid, @ModelAttribute Cart cart) {
		Assert.notNull(cart,"Cart must not be null");
		Assert.notNull(pid,"Product must not be null");

		cartManagementService.deleteCartItem(pid, cart);

		return "redirect:/cart";
	}

	@PostMapping("/cart/checkout")
	@PreAuthorize("isAuthenticated()")
	String checkout(@ModelAttribute("cart") Cart cart,
					@RequestParam("OrderType") String orderType,
					@LoggedIn Optional<UserAccount> userAccount,
					Model model,
					AddressForm addressForm) {
		Assert.notNull(cart, "Cart must not be null");
		Assert.notNull(userAccount, "Useraccount must not be null");
		Assert.notNull(orderType, "orderType must not be null");
		Assert.notNull(model, "Model must not be null");

		if (userAccount.isPresent() && userAccount.get().hasRole(Role.of("BOSS"))) {
			return "redirect:/index";
		}
		if (userAccount.isPresent() && userAccount.get().hasRole(Role.of("EMPLOYEE"))) {
			return "redirect:/index";
		}

		return userAccount.map(account -> {
			if (!cartManagementService.isCartValid(cart)) {
				model.addAttribute("validationError", true);
				return cartView(model, cart);
			}

			if (orderType.equals("Delivery")) {
				String billingAddress = cartManagementService.getBillingAddressFromUser(account);
				model.addAttribute("billingAddress", billingAddress);
				return "delivery_details";
			}
			cartManagementService.createOrder(cart, Cash.CASH, new Pickup(), account);

			associationRuleMiningService.update(cartManagementService.getProductIdentifiers(cart));

			model.addAttribute("randomBookNerdQuote", randomQuote());
			return "thank_you";
		}).orElse("custom_error");
	}

	@PostMapping("/cart/checkout/delivery")
	String deliveryCheckout(@ModelAttribute("cart") Cart cart,
							@LoggedIn Optional<UserAccount> userAccount,
							@RequestParam("addressChoice") String addressChoice,
							@ModelAttribute("billingAddress") String billingAddress,
							AddressForm addressForm,
							Model model) {
		Assert.notNull(cart,"Cart must not be null");
		Assert.notNull(userAccount,"Useraccount must not be null");
		Assert.notNull(addressChoice,"Address choice must not be null");
		Assert.notNull(billingAddress,"Billing-Address must not be null");
		Assert.notNull(addressForm,"addressForm must not be null");

		associationRuleMiningService.update(cartManagementService.getProductIdentifiers(cart));

		return userAccount.map( account -> {
			if (!cartManagementService.isCartValid(cart)) {
				model.addAttribute("validationError", true);
				return cartView(model, cart);
			}

			String shippingAddress;

			if (addressChoice.equals("default")){
				shippingAddress = billingAddress;
			} else {
				shippingAddress = addressForm.compressAddress();
			}

			DeliveryMethod deliveryMethod = new Delivery(shippingAddress);
			cartManagementService.createOrder(cart, new Bill(), deliveryMethod, account);

			associationRuleMiningService.update(cartManagementService.getProductIdentifiers(cart));

			model.addAttribute("randomBookNerdQuote", randomQuote());
			return  "thank_you";
		}).orElse("custom_error");
	}

	@PostMapping("/cart/empty_cart")
	String empty(@ModelAttribute Cart cart) {
		Assert.notNull(cart,"Cart must not be null");
			cart.clear();
 			return ("redirect:/cart");
	}

	@PostMapping("/cart/reset") //method only exists to follow rule java:S3753
	String reset(SessionStatus sessionStatus){
		sessionStatus.setComplete();
		return "/catalog";
	}

	public static String randomQuote() {
		return bookNerdQuotes[new Random().nextInt(bookNerdQuotes.length)];
	}

}
