package eduportal.dao.entity;

import com.googlecode.objectify.*;
import com.googlecode.objectify.annotation.*;

@Entity
public class City extends AbstractEntity {
	
	private Key<Country> country;
	@Index
	private String name;
	protected final int maxIdValue = 999_999;

	public Country getCountry() {
		return Ref.create(country).get();
	}

	public void setCountry(Country country) {
		this.country = Ref.create(country).getKey();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		City other = (City) obj;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", country=" + country + ", name=" + name + "]";
	}
	
	
}
