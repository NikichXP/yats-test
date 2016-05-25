package eduportal.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.util.*;
import eduportal.dao.entity.*;

public class GeoDAO {

	public static City createCity (String name, Country c) {
		City city = new City();
		city.setName(name);
		city.setCountry(c);
		ofy().save().entity(city);
		return city;
	}
	
	public static City createCity (String name, String country) {
		City ct = ofy().load().type(City.class).filter("name == ", name).first().now();
		if (ct != null) {
			return ct;
		}
		City city = new City();
		Country c = getCountry(country);
		city.setName(name);
		city.setCountry(c);
		ofy().save().entity(city);
		return city;
	}
	
	public static City getCity(String cityname) {
		try {
			return ofy().load().type(City.class).filter("name", cityname).first().now();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static City getCityById(String id) {
		try {
			return ofy().load().type(City.class).id(id).now();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static List<Country> getCountryList (String filterExp) {
		return ofy().load().type(Country.class).filter("name >= ", filterExp).filter("name <=", filterExp+"\uFFFD").list();
	}
	
	public static Country getCountry(String country) {
		Country c = ofy().load().type(Country.class).filter("name", country).first().now();
		if (c == null) {
			Country ctr = new Country(country);
			ofy().save().entity(ctr);
			return ctr;
		}
		return c;
	}

	public static Country getCountryById(Long countryid) {
		return ofy().load().type(Country.class).id(countryid).now();
	}

}
