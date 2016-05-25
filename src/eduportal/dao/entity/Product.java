package eduportal.dao.entity;

import com.googlecode.objectify.*;
import com.googlecode.objectify.annotation.*;

@Entity
public class Product extends AbstractEntity {
	
	@Index
	private String title;
	private String description;
	@Index
	private Key<City> city;
	@Index
	private boolean actual;
	@Index
	private double defaultPrice;
	
	public Product () {
		
	}
	
	public Product (String name, String descr, City c) {
		super();
		this.title = name;
		this.description = descr;
		this.city = Ref.create(c).getKey();
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public City getCity() {
		return Ref.create(city).get();
	}
	public void setCity(City city) {
		this.city = Ref.create(city).getKey();
	}
	public boolean isActual() {
		return actual;
	}
	public void setActual(boolean actual) {
		this.actual = actual;
	}
	
	public double getDefaultPrice() {
		return defaultPrice;
	}

	public Product setDefaultPrice(double defaultPrice) {
		this.defaultPrice = defaultPrice;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (actual ? 1231 : 1237);
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (actual != other.actual)
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", title=" + title + ", description=" + description + ", city=" + city
				+ ", actual=" + actual + ", defaultPrice=" + defaultPrice + "]";
	}
	
}
