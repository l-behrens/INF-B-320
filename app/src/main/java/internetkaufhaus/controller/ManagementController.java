package internetkaufhaus.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.lang.Object;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;
import javax.validation.Valid;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.collections.IteratorUtils;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderLine;

import internetkaufhaus.forms.EditArticleForm;
import internetkaufhaus.forms.ChangeStartPageForm;
import internetkaufhaus.forms.StockForm;

import internetkaufhaus.model.Comment;
import internetkaufhaus.model.ConcreteProduct;
import internetkaufhaus.model.ConcreteOrder;
import internetkaufhaus.model.Search;
import internetkaufhaus.model.StockManager;

@Controller
@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
public class ManagementController {

	private static final Quantity NONE = Quantity.of(0);
	private final Catalog<ConcreteProduct> catalog;
	private final Inventory<InventoryItem> inventory;
	private final Search prodSearch;
	private final OrderManager<ConcreteOrder> orderManager;
	private final StockManager stock;
	//private final List<ConcreteProduct> carousselList;
	//private final List<ConcreteProduct> selectionList;

	@Autowired
	public ManagementController(Catalog<ConcreteProduct> catalog, Inventory<InventoryItem> inventory, Search prodSearch, OrderManager<ConcreteOrder> orderManager, StockManager stock) {
		this.catalog = catalog;
		this.inventory = inventory;
		this.prodSearch = prodSearch;
		this.orderManager = orderManager;
		this.stock = stock;
		//this.carousselList = carousselList;
		//this.selectionList = selectionList;
	}

	@RequestMapping("/employee")
	public String employeeStart(@LoggedIn Optional<UserAccount> userAccount, ModelMap model) {
		model.addAttribute("account", userAccount.get());
		return "employee";
	}

	@RequestMapping("/employee/changecatalog")
	public String articleManagement(Optional<UserAccount> userAccount, ModelMap model) {
		model.addAttribute("prod50", catalog.findAll());
		model.addAttribute("inventory", inventory);

		return "changecatalog";
	}

	@RequestMapping("/employee/changecatalog/addArticle")
	public String addArticle(Optional<UserAccount> userAccount, ModelMap model) {
		model.addAttribute("categories", prodSearch.getCagegories());
		model.addAttribute("categories", prodSearch.getCagegories());
		return "changecatalognewitem";
	}

	@RequestMapping("/employee/acceptComment/{comId}")
	public String acceptComments(@PathVariable("comId") long comId) {
		for (ConcreteProduct prods : catalog.findAll()) {
			for (Comment c : prods.getUnacceptedComments()) {
				if (c.getId() == comId) {
					c.accept();
				}
			}
			this.catalog.save(prods);
		}
		return "redirect:/employee/comments";
	}

	@RequestMapping("/employee/deleteComment/{comId}")
	public String deleteComments(@PathVariable("comId") long comId) {
		boolean break_outer = false;
		for (ConcreteProduct prods : catalog.findAll()) {
			if (break_outer)
				break;
			for (Comment c : prods.getComments()) {
				if (c.getId() == comId) {
					prods.removeComment(c);
					break_outer = true;
					// prevents modification while interation
					break;
				}
			}
			this.catalog.save(prods);
		}

		return "redirect:/employee/comments";
	}

	@RequestMapping("/employee/comments")
	public String comments(ModelMap model) {
		List<Comment> comlist = new ArrayList<Comment>();
		for (ConcreteProduct prods : catalog.findAll()) {
			comlist.addAll(prods.getUnacceptedComments());
		}

		model.addAttribute("Comments", comlist);
		// model.addAttribute("concretProduct", com.getKey(com.values));

		return "changecatalogcomment";
	}

	@RequestMapping("/employee/changecatalog/editArticle/{prodId}")
	public String editArticle(@PathVariable("prodId") ConcreteProduct prod, Optional<UserAccount> userAccount, ModelMap model) {
		model.addAttribute("categories", prodSearch.getCagegories());
		model.addAttribute("concreteproduct", prod);
		model.addAttribute("price", prod.getPrice().getNumber());
		return "changecatalogchangeitem";
	}

