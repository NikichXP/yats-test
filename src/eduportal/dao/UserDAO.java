package eduportal.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.util.*;
import com.googlecode.objectify.*;
import com.googlecode.objectify.cmd.Query;
import eduportal.dao.entity.*;
import eduportal.util.UserUtils;

public class UserDAO {

	private static String[] credentialVariables = { "mail", "phone" };
	
	public static UserEntity create(String pass, String name, String surname, String mail, String phone, UserEntity creator) {
		if (ofy().load().kind("UserEntity").filter("mail == ", mail).list().isEmpty() == false) {
			return null;
		}
		if (ofy().load().kind("UserEntity").filter("phone == ", phone).list().isEmpty() == false) {
			return null;
		}
		UserEntity u = new UserEntity();
		u.setPass(pass);
		u.setName(name);
		u.setMail(mail);
		u.setPhone(phone);
		u.setSurname(surname);
		u.setPermission(new Permission());
		u.getPermission().setCorporation(creator.getPermission().corporationEntity());
		u.setCreator(creator);
		ofy().save().entity(u).now();
		return u;
	}
	
	
	public static List<Corporation> getCorpList (UserEntity user) {
		return ofy().load().type(Corporation.class).list();
	}

	/**
	 * Performs search through users DB
	 * 
	 * @param creds
	 * @return
	 */
	public static List<UserEntity> searchUsers(String creds) {
		Query<UserEntity> q = ofy().load().kind("UserEntity");
		ArrayList<Query<UserEntity>> qn = new ArrayList<>();
		qn.ensureCapacity(credentialVariables.length);
		if (creds == null) {
			return null;
		}
		List<UserEntity> ret = new ArrayList<>();
		for (String filter : credentialVariables) {
			qn.add(q.filter(filter + " > ", creds).filter(filter + " < ", creds + "\uFFFD"));
		}
		if (creds.contains(" ")) {
			for (String key : creds.split(" ")) {
				for (UserEntity elem : q.iterable()) {
					if (elem.getName().contains(key) || elem.getSurname().contains(key)) {
						ret.add(elem);
					}
				}
			}
		} else {
			Query<UserEntity> q1 = q.filter("name >=", creds).filter("name <= ", creds + "\uFFFD"),
					q2 = q.filter("surname >=", creds).filter("surname <= ", creds + "\uFFFD");
			ret = q1.list();
			ret.addAll(q2.list());
		}
		for (Query<UserEntity> query : qn) {
			try {
				ret.addAll(query.list());
			} catch (Exception e) {
				System.out.println("Here is null thrown " + e.toString());
			}
		}
		return ret;
	}

	public static UserEntity get(String login, String pass) {
		if (pass.length() != UserUtils.CRYPTOLENGTH) {
			pass = UserUtils.encodePass(pass);
		}
		UserEntity u = null;
		for (String par : credentialVariables) {
			u = (UserEntity) ofy().load().kind("UserEntity").filter(par, login).filter("pass == ", pass).first().now();
			if (u != null) {
				return u;
			}
		}
		return null;
	}
	
	public static void update(UserEntity u) {
		ofy().save().entity(u).now();
	}

	public static UserEntity get(long parseLong) {
		return (UserEntity) ofy().load().kind("UserEntity").id(parseLong).now();
	}

	public static UserEntity get(String parseLong) {
		return (UserEntity) ofy().load().kind("UserEntity").id(parseLong).now();
	}

	public static void delete(String target) {
		UserEntity u = (UserEntity) ofy().load().kind("UserEntity").id(target).now();
		DeletedUser du = new DeletedUser(u);
		ofy().delete().entity(u).now();
		ofy().save().entity(du);
	}

	public static UserEntity create(UserEntity user) {
		ofy().save().entity(user).now();
		return user;
	}

	public static List<UserEntity> getClients(UserEntity u) {
		Key<UserEntity> key = Ref.create(u).getKey();
		return ofy().load().type(UserEntity.class).filter("creator", key).list();
	}
	
	public static List<UserEntity> searchUsers(String phone, String name, String mail, Corporation corp) {
		System.out.println(corp.getId());
		return userFilter (ofy().load().type(UserEntity.class).filter("corpId", corp.getId()), phone, name, mail) ;
	}
	
	public static List<UserEntity> searchUsers(String phone, String name, String mail, long corp) {
		System.out.println(corp);
		return userFilter (ofy().load().type(UserEntity.class).filter("corpId", corp), phone, name, mail) ;
	}

	public static List<UserEntity> searchUsers(String phone, String name, String mail) {
		return userFilter (ofy().load().type(UserEntity.class), phone, name, mail) ;
	}
	
	private static List<UserEntity> userFilter (Query<UserEntity> q, String phone, String name, String mail) {
		List<UserEntity> ret = new ArrayList<>();
		if (phone != null) {
			q = q.filter("phone >=", phone).filter("phone <= ", phone + "\uFFFD");
		}
		if (mail != null) {
			q = q.filter("mail >=", mail).filter("mail <= ", mail + "\uFFFD");
		}
		if (name != null) {
			if (name.split(" ").length > 1) {
				for (String key : name.split(" ")) {
					for (UserEntity elem : q.iterable()) {
						if (elem.getName().contains(key) || elem.getSurname().contains(key)) {
							ret.add(elem);
						}
					}
				}
			} else {
				Query<UserEntity> q1 = q.filter("name >=", name).filter("name <= ", name + "\uFFFD"),
						q2 = q.filter("surname >=", name).filter("surname <= ", name + "\uFFFD");
				ret = q1.list();
				ret.addAll(q2.list());
			}
		}
		if (ret.isEmpty()) {
			ret = q.list();
		}
		return ret;
	}
	
	public static void createCorp (Corporation... corp) {
		ofy().save().entities(corp);
	}
	
	public static Corporation getCorp (long id) {
		return ofy().load().type(Corporation.class).id(id).now();
	}
	
	public static Corporation getCorp (String name) {
		return ofy().load().type(Corporation.class).filter("name", name).first().now();
	}
	
	public static Corporation getOwnerCorp() {
		return ofy().load().type(Corporation.class).filter("isOwnerCorp", true).first().now();
	}
}
