package eduportal.dao.entity;

import java.util.ArrayList;
import com.googlecode.objectify.*;
import com.googlecode.objectify.annotation.*;

import eduportal.util.UserUtils;

@Entity
public class UserEntity extends AbstractEntity {
	private static final long serialVersionUID = 1441712953568281477L;
	@Index
	private String pass;
	@Index
	private String phone;
	@Index
	private String mail;
	private Permission permission;
	private int accessLevel;
	@Index
	private long corpId;
	@Index
	private String name;
	@Index
	private String surname;
	@Index
	private Key<UserEntity> creator;
	private ArrayList<Long> ordersId;

	public void addOrder(Order ord) {
		long id = ord.getId();
		for (long l : ordersId) {
			if (l == id) {
				return;
			}
		}
		ordersId.add(ord.getId());
	}

	public boolean hasNull() {
		if (pass == null) {
			return true;
		} else if (phone == null) {
			return true;
		} else if (mail == null) {
			return true;
		} else if (name == null) {
			return true;
		} else if (surname == null) {
			return true;
		} else {
			return false;
		}
	}

	public UserEntity() {
		super();
		this.ordersId = new ArrayList<>();
		this.permission = new Permission();
	}

	public UserEntity(String pass, String name, String surname, String phone, String mail) {
		super();
		this.name = name;
		this.surname = surname;
		this.mail = mail;
		this.phone = phone;
		this.pass = UserUtils.encodePass(pass);
		this.permission = new Permission();
		this.ordersId = new ArrayList<>();
	}

	public String getPass() {
		return pass;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public void setPass(String pass) {
		if (pass == null) {
			return;
		}
		this.pass = UserUtils.encodePass(pass);
	}

	public UserEntity setAccessLevel(int accessGroup) {
		this.accessLevel = accessGroup;
		return this;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPhone() {
		return phone;
	}

	public String getMail() {
		return mail;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public ArrayList<Long> getOrdersId() {
		return this.ordersId;
	}

	public void setOrders(Order[] orders) {
		this.ordersId.ensureCapacity(orders.length);
		for (Order ord : orders) {
			this.ordersId.add(ord.getId());
		}
	}

	public long getCreator() {
		return creator.getId();
	}

	public UserEntity creatorEntity() {
		return Ref.create(creator).get();
	}

	public UserEntity setCreator(UserEntity creator) {
		this.creator = Ref.create(creator).getKey();
		this.permission.setCorporation(creator.getPermission().corporationEntity());
		this.corpId = creator.getPermission().corporationEntity().getId();
		return this;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public void setOrdersId(ArrayList<Long> ordersId) {
		this.ordersId = ordersId;
	}

	public long getCorpId() {
		return this.permission.getCorporation();
	}

	public void setCorpId(long corpId) {
		this.corpId = corpId;
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ordersId == null) ? 0 : ordersId.hashCode());
		result = prime * result + ((pass == null) ? 0 : pass.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
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
		if (!(obj instanceof UserEntity)) {
			return false;
		}
		UserEntity other = (UserEntity) obj;
		if (permission == null) {
			if (other.permission != null) {
				return false;
			}
		} else if (!permission.equals(other.permission)) {
			return false;
		}
		if (creator == null) {
			if (other.creator != null) {
				return false;
			}
		} else if (!creator.equals(other.creator)) {
			return false;
		}
		if (mail == null) {
			if (other.mail != null) {
				return false;
			}
		} else if (!mail.equals(other.mail)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (ordersId == null) {
			if (other.ordersId != null) {
				return false;
			}
		} else if (!ordersId.equals(other.ordersId)) {
			return false;
		}
		if (pass == null) {
			if (other.pass != null) {
				return false;
			}
		} else if (!pass.equals(other.pass)) {
			return false;
		}
		if (phone == null) {
			if (other.phone != null) {
				return false;
			}
		} else if (!phone.equals(other.phone)) {
			return false;
		}
		if (surname == null) {
			if (other.surname != null) {
				return false;
			}
		} else if (!surname.equals(other.surname)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UserEntity [pass=" + ((pass.length() == 128) ? "..." : pass)
				+ ", phone=" + phone + ", mail=" + mail + ", permission=" + permission + ", accessLevel=" + accessLevel
				+ ", corpId=" + corpId + ", name=" + name + ", surname=" + surname + ", creator=" + creator
				+ ", ordersId=" + ordersId + "]";
	}

	public void wipeSecData() {
		this.pass = null;
		this.ordersId = null;
		this.permission = null;
	}

}
