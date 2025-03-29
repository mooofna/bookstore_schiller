package schiller.catalog;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import schiller.associationRuleMining.AssociationRuleMiningService;
import schiller.inventory.productSpecialization.book.Book;
import schiller.inventory.productSpecialization.productExtension.ProductExtension;
import schiller.inventory.productSpecialization.ProductSpecialization;

import java.util.Optional;

@Controller
class CatalogController {

	private final CatalogService catalogService;
	private final AssociationRuleMiningService associationRuleMiningService;

	CatalogController(CatalogService catalogService,
					  AssociationRuleMiningService associationRuleMiningService
	) {

		Assert.notNull(catalogService, "CatalogService must not be Null");
		Assert.notNull(associationRuleMiningService, "AssociationRuleMiningService must not be Null");

		this.catalogService = catalogService;
		this.associationRuleMiningService = associationRuleMiningService;
	}

	@GetMapping("/catalog")
	String catalog(Model model, @Nullable @RequestParam Long genreId, @Nullable @RequestParam String categoryId) {

		Streamable<? extends ProductSpecialization> catalog = catalogService.getCatalog(genreId, categoryId);
		catalogService.prepareCatalogTemplate(model, catalog);

		return "catalog";
	}

	@GetMapping("/product/{product}")
	String detail(Model model, @PathVariable ProductSpecialization product, CatalogService.CommentAndRating form) {

		Quantity quantity = catalogService.quantityOfProduct(product);
		boolean orderable = catalogService.productIsOrderable(quantity);

		model.addAttribute("form", form);
		model.addAttribute("product", product);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", orderable);


		Streamable<Product> associatedProducts = associationRuleMiningService.getAssociatedProducts(product);
		model.addAttribute("associatedProducts", associatedProducts);
		model.addAttribute("associatedProductsEmpty", associatedProducts.isEmpty());

		Streamable<Book> sameGenreBooks = catalogService.getOtherBooksOfSameGenre(product);
		model.addAttribute("sameGenreBooks", sameGenreBooks);
		model.addAttribute("sameGenreBooksEmpty", sameGenreBooks.isEmpty());

		Streamable<Book> sameAuthorBooks = catalogService.getOtherBooksOfSameAuthor(product);
		model.addAttribute("sameAuthorBooks", sameAuthorBooks);
		model.addAttribute("sameAuthorBooksEmpty", sameAuthorBooks.isEmpty());

		Streamable<ProductExtension> sameCategoryProductExtensions =
			catalogService.getOtherProductExtensionsOfSameCategory(product);
		model.addAttribute("sameCategoryProductExtensions", sameCategoryProductExtensions);
		model.addAttribute("sameCategoryProductExtensionsEmpty", sameCategoryProductExtensions.isEmpty());

		return "product_details";
	}

	@PostMapping("/product/{product}/comments")
	@PreAuthorize("isAuthenticated()")
	public String comment(Model model, @PathVariable ProductSpecialization product,
						  @Valid CatalogService.CommentAndRating form, Errors errors,
						  @LoggedIn Optional<UserAccount> userAccount) {
		if (errors.hasErrors()) {
			return detail(model, product, form);
		}
		if (userAccount.isEmpty()){
			return "/login";
		}
		catalogService.addCommentToProduct(product, form, userAccount.get());
		model.addAttribute("product", product);
		return "redirect:/product/" + product.getId();
	}

	@GetMapping("/catalog/filter")
	public String filter(Model model,
						 @RequestParam("ProductType") String productType,
						 @Nullable @RequestParam("Price") Integer price) {

		Streamable<? extends ProductSpecialization> catalog = catalogService.getFilteredCatalog(productType, price);
		catalogService.prepareCatalogTemplate(model, catalog);

		return "catalog";
	}
}