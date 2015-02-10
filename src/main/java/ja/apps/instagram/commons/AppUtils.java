package ja.apps.instagram.commons;

import ja.apps.instagram.AppGlobal;
import ja.tools.common.HttpCall;
import ja.tools.common.JTools;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;

import org.ocpsoft.prettytime.PrettyTime;

public class AppUtils {
	private static PrettyTime prettyTime = new PrettyTime();

	public static byte[] downloadResourceFromHttpAndSave(String urlString)
			throws Exception {
		String filename = Integer.toString(urlString.hashCode());
		File file = new File(AppGlobal.CACHE_DIR, filename);
		byte[] data = null;
		if (!file.exists()) {
			data = downloadResourceFromHttp(urlString);
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(data, 0, data.length);
			out.flush();
			out.close();
		} else {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			copyStream(in, out);
			data = out.toByteArray();
		}
		return data;
	}

	public static byte[] downloadResourceFromHttp(String urlString)
			throws Exception {
		return HttpCall.create(urlString).get();
	}

	public static Image resizeImage(byte[] data, int size) {
		return JTools.resizeImage(data, size, true);
		// try {
		// return Scalr.resize(ImageIO.read(new ByteArrayInputStream(data)),
		// size);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return null;
	}

	public static void copyStream(InputStream in, OutputStream os)
			throws IOException {
		int read = 0;
		byte[] buff = new byte[1024];
		while ((read = in.read(buff, 0, buff.length)) > 0) {
			os.write(buff, 0, read);
		}
	}

	public static String usePrettyTime(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis * 1000);
		Date d = cal.getTime();
		return prettyTime.format(d);
	}

	public static long mediaDataId(String id) {
		String[] split = id.split("_");
		return Long.parseLong(split[0]);
	}

	public static void openBrowser(String url) {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI(url));
		} catch (final Exception e) {
			e.printStackTrace();
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			});

		}
	}
}
