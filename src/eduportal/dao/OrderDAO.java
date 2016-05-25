package eduportal.dao;

import java.util.*;

import com.googlecode.objectify.*;

import static com.googlecode.objectify.ObjectifyService.ofy;
import eduportal.dao.entity.*;

public class OrderDAO {
	
	/** Generic method */
	public static List<Order> getOrdersByUser (UserEntity u) {
		List<Order> ret = new ArrayList<>();
		Key<UserEntity> key = Ref.create(u).key();
		ret = ofy().load().type(Order.class).filter("user", key).list();
		ret.addAll(ofy().load().type(Order.class).filter("createdBy", key).list());
		return ret;
	}
	
	public static List<Order> getCreatedOrdersByUser (UserEntity u) {
		Key<UserEntity> key = Ref.create(u).key();
		return ofy().load().type(Order.class).filter("createdBy", key).list();
	}
	
	public static List<Order> getSelfOrdersByUser (UserEntity u) {
		Key<UserEntity> key = Ref.create(u).key();
		return ofy().load().type(Order.class).filter("user", key).list();
	}
	
	public static Order getOrder (String id) {
		id = id.trim();
		return ofy().load().type(Order.class).id(Long.parseLong(id)).now();
	}

	public static List<Order> getAllOrders() {
		return ofy().load().type(Order.class).list();
	}

	public static List<Product> getAllProducts(boolean isActual) {
		return ofy().load().type(Product.class).filter("actual", isActual).list();
	}
	
	public static void saveOrder (Order o) {
		if (o.getStart() == null) {
			o.setStart(new Date());
		}
		if (o.getEnd() == null) {
			o.setEnd(new Date (System.currentTimeMillis() + ( 1_000 * 3600)));
		}
		ofy().save().entity(o); 
	}
	
	public static void saveProduct (Product p) {
		ofy().save().entity(p).now();
	}
	
	public static boolean saveProduct (String title, String description, City c, double defaultPrice) {
		Product p = new Product(title, description, c).setDefaultPrice(defaultPrice);
		ofy().save().entity(p).now();
		return true;
	}

	public static Product getProduct(long product) {
		return ofy().load().type(Product.class).id(product).now();
	}

	public static Order getOrder(Long id) {
		return ofy().load().type(Order.class).id(id).now();
	}

	public static void deleteOrder (Order order) {
		ofy().delete().entity(order);
	}
}
