package eduportal.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.*;
import javax.servlet.http.*;
import com.google.api.server.spi.config.*;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.Text;

import eduportal.dao.*;
import eduportal.dao.entity.*;
import eduportal.model.*;

@Api(name = "test", version = "v1")
public class TestAPI {

	@ApiMethod(path = "test", httpMethod = "GET")
	public ArrayList<Object> test(HttpServletRequest req) {
		ArrayList<Object> ret = new ArrayList<>();
		UserEntity u = ofy().load().type(UserEntity.class).first().now();
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String URL = blobstoreService.createUploadUrl("/FileProcessorServlet");
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
		Iterator<BlobInfo> blobsIterator = blobInfoFactory.queryBlobInfos();
	    while (blobsIterator.hasNext()) {
	        BlobInfo blobInfo = blobsIterator.next();
	        String[] adds = {"blob  " + blobInfo.getFilename(),
	        		"content-type  " + blobInfo.getContentType(),
	        		"key  " + blobInfo.getBlobKey().getKeyString()
	        		};
	        ret.add(adds);
	    }
		ret.add(URL);
		ret.add("end");
		return ret;
	}
	
	@ApiMethod(path = "ping2", httpMethod = "GET")
	public Text test2() {
		return new Text("alsd");
	}

	@ApiMethod(path = "cookies", httpMethod = "GET")
	public ArrayList<Object> testCookies(HttpServletRequest req) {
		ArrayList<Object> ret = new ArrayList<>();
		for (Cookie c : req.getCookies()) {
			ret.add(c.getName() + " " + c.getValue());
		}
		return ret;
	}

	@ApiMethod(name = "listSessions", path = "listsession", httpMethod = "GET")
	public List<String> listSession() {
		return AuthContainer.testMethod();
	}

	@ApiMethod(name = "ping", path = "ping", httpMethod = "GET")
	public Text ping() {
		return new Text("ping");
	}

	@ApiMethod(name = "Rebuild__DB", path = "rebuildDB", httpMethod = "GET")
	public List<String> rebuildDB(@Named("size") String size, @Named("usersize") @Nullable Integer usersize,
			@Named("ordersize") @Nullable Integer ordersize, @Named("shuffled") @Nullable Boolean shuffled, @Named ("prior") @Nullable Integer prior) {
		ofy().cache(true).flush();
		for (Class<?> clazz : UserAPI.objectifiedClasses) {
			for (Object u : ofy().load().kind(clazz.getSimpleName()).list()) {
				ofy().delete().entity(u).now();
			}
		}
		Corporation corp = new Corporation("Vedi Tour Group");
		corp.setOwnerCorp(true);
		UserEntity[] admins = {
				new UserEntity("pass", "Admin", "Adminov", "+123456789012", "admin@corp.com")
						.setAccessLevel(AccessSettings.ADMIN_LEVEL + 1),
				new UserEntity("order", "New", "Order", "+123456789015", "order@corp.com")
						.setAccessLevel(AccessSettings.MODERATOR_LEVEL),
				new UserEntity("adminus", "Adminus", "Maximus", "+123456789016", "adminus@corp.com")
						.setAccessLevel(AccessSettings.MODERATOR_LEVEL),
				new UserEntity("user", "User", "User", "+123456789013", "user@corp.com")
						.setAccessLevel(AccessSettings.MODERATOR_LEVEL),
				new UserEntity("johndoe", "John", "Doe", "+123456789014", "john@doe.com")
						.setAccessLevel(AccessSettings.MODERATOR_LEVEL) };
		corp.setOwner(admins[0]);
		ofy().save().entity(corp).now();
		for (UserEntity user : admins) {
			user.setPermission(new Permission());
			user.getPermission().setCorporation(corp);
		}
		for (UserEntity user : admins) {
			user.setCreator(admins[0]);
		}
		UserEntity[] clients;
		int dbsize;
		if (size == null) {
			size = "norm";
		}
		switch (size) {
		case "big":
			dbsize = 100;
			break;
		case "large":
			dbsize = 500;
			break;
		case "ultra":
			dbsize = 1_000;
			break;
		case "mega":
			dbsize = 10_000;
			break;
		case "fuck":
			dbsize = 512_000;
			break;
		default:
			dbsize = 20;
			break;
		}
		clients = new UserEntity[100];
		Order o[] = new Order[dbsize];
		if (usersize != null) {
			clients = new UserEntity[usersize];
		}
		if (ordersize != null) {
			o = new Order[ordersize];
		}
		String[] clientName = NameGen.genNames(clients.length * 2);
		for (int i = 0; i < clients.length; i++) {
			clients[i] = new UserEntity("pass" + i, clientName[2 * i], clientName[2 * i + 1],
					"+5555" + ((i < 10) ? "00" + i : (i > 9 && i < 100) ? "0" + i : i) + "12345",
					("user"+i + "@corp.com").toLowerCase())
							.setCreator(admins[i % 5]);
			clients[i].getPermission().setCorporation(corp);
		}
		if (prior != null) {
			if (prior < admins.length) {
				for (UserEntity u : clients) {
					if (Math.random() > 0.33) {
						u.setCreator(admins[prior]);
					}
				}
			}
		}
		ofy().save().entities(admins);
		ofy().save().entities(clients);
		City[] c = { GeoDAO.createCity("Kiev", "Ukraine"), GeoDAO.createCity("Lvov", "Ukraine"),
				GeoDAO.createCity("Prague", "Czech Republic"), GeoDAO.createCity("Budapest", "Hungary"),
				GeoDAO.createCity("London", "United Kingdom") };
		Product p[] = { new Product("Test product", "Some product to test", c[0]),
				new Product("NewOrderTest", "Some product to test new order", c[1]),
				new Product("Prague Study School", "Nice Prague school of english, .......", c[2]),
				new Product("Высшая школа Будапешта", "Описание программы", c[3]),
				new Product("Высшая школа Лондона", "Описание программы", c[4]),
				new Product("LSE", "Описание программы", c[4]), new Product("Ещё один ВУЗ", "Описание программы", c[2]),
				new Product("КПИ", "Как же без него?", c[0]), };
		for (Product prod : p) {
			prod.setActual(Math.random() > 0.5);
			prod.setDefaultPrice((double) Math.round(Math.random() * 100_000_00) / 100);
		}
		ProductDAO.save(p);
		Random r = new Random();
		for (int ptr = 0; ptr < o.length; ptr++) {
			o[ptr] = new Order(p[r.nextInt(p.length)]);
		}
		if (shuffled == null) {
			shuffled = false;
		}
		
		int i = 0;
		for (Order ord : o) {
			i++;
			if (shuffled) {
				ord.setUser(clients[r.nextInt(clients.length)]);
				ord.setCreatedBy(admins[i % admins.length]);
			} else {
				ord.setUser(clients[i % clients.length]);
				ord.setCreatedBy(admins[i % admins.length]);
				ord.setProduct(p[i % p.length]);
			}
			if (Math.random() > 0.5) {
				ord.setPaid(ord.getPrice());
			} else {
				ord.setPaid((double) Math.round(Math.random() * 10 * ord.getPrice()) / 100);
			}
			OrderDAO.saveOrder(ord);
		}
		return getAll();
	}

