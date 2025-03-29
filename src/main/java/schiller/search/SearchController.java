package schiller.search;

import jakarta.annotation.Nullable;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import schiller.inventory.productSpecialization.ProductSpecialization;
import schiller.inventory.productSpecialization.book.genre.Genre;
import schiller.inventory.productSpecialization.productExtension.category.Category;
import schiller.order.SchillerOrder;
import schiller.user.User;

import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

	private final SearchService searchService;

	SearchController(SearchService searchService) {
		this.searchService = searchService;
    }

    @GetMapping("/search")
    String search(Model model, @Nullable @RequestParam String searchTerm) {

		Streamable<ProductSpecialization> searchMatches = searchService.evaluateSearchTerm(searchTerm);
		Iterable<Genre> genres = searchService.allGenre();
		Iterable<Category> categories = searchService.allCategories();

		model.addAttribute("catalog", searchMatches);
		model.addAttribute("genres", genres);
		model.addAttribute("categories", categories);
		model.addAttribute("categoriesEmpty", categories.spliterator().getExactSizeIfKnown() == 0);
        model.addAttribute("title", "search.title");

        return "catalog";
    }

	@GetMapping("/orders/search")
	public String searchOrders(Model model, @RequestParam String searchTerm) {

		List<SchillerOrder> orders = searchService.searchOrders(searchTerm);

		model.addAttribute("orders", orders);

		return "orders";
	}

	@GetMapping("/users/search")
	public String searchUsers(Model model, @RequestParam String searchTerm) {
		List<User> users = searchService.searchUsers(searchTerm);
		Map<UserAccount, Integer> userOpenOrdersCount = searchService.getUserOpenOrdersCount(users);

		model.addAttribute("userlist", users);
		model.addAttribute("userOpenOrdersCount", userOpenOrdersCount);

		return "users";
	}

}
