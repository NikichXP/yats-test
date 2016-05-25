package eduportal.model;

import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Level;
import eduportal.dao.UserDAO;
import eduportal.dao.entity.*;
import eduportal.util.AuthToken;
import com.google.appengine.api.memcache.*;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class AuthContainer {

	private static HashMap<String, AuthSession> sessions = new HashMap<>();
	private static AsyncMemcacheService cache;
	
	static {
		cache = MemcacheServiceFactory.getAsyncMemcacheService();
		cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	}
	
	private static Set<String> keySet() {
		Set<String> ret = sessions.keySet();
		return ret;
	}
	
	private static AuthSession get(String key) {
		if (sessions.get(key) != null) {
			return sessions.get(key);
		}
		try {
			Future<Object> futureValue = cache.get(key);
			AuthSession session = (AuthSession) futureValue.get();
			if (session != null) {
				return session;
			}
		} catch (Exception e) {
			return null;
		}
		AuthSession ret = ofy().load().type(AuthSession.class).id(key).now();
		if (ret.getTimeout() > System.currentTimeMillis()) {
			List<AuthSession> clearList = ofy().load().type(AuthSession.class).filter("timeout > ", System.currentTimeMillis()).list();
			ofy().delete().entities(clearList);
			ret = null;
		}
		if (ret != null) {
			sessions.put(key, ret); //back-impl
		}
		return ret;
	}

	private static void put(String key, AuthSession value) {
		sessions.put(key, value);
		cache.put(key, value, Expiration.byDeltaSeconds(3600*24)); //1 day
		ofy().save().entity(value.setToken(key));
	}

	private static void remove(String token) {
		try {
			sessions.remove(token);
		} catch (Exception e) {
		}
		cache.delete(token);
		ofy().delete().type(AuthSession.class).id(token);
	}

	public static AuthToken authenticate(String login, String pass) {
		synchronized (sessions) {
			UserEntity user = UserDAO.get(login, pass);
			if (user == null) {
				return new AuthToken().setAccessLevel("FAIL");
			}
			String token = UUID.randomUUID().toString();
			AuthSession session = new AuthSession(user);
			put(token, session);
			AuthToken ret = new AuthToken();
			ret.setSessionId(token);
			ret.setTimeoutTimestamp(session.getTimeout());
			ret.putAccessLevelInt(user.getAccessLevel());
			return ret;
		}
	}

	public static boolean checkReq(String token, int acclvl) {
		synchronized (sessions) {
			if (get(token).getAccessLevel() >= acclvl) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Return user if token is valid & session not over
	 * 
	 * @param token
	 *            - session id
	 * @return - User
	 */
	public static UserEntity getUser(String token) {
		synchronized (sessions) {
			if (token == null) {
				return null;
			}
			UserEntity u = null;
			try {
				u = get(token).user();
			} catch (Exception e) {
				return null;
			}
			if (u != null) {
				if (get(token).getTimeout() > System.currentTimeMillis()) {
					return u;
				} else {
					remove(token);
				}
			}
			return null;
		}
	}

	public static int getAccessGroup(String token) {
		synchronized (sessions) {
			return get(token).getAccessLevel();
		}
	}

	/**
	 * Reset session timeout
	 * 
	 * @param token
	 *            - sessionID to update
	 */
	public static void updateTimeout(String token) {
		synchronized (sessions) {
			if (get(token).getTimeout() < System.currentTimeMillis()) {
				get(token).setTimeout(get(token).getTimeout() + 3600_000);
			}
		}
	}

	public static boolean checkToken(String token) {
		synchronized (sessions) {
			if (token == null) {
				return false;
			}
			if (get(token) == null) {
				return false;
			}
			if (get(token).getTimeout() <= System.currentTimeMillis()) {
				return false;
			}
			return true;
		}
	}

	// CUT BELOW
	public static ArrayList<String> testMethod() {
		synchronized (sessions) {
			ArrayList<String> ret = new ArrayList<>();
			for (String s : keySet()) {
				ret.add("Session: " + s + "  ==  " + sessions.get(s).user());
				ret.add("Token_t: " + s + "  ==  "
						+ (((double) sessions.get(s).getTimeout() - System.currentTimeMillis()) / (1000 * 3600)));
				ret.add("Access : " + s + "  ==  " + sessions.get(s).user().getAccessLevel());
			}
			return ret;
		}
	}
}
