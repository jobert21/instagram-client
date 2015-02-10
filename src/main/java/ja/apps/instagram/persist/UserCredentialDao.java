package ja.apps.instagram.persist;

import ja.apps.instagram.commons.JdbmUtils;
import ja.apps.instagram.model.UserCredential;
import jdbm.PrimaryTreeMap;

public class UserCredentialDao {
	private static final UserCredentialDao instance = new UserCredentialDao();
	private final String SINGLE_STORE = "single_user";
	private PrimaryTreeMap<String, UserCredential> tree;

	private UserCredentialDao() {
		tree = JdbmUtils.getInstance().create("credentials");
	}

	public static UserCredentialDao getInstance() {
		return instance;
	}

	public void saveCredential(UserCredential cred) {
		tree.put(SINGLE_STORE, cred);
	}

	public UserCredential getCredential() {
		return tree.get(SINGLE_STORE);
	}
}