	@ApiMethod(path = "getAll", httpMethod = "GET")
	public List<String> getAll() {
		List<String> ret = new ArrayList<>();
		for (Class<?> clazz : UserAPI.objectifiedClasses) {
			for (Object o : ofy().load().kind(clazz.getSimpleName()).list()) {
				ret.add(o.toString());
			}
		}
		ret.addAll(AuthContainer.testMethod());
		return ret;
	}

	@ApiMethod(path = "getAllObj", httpMethod = "GET")
	public List<Object> getAllObj() {
		List<Object> ret = new ArrayList<>();
		for (Class<?> clazz : UserAPI.objectifiedClasses) {
			for (Object o : ofy().load().kind(clazz.getSimpleName()).list()) {
				ret.add(o);
			}
		}
		ret.addAll(AuthContainer.testMethod());
		return ret;
	}

	@ApiMethod(path = "getCountry", httpMethod = "GET")
	public List<Object> getCountry() {
		return ofy().load().kind("Country").list();

	}

	private static class NameGen {
		public static ArrayList<Character> glasn = new ArrayList<>();

		static {
			char[] characters = { 'a', 'i', 'u', 'e', 'o', 'y' };
			for (char char_ : characters) {
				glasn.add(char_);
			}
		}

		public static String[] genNames(int size) {
			String[] names = new String[size];
			StringBuilder sb;
			double melodical = 0.33;
			double k = melodical;
			double kvar = 1.33;
			Random r = new Random();
			char temp;
			boolean glasnoe;

			for (int n = 0; n < size; n++) {
				sb = new StringBuilder();
				for (int i = 0; i < (Math.random() * 4) + 3; i++) {
					glasnoe = (Math.random() > k);
					do {
						temp = (char) (r.nextInt(('Z' - 'A' + 1)) + 'a');
					} while (checkGlasn(glasnoe, temp));
					k = ((glasnoe) ? k * kvar : k / kvar);
					if (i == 0) {
						temp = Character.toUpperCase(temp);
					}
					sb.append(temp);
				}
				names[n] = sb.toString();
			}
			return names;
		}

		private static boolean checkGlasn(boolean cond, char temp) {
			if (glasn.contains(temp) == cond) {
				return true;
			}
			return false;
		}
	}
}
