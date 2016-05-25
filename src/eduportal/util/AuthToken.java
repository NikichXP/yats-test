package eduportal.util;

import eduportal.model.AccessSettings;

public class AuthToken {
	
	private String sessionId;
	private long timeout;
	private String accessLevel;
	
	public String getSessionId() {
		return sessionId;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public void setTimeoutTimestamp(long timeoutTimestamp) {
		this.timeout = timeoutTimestamp;
	}
	public String getAccessLevel() {
		return accessLevel;
	}
	public AuthToken setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
		return this;
	}
	public void putAccessLevelInt(int al) {
		this.accessLevel = ( (al == AccessSettings.MODERATOR_LEVEL) ? "MODERATOR" : (al == AccessSettings.ADMIN_LEVEL) ? "ADMIN" : "USER");
	}
	
	
}
