package ja.apps.instagram.http;

import ja.apps.instagram.commons.IResponseListener;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;

public class CallbackServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, String> params = new HashMap<String, String>();
		Enumeration<String> en = req.getParameterNames();
		while (en.hasMoreElements()) {
			String par = en.nextElement();
			Object o = req.getParameter(par);
			params.put(par, (String) o);
		}
		List<IResponseListener> listeners = HttpUtils.listeners;
		for (IResponseListener l : listeners) {
			l.response(params);
		}

		try {
			resp.setContentType("text/html");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println(
					"Authorization accepted. You may now close your browser.");
			((Request) req).setHandled(true);
		} catch (IOException e) {
		}
	}
}
