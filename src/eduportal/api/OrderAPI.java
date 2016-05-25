package eduportal.api;

import java.util.*;
import com.google.api.server.spi.config.*;
import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.cmd.*;
import static com.googlecode.objectify.ObjectifyService.ofy;
import eduportal.dao.*;
import eduportal.dao.entity.*;
import eduportal.model.*;

@Api(name = "order", version = "v1", title = "Order/Product API")
public class OrderAPI {

	@ApiMethod(name = "getProducts", path = "products", httpMethod = "GET")
	public List<Product> getActualProducts(@Named("token") String token) {
		if (AccessLogic.canSeeProducts(token)) {
			return ProductDAO.getActual();
		}
		return null;
	}

	@ApiMethod(name = "getAllProducts", path = "allProducts", httpMethod = "GET")
	public List<Product> getAllProducts(@Named("token") String token) {
		if (AccessLogic.canSeeAllProducts(token)) {
			return ProductDAO.getAll();
		}
		return null;
	}

	@ApiMethod(name = "setActivity", httpMethod = "GET", path = "productActivation")
	public Text setUnActualProduct(@Named("id") Long id, @Named("token") String token,
			@Named("actual") Boolean actual) {
		if (AccessLogic.canActivateProduct(token)) {
			try {
				Product p = ProductDAO.get(id);
				p.setActual(actual);
				ProductDAO.save(p);
				return new Text("Done");
			} catch (Exception e) {
				return new Text("No id sent");
			}
		} else {
			return new Text("No permission");
		}
	}

	@ApiMethod(path = "createorder", httpMethod = "GET")
	public Text createOrder(@Named("productid") Long productid, @Named("clientid") Long clientid,
			@Named("token") String token, @Named("paid") @Nullable Double paid) { // Token to identify creator
		UserEntity admin = AuthContainer.getUser(token);
		if (admin == null) {
			return new Text ("Wrong token");
		}
		Order o = new Order(ProductDAO.get(productid));
		if (paid != null) {
			o.setPaid(paid);
		}
		o.setCreatedBy(admin);
		o.setUser(UserDAO.get(clientid));
		OrderDAO.saveOrder(o);
		return new Text("Done");
	}

	@ApiMethod(name = "editOrder", path = "editorder", httpMethod = "GET")
	public Text editOrder(@Named("orderid") Long orderid, @Named("token") String token,
			@Named("paid") @Nullable Double paid, @Named ("comment") @Nullable String comment) {
		UserEntity admin = AuthContainer.getUser(token);
		Order order = OrderDAO.getOrder(orderid);
		if (AccessLogic.canEditOrder(admin, order) == false) {
			return new Text("You cannot edit this order!");
		}
		boolean flag = false;
		if (paid != null) {
			order.setPaid(order.getPaid()+paid);
			flag = true;
		}
		if (comment != null) {
			order.setComment(comment);
			flag = true;
		}
		if (flag) {
			OrderDAO.saveOrder(order);
		}
		return new Text(order.toString());
	}
	
	@ApiMethod (path = "cancelOrder", httpMethod = "GET")
	public Text cancelOrder (@Named("token") String token, @Named ("orderid") Long orderid) {
		Order order = OrderDAO.getOrder(orderid);
		if (!AccessLogic.canCancelOrder(token, order)) {
			return new Text ("403 Forbidden");
		}
		OrderDAO.deleteOrder(order);
		return new Text ("Order cancelled");
	}

	/**
	 * @return Orders associated with user
	 */
	@ApiMethod(name = "getAllOrders", path = "allOrders", httpMethod = "GET")
	public List<Order> getAllOrders(@Named("token") String token) {
		UserEntity u = AuthContainer.getUser(token);
		return ((u == null) ? null : (u.getAccessLevel() >= AccessSettings.MODERATOR_LEVEL) ? OrderDAO.getCreatedOrdersByUser(u) : OrderDAO.getSelfOrdersByUser(u));
	}
	
	@ApiMethod(name = "getEveryOrders", path = "everyOrders", httpMethod = "GET")
	public List<Order> getEveryOrders(@Named("token") String token) {
		UserEntity u = AuthContainer.getUser(token);
		return ((u == null) ? null : OrderDAO.getOrdersByUser(u));
	}

	@ApiMethod(name = "getCreatedOrders", path = "createdOrders", httpMethod = "GET")
	public List<Order> getCreatedOrders(@Named("token") String token) {
		UserEntity u = AuthContainer.getUser(token);
		return ((u == null) ? null : OrderDAO.getCreatedOrdersByUser(u));
	}

	@ApiMethod(name = "getMyOrders", path = "myOrders", httpMethod = "GET")
	public List<Order> getMyOrders(@Named("token") String token) {
		UserEntity u = AuthContainer.getUser(token);
		return ((u == null) ? null : OrderDAO.getSelfOrdersByUser(u));
	}

	@ApiMethod(name = "filter_through_all_orders", path = "filter", httpMethod = "GET")
	public List<Order> filterOrders( // TODO Move to DAO
			@Named("client_name") String clientName, @Named("client_id") String clientId,
			@Named("is_paid") Boolean isPaid, @Named("created_by") String createdBy, 
			@Named("token") String token) {
		if (!AccessLogic.canSeeAllOrders(token)) {
			return null;
		}
		Query<Order> q = ofy().load().kind("Order");
		if (clientName != null) {
			q = q.filter("clientName = ", clientName);
		}
		if (clientId != null) {
			q = q.filter("userid = ", clientId);
		}
		if (isPaid != null) {
			q = q.filter("donePaid", isPaid);
		}
		List<Order> list = q.list();
		return list;
	}

	@ApiMethod(name = "createCity", httpMethod = "GET", path = "create/city")
	public Text addCity(@Named("city") String cityname, @Named("country") String country, @Named("token") String token) { 
		if (!AccessLogic.canCreateCity(token)) {
			return new Text("403 Forbidden");
		}
		if (GeoDAO.getCity(cityname) != null) {
			return new Text(GeoDAO.getCity(cityname).toString() + " already exists");
		}
		Country ctr = GeoDAO.getCountry(country);
		City c = GeoDAO.createCity(cityname, ctr);
		if (c != null) {
			return new Text("City added");
		} else {
			return new Text("City not added");
		}
	}

	@ApiMethod(name = "addProduct", httpMethod = "GET", path = "product/add")
	public Text addProduct(@Named("title") String title, @Named("description") String descr,
			@Named("cityid") String cityname, @Named ("token") String token) {
		if (!AccessLogic.canAddProduct(token)) {
			return new Text("403 Forbidden");
		}
		City city = GeoDAO.getCityById(cityname);
		if (city == null) {
			city = GeoDAO.getCity(cityname);
			if (city == null) {
				return new Text("No such city exist");
			}
		}
		Product p = new Product(title, descr, city);
		ProductDAO.save(p);
		return new Text(p.toString());
	}

}
