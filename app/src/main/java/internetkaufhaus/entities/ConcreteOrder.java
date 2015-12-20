package internetkaufhaus.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.collections.IteratorUtils;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccount;

@Entity
@Table(name = "CORDER")
public class ConcreteOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	// private OrderManager<Order> orderManager;
	private OrderStatus status;
	@OneToOne
	private UserAccount user;

	@Id
	@GeneratedValue
	private Long id;

	private String billingGender;
	private String billingFirstName;
	private String billingLastName;
	private String billingStreet;
	private String billingHouseNumber;
	private String billingAdressLine2;
	private String billingZipCode;
	private String billingTown;

	private String shippingGender;
	private String shippingFirstName;
	private String shippingLastName;
	private String shippingStreet;
	private String shippingHouseNumber;
	private String shippingAdressLine2;
	private String shippingZipCode;
	private String shippingTown;

	private LocalDateTime dateOrdered;

	@OneToOne
	private Order order;

	private boolean returned = false;
	private String returnReason;

	@SuppressWarnings({ "unused" })
	private ConcreteOrder() {

	}

	public ConcreteOrder(UserAccount account, Cash cash) {
		this.order = new Order(account, cash);
		this.status = this.order.getOrderStatus();
		this.user = account;

	}

	public ConcreteOrder(String billingGender, String billingFirstName, String billingLastName, String billingStreet, String billingHouseNumber, String billingAdressLine2, String billingZipCode, String billingTown, String shippingGender, String shippingFirstName, String shippingLastName, String shippingStreet, String shippingHouseNumber, String shippingAdressLine2, String shippingZipCode, String shippingTown, LocalDateTime dateOrdered, Order order) {
		this.billingGender = billingGender;
		this.billingFirstName = billingFirstName;
		this.billingLastName = billingLastName;
		this.billingStreet = billingStreet;
		this.billingHouseNumber = billingHouseNumber;
		this.billingAdressLine2 = billingAdressLine2;
		this.billingZipCode = billingZipCode;
		this.billingTown = billingTown;

		this.shippingGender = shippingGender;
		this.shippingFirstName = shippingFirstName;
		this.shippingLastName = shippingLastName;
		this.shippingStreet = shippingStreet;
		this.shippingHouseNumber = shippingHouseNumber;
		this.shippingAdressLine2 = shippingAdressLine2;
		this.shippingZipCode = shippingZipCode;
		this.shippingTown = shippingTown;

		this.dateOrdered = dateOrdered;
		this.order = order;
		this.status = order.getOrderStatus();

	}

	public String getBillingGender() {
		return billingGender;
	}

	public Order getOrder() {
		return this.order;
	}

	public String getBillingFirstName() {
		return billingFirstName;
	}

	public String getBillingLastName() {
		return billingLastName;
	}

	public String getBillingStreet() {
		return billingStreet;
	}

	public String getBillingHouseNumber() {
		return billingHouseNumber;
	}

	public String getBillingAdressLine2() {
		return billingAdressLine2;
	}

	public String getBillingZipCode() {
		return billingZipCode;
	}

	public String getBillingTown() {
		return billingTown;
	}

	public String getShippingGender() {
		return shippingGender;
	}

	public String getShippingFirstName() {
		return shippingFirstName;
	}

	public String getShippingLastName() {
		return shippingLastName;
	}

	public String getShippingStreet() {
		return shippingStreet;
	}

	public String getShippingHouseNumber() {
		return shippingHouseNumber;
	}

	public String getShippingAdressLine2() {
		return shippingAdressLine2;
	}

	public String getShippingZipCode() {
		return shippingZipCode;
	}

	public String getShippingTown() {
		return shippingTown;
	}

	public void setDateOrdered(LocalDateTime dateOrdered) {
		this.dateOrdered = dateOrdered;
	}

	public int getOrderLinesSize() {
		Collection<OrderLine> orderLines = IteratorUtils.toList(this.order.getOrderLines().iterator());
		return orderLines.size();
	}

	public int getTotalProductNumber() {
		int total = 0;
		Iterable<OrderLine> orderLines = this.order.getOrderLines();
		for (OrderLine orderLine : orderLines) {
			total += Integer.parseInt(orderLine.getQuantity().toString());
		}
		return total;
	}

	public LocalDateTime getDateOrdered() {
		return this.dateOrdered;
	}

	public boolean getReturned() {
		return this.returned;
	}

	public void setBillingGender(String billingGender) {
		this.billingGender = billingGender;
	}

	public void setBillingFirstName(String billingFirstName) {
		this.billingFirstName = billingFirstName;
	}

	public void setBillingLastName(String billingLastName) {
		this.billingLastName = billingLastName;
	}

	public void setBillingStreet(String billingStreet) {
		this.billingStreet = billingStreet;
	}

	public void setBillingHouseNumber(String billingHouseNumber) {
		this.billingHouseNumber = billingHouseNumber;
	}

	public void setBillingAdressLine2(String billingAdressLine2) {
		this.billingAdressLine2 = billingAdressLine2;
	}

	public void setBillingZipCode(String billingZipCode) {
		this.billingZipCode = billingZipCode;
	}

	public void setBillingTown(String billingTown) {
		this.billingTown = billingTown;
	}

	public void setShippingGender(String shippingGender) {
		this.shippingGender = shippingGender;
	}

	public void setShippingFirstName(String shippingFirstName) {
		this.shippingFirstName = shippingFirstName;
	}

	public void setShippingLastName(String shippingLastName) {
		this.shippingLastName = shippingLastName;
	}

	public void setShippingStreet(String shippingStreet) {
		this.shippingStreet = shippingStreet;
	}

	public void setShippingHouseNumber(String shippingHouseNumber) {
		this.shippingHouseNumber = shippingHouseNumber;
	}

	public void setShippingAdressLine2(String shippingAdressLine2) {
		this.shippingAdressLine2 = shippingAdressLine2;
	}

	public void setShippingZipCode(String shippingZipCode) {
		this.shippingZipCode = shippingZipCode;
	}

	public void setShippingTown(String shippingTown) {
		this.shippingTown = shippingTown;
	}

	public void setUserAccount(UserAccount account) {
		this.setUserAccount(account);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setBillingAdress(List<String> billingAdress) {
		this.billingGender = billingAdress.get(0);
		this.billingFirstName = billingAdress.get(1);
		this.billingLastName = billingAdress.get(2);
		this.billingStreet = billingAdress.get(3);
		this.billingHouseNumber = billingAdress.get(4);
		this.billingAdressLine2 = billingAdress.get(5);
		this.billingZipCode = billingAdress.get(6);
		this.billingTown = billingAdress.get(7);
	}

	public void setShippingAdress(List<String> shippingAdress) {
		this.shippingGender = shippingAdress.get(0);
		this.shippingFirstName = shippingAdress.get(1);
		this.shippingLastName = shippingAdress.get(2);
		this.shippingStreet = shippingAdress.get(3);
		this.shippingHouseNumber = shippingAdress.get(4);
		this.shippingAdressLine2 = shippingAdress.get(5);
		this.shippingZipCode = shippingAdress.get(6);
		this.shippingTown = shippingAdress.get(7);
	}

	public void setReturned(boolean returned) {
		this.returned = returned;
	}

	public UserAccount getUser() {
		return user;
	}

	/*
	 * public void setOrderManager(OrderManager<Order> orderManager){ this.orderManager = orderManager; } public OrderManager<Order> getOrderManager(){ return this.orderManager; }
	 */
	public OrderStatus getStatus() {
		return this.status;
	}

	public void setStatus(OrderStatus state) {
		this.status = state;
	}

	public String getReturnReason() {
		return returnReason;
	}

	public void setReturnReason(String returnReason) {
		this.returnReason = returnReason;
	}
}