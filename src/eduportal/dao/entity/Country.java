package eduportal.dao.entity;

import com.googlecode.objectify.annotation.*;

@Entity
public class Country extends AbstractEntity {
	
	@Index
	private String name;
	protected final int maxIdValue = 99_999;
	
	public Country () {
		super();
	}
	public Country (String title) {
		super();
		this.name = title;
	}	
	
	public String getName() {
		return name;
	}
	public void setName(String title) {
		this.name = title;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Country)) {
			return false;
		}
		Country other = (Country) obj;
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "Country [id=" + id + ", name=" + name + "]";
	}
}
