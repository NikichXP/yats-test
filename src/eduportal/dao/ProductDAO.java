package eduportal.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.util.*;
import eduportal.dao.entity.*;

public class ProductDAO {
	
	public static boolean create (Product p) {
		ofy().save().entity(p).now();
		return (ofy().load().type(Product.class).id(p.getId()).now() != null);
	}

	public static Product get(String productId) {
		return ofy().load().type(Product.class).id(productId).now();
	}
	
	public static Product get(Long id) {
		return ofy().load().type(Product.class).id(id).now();
	}
	
	public static List<Product> getAll() {
		return ofy().load().type(Product.class).list();
	}
	
	public static List<Product> getActual() {
		return ofy().load().type(Product.class).filter("actual == ", true).list();
	}
	
	public static List<Product> getAllByCounry(String countryid) {
		return ofy().load().type(Product.class).filter("countryid", countryid).list();
	}
	
	public static List<Product> getAllByCity(String cityid) {
		return ofy().load().type(Product.class).filter("cityid", cityid).list();
	}
	
	public static List<Product> getAllByCity(City city) {
		return ofy().load().type(Product.class).filter("cityid", city.getId()).list();
	}

	public static void save(Product... p) {
		ofy().save().entities(p).now();
		return;
	}

}
