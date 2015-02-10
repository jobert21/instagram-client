package ja.apps.instagram.model;

import java.io.Serializable;

public class UserCredential implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8228613713192163815L;
	private String token;

	public UserCredential() {

	}

	public UserCredential(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
