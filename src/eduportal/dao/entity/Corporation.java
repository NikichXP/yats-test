package eduportal.dao.entity;

import com.googlecode.objectify.*;
import com.googlecode.objectify.annotation.*;

import eduportal.model.AccessSettings;

@Entity
public class Corporation extends AbstractEntity {
	
	private String name;
	private Ref<UserEntity> owner;
	@Index
	private boolean isOwnerCorp;
	
	public Corporation () {
		super();
	}
	
	public Corporation (String s) {
		super();
		this.name = s;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserEntity getOwner() {
		return owner.get();
	}

	public void setOwner(UserEntity owner) {
		this.owner = Ref.create(owner);
	}

	public boolean isOwnerCorp() {
		return isOwnerCorp;
	}

	public void setOwnerCorp(boolean isOwnerCorp) {
		this.isOwnerCorp = AccessSettings.OWNERCORP_NAME.equals(this.name);
	}

	@Override
	public String toString() {
		return "Corporation [name=" + name + ", owner=" + owner + ", id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		if (!(obj instanceof Corporation)) {
			return false;
		}
		Corporation other = (Corporation) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
			return false;
		}
		return true;
	}	
	
}
