package internetkaufhaus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import internetkaufhaus.entities.ConcreteProduct;
import internetkaufhaus.model.StartPage;
import internetkaufhaus.services.DataService;

/**
 * This is the shop controller. It controls the shop. Or maybe it shops the
 * controls? You never know... In this class you may find the controllers for
 * general pages, should you choose to look for them.
 * 
 * @author max
 *
 */
@Controller
@SessionAttributes("cart")
public class ShopController {

	/** The start page. */
	@Autowired
	private StartPage startPage;

	/** The data service. */
	@Autowired
	private DataService dataService;

	/**
	 * This is a Request Mapping. It Maps Requests. Or does it Request Maps?
	 * This page shows the main page.
	 *
	 * @param model
	 *            the model
	 * @return the string
	 */
	@RequestMapping(value = { "/", "/index" })
	public String index(ModelMap model) {
		model.addAttribute("selection", this.startPage.getSelectionProducts());
		model.addAttribute("banner", this.startPage.getBannerProducts());
		model.addAttribute("selection", startPage.getSelectionProducts());
		List<ConcreteProduct> top5rated = dataService.getConcreteProductRepository()
				.findAll(
						new PageRequest(0, 5,
								new Sort(new Sort.Order(Sort.Direction.DESC, "averageRating", Sort.NullHandling.NATIVE),
										new Sort.Order(Sort.Direction.ASC, "name", Sort.NullHandling.NATIVE))))
				.getContent();
		model.addAttribute("top5rated", top5rated);
		List<ConcreteProduct> top5sold = dataService.getConcreteProductRepository()
				.findAll(
						new PageRequest(0, 5,
								new Sort(
										new Sort.Order(Sort.Direction.DESC, "amountProductsSold",
												Sort.NullHandling.NATIVE),
										new Sort.Order(Sort.Direction.ASC, "name", Sort.NullHandling.NATIVE))))
				.getContent();
		model.addAttribute("top5sold", top5sold);
		return "index";
	}

	/**
	 * Agb.
	 *
	 * @return the string
	 */
	@RequestMapping(value = "/agb")
	public String agb() {
		return "agb";
	}

	/**
	 * Impressum.
	 *
	 * @return the string
	 */
	@RequestMapping(value = "/impressum")
	public String impressum() {
		return "impressum";
	}

	/**
	 * Datenschutz.
	 *
	 * @return the string
	 */
	@RequestMapping(value = "/datenschutz")
	public String datenschutz() {
		return "datenschutz";
	}

	/**
	 * Kontakt.
	 *
	 * @return the string
	 */
	@RequestMapping(value = "/contact")
	public String contact() {
		return "contact";
	}
}
