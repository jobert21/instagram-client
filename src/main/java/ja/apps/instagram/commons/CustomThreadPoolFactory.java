package ja.apps.instagram.commons;

import java.util.concurrent.ThreadFactory;

public class CustomThreadPoolFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName(Constants.APP_NAME);
		return t;
	}

}
