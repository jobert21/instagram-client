package ja.apps.instagram.http;

import ja.apps.instagram.commons.IResponseListener;

import java.util.ArrayList;
import java.util.List;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
	public static final int PORT = 9888;
	public static final String CTX_PATH = "/callback";
	private static Server server;
	private static String url;
	protected static final List<IResponseListener> listeners = new ArrayList<IResponseListener>();

	public static String buildUrl() {
		if (url == null) {
			url = "http://127.0.0.1:" + PORT + CTX_PATH;
		}
		return url;
	}

	public static void addResponseListener(IResponseListener l) {
		listeners.add(l);
	}

	public static void startServer() throws Exception {
		if (server == null) {
			server = new Server(PORT);
			Context context = new Context(server, CTX_PATH, Context.REQUEST);
			context.addServlet(new ServletHolder(CallbackServlet.class), "/*");
			server.start();
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					stopServer();
				}
			}));
		}
	}

	public static void stopServer() {
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

}