	@RequestMapping(value = "/employee/changecatalog/editedArticle", method = RequestMethod.POST)
	public String editedArticle(@ModelAttribute("editArticleForm") @Valid EditArticleForm editForm, @RequestParam("image") MultipartFile img, BindingResult result) {
		if (result.hasErrors()) {
			return "redirect:/employee/changecatalog/editArticle/";
		}

		if (!img.isEmpty()) {
			try {
				byte[] bytes = img.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("filename"))); // TODO: generate filename
				stream.write(bytes);
				stream.close();
				System.out.println("success (yay) !!!"); // TODO: validation
			} catch (Exception e) {
				System.out.println("error (" + e.getMessage() + ") !!!");
			}
		} else {
			System.out.println("another error (file empty) !!!");
		}

		ConcreteProduct prodId = editForm.getProdId();
		prodId.addCategory(editForm.getCategory());
		prodId.setName(editForm.getName());
		prodId.setPrice(Money.of(editForm.getPrice(), "EUR"));
		prodId.setDescription(editForm.getDescription());

		if (!(img.getOriginalFilename().isEmpty())) {
			prodId.setImagefile(img.getOriginalFilename());
		}

		prodSearch.delete(prodId);
		catalog.save(prodId);

		List<ConcreteProduct> prods = new ArrayList<ConcreteProduct>();
		prods.add(prodId); // TODO: das hier ist offensichtlich.
		prodSearch.addProds(prods);

		return "redirect:/employee/changecatalog";
	}

	@RequestMapping(value = "/employee/changecatalog/addedArticle", method = RequestMethod.POST)
	public String addedArticle(@ModelAttribute("editArticleForm") @Valid EditArticleForm editForm, @RequestParam("image") MultipartFile img, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			return "redirect:/employee/changecatalog/addArticle/";
		}

		if (!img.isEmpty()) {
			try {
				byte[] bytes = img.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("filename"))); // TODO: generate filename
				stream.write(bytes);
				stream.close();
				System.out.println("success (yay) !!!"); // TODO: validation
			} catch (Exception e) {
				System.out.println("error (" + e.getMessage() + ") !!!");
			}
		} else {
			System.out.println("another error (file empty) !!!");
		}

		if (img.getOriginalFilename().isEmpty()) {

			JOptionPane.showMessageDialog(null, "Bildpfad fehlt!");

		}

		if (editForm.getName().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Geben Sie bitte einen Artikelnamen an!");
		}

		if (editForm.getDescription().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Die Artikelbeschreibung fehlt!");
		}

		ConcreteProduct prodId = new ConcreteProduct(editForm.getName(), Money.of(editForm.getPrice(), "EUR"), editForm.getCategory(), editForm.getDescription(), "", img.getOriginalFilename());

		catalog.save(prodId);

		List<ConcreteProduct> prods = new ArrayList<ConcreteProduct>();
		prods.add(prodId); // TODO: das hier ist offensichtlich.
		prodSearch.addProds(prods);
		
		InventoryItem inventoryItem = new InventoryItem(prodId, Quantity.of(0));
		inventory.save(inventoryItem);

		return "redirect:/employee/changecatalog";

	}

	@RequestMapping("/employee/changecatalog/deleteArticle/{prodId}")
	public String deleteArticle(@PathVariable("prodId") ConcreteProduct prod, Optional<UserAccount> userAccount, ModelMap model) {
		model.addAttribute("categories", prodSearch.getCagegories());
		model.addAttribute("concreteproduct", prod);
		model.addAttribute("price", prod.getPrice().getNumber());
		return "changecatalogdeleteitem";
	}

	@RequestMapping(value = "/employee/changecatalog/deletedArticle/{prodId}", method = RequestMethod.POST)
	public String deletedArticle(@PathVariable("prodId") ProductIdentifier prod) {
		//catalog.delete(catalog.findOne(prod).get());
		
		prodSearch.delete(catalog.findOne(prod).get());
		
		inventory.delete(inventory.findByProductIdentifier(prod).get());
		
		return "redirect:/employee/changecatalog";

	}

	@RequestMapping("/employee/changecatalog/orderArticle/{prodId}")
	public String orderArticle(@PathVariable("prodId") ConcreteProduct prod, Optional<UserAccount> userAccount, ModelMap model) {

		Optional<InventoryItem> item = inventory.findByProductIdentifier(prod.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);
		model.addAttribute("categories", prodSearch.getCagegories());
		model.addAttribute("concreteproduct", prod);
		model.addAttribute("quantity", quantity);
		model.addAttribute("price", prod.getPrice().getNumber());
		return "changecatalogorderitem";
	}

	@RequestMapping(value = "/employee/changecatalog/orderedArticle", method = RequestMethod.POST)
	public String orderedArticle(@ModelAttribute("StockForm") @Valid StockForm stockForm, BindingResult result, ModelMap model, @LoggedIn Optional<UserAccount> userAccount) {
		if (result.hasErrors()) {
			return "redirect:/employee/changecatalog";
		}
		ConcreteOrder order = new ConcreteOrder(userAccount.get());
		
		OrderLine orderLine = new OrderLine(catalog.findOne(stockForm.getProdId()).get(), Quantity.of(stockForm.getQuantity()));
		
		order.add(orderLine);
		
		order.setDateOrdered(LocalDateTime.now());
		
		orderManager.save(order);
		
		stock.orderArticle(stockForm.getProdId(), Quantity.of(stockForm.getQuantity()));
		return "redirect:/employee/changecatalog";
	}
	@RequestMapping(value = "/employee/changecatalog/decreasedArticle/{prodId}", method = RequestMethod.POST)
	public String decreasedArticle(@ModelAttribute("StockForm") @Valid StockForm stockForm, BindingResult result) {
		if(result.hasErrors())
		{
			return "redirect:/employee/changecatalog";
		}
		stock.removeArticle(stockForm.getProdId(), Quantity.of(stockForm.getQuantity()));
		return "redirect:/employee/changecatalog";
	}

	@RequestMapping("/employee/startpage/{totalCaroussel}/{totalSelection}")
	public String editStartPage(@PathVariable("totalCaroussel") int totalCaroussel, @PathVariable("totalSelection") int totalSelection, ModelMap model) {
		model.addAttribute("prod50", catalog.findAll());
		model.addAttribute("totCar", totalCaroussel);
		model.addAttribute("totSel", totalSelection);
		return "changestartpage";
	}

	@RequestMapping(value = "/employee/startpage/changedSetting", method = RequestMethod.POST)
	public String changeStartPageSetting(@RequestParam("totalCaroussel") int totalCaroussel, @RequestParam("totalSelection") int totalSelection) {
		return "redirect:/employee/startpage/" + totalCaroussel + '/' + totalSelection;
	}
	/*
	@RequestMapping(value = "/employee/startpage/changedstartpage", method = RequestMethod.POST)
	public String changeStartpage(@ModelAttribute ChangeStartPageForm changeStartPageForm) {
		List<ProductIdentifier> carousselProdsId = changeStartPageForm.getCarousselArticle();
		List<ProductIdentifier> selectionProdsId = changeStartPageForm.getSelectionArticle();
		int index = 0;
		for (ProductIdentifier prodId : carousselProdsId) {
			carousselList.set(index, catalog.findOne(prodId).get());
			index ++;
		}
		index = 0;
		for (ProductIdentifier prodId : selectionProdsId) {
			selectionList.set(index, catalog.findOne(prodId).get());
			index ++;
		}
		return "redirect:/";
	}*/
	
	@RequestMapping(value = "/employee/orders")
	public String orders(ModelMap model) {
		Collection<ConcreteOrder> ordersPaid = IteratorUtils.toList(orderManager.findBy(OrderStatus.PAID).iterator());
		Collection<ConcreteOrder> ordersCancelled = IteratorUtils.toList(orderManager.findBy(OrderStatus.CANCELLED).iterator());
		Collection<ConcreteOrder> ordersCompleted = IteratorUtils.toList(orderManager.findBy(OrderStatus.COMPLETED).iterator());
		
		model.addAttribute("ordersPaid", ordersPaid);
		model.addAttribute("ordersCancelled", ordersCancelled);
		model.addAttribute("ordersCompleted", ordersCompleted);
		return "orders";
	}
	
	@RequestMapping(value="/employee/orders/accept/{orderId}", method = RequestMethod.GET)
	public String acceptOrder(@PathVariable("orderId") OrderIdentifier orderId) {
		orderManager.completeOrder(orderManager.get(orderId).get());
		return "redirect:/employee/orders";
	}
	
	@RequestMapping(value="/employee/orders/cancel/{orderId}", method = RequestMethod.GET)
	public String cancelOrder(@PathVariable("orderId") OrderIdentifier orderId) {
		orderManager.cancelOrder(orderManager.get(orderId).get());
		return "redirect:/employee/orders";
	}
	
	@RequestMapping(value="/employee/orders/detail/{orderId}")
	public String detailOrder(@PathVariable("orderId") OrderIdentifier orderId, ModelMap model) {
		ConcreteOrder order = orderManager.get(orderId).get();
		Collection<ConcreteOrder> orderLines = IteratorUtils.toList(order.getOrderLines().iterator());
		model.addAttribute("order", order);
		model.addAttribute("orderLines", orderLines);
		return "orderdetail";
	}

	public Inventory<InventoryItem> getInventory() {
		return inventory;
	}

	public static Quantity getNone() {
		return NONE;
	}

}
