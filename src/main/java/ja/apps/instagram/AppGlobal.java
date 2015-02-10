package ja.apps.instagram;

import ja.apps.instagram.commons.Constants;
import ja.apps.instagram.commons.JdbmUtils;
import ja.apps.instagram.http.HttpUtils;
import ja.apps.instagram.ui.MainUi;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppGlobal {
	private static final Logger log = LoggerFactory.getLogger(AppGlobal.class);
	public static File CACHE_DIR;

	private AppGlobal() {
	}

	public static void configureUi() throws Exception {
		String osName = System.getProperty("os.name");
		if (osName.contains("Mac")) {
			System.setProperty(Constants.OSX_USE_SCREEN_MENU_BAR, "true");
			System.setProperty(Constants.OSX_ABOUT_NAME, Constants.APP_NAME);
//			System.setProperty(Constants.OSX_BRUSH_METAL_LOOK, "true");
		}
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUi frame = new MainUi();
					frame.setVisible(true);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					System.exit(1);
				}
			}
		});
	}

	public static void main(String[] args) {
		CACHE_DIR = new File(System.getProperty("user.home"),
				Constants.LOCAL_DIR_STORE);
		if (!CACHE_DIR.exists()) {
			CACHE_DIR.mkdirs();
		}

		try {
			JdbmUtils.getInstance().init(CACHE_DIR);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			System.exit(1);
		}

		try {
			HttpUtils.startServer();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			System.exit(1);
		}

		try {
			configureUi();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			System.exit(1);
		}
	}
}
