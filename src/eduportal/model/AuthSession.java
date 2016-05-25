package eduportal.model;

import java.io.Serializable;

import com.googlecode.objectify.*;
import com.googlecode.objectify.annotation.*;
import eduportal.dao.entity.UserEntity;

@Entity
public class AuthSession implements Serializable {
	private static final long serialVersionUID = -3213250756514680668L;
	@Id
	private String token;
	private Ref<UserEntity> user;
	@Index
	private long timeout;
	private int accessLevel;
	
	@Deprecated
	public AuthSession() {
	}

	public AuthSession(UserEntity user) {
		this.user = Ref.create(user);
		this.accessLevel = user.getAccessLevel();
		this.timeout = System.currentTimeMillis() + ((user.getAccessLevel() >= AccessSettings.MODERATOR_LEVEL)
				? AccessSettings.WORKER_SESSION_TIMEOUT : AccessSettings.USER_SESSION_TIMEOUT);
	}
	
	public AuthSession(UserEntity user, String token) {
		this.user = Ref.create(user);
		this.accessLevel = user.getAccessLevel();
		this.timeout = System.currentTimeMillis() + ((user.getAccessLevel() >= AccessSettings.MODERATOR_LEVEL)
				? AccessSettings.WORKER_SESSION_TIMEOUT : AccessSettings.USER_SESSION_TIMEOUT);
		this.token = token;
	}

	public AuthSession(UserEntity usr, long timeout2, int acclvl) {
		this.user = Ref.create(usr);
		this.timeout = timeout2;
		this.accessLevel = acclvl;
	}

	public UserEntity user() {
		return user.get();
	}

	public long getTimeout() {
		return timeout;
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	public void setUser(Ref<UserEntity> user) {
		this.user = user;
	}
	
	public void defineUser(UserEntity user) {
		this.user = Ref.create(user);
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	public String getToken() {
		return token;
	}

	public AuthSession setToken(String token) {
		this.token = token;
		return this;
	}

	@Override
	public String toString() {
		return "AuthSession [token=" + token + ", user=" + user + ", timeout=" + timeout + ", accessLevel="
				+ accessLevel + "]";
	}
	
}