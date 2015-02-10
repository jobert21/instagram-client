package ja.apps.instagram.commons;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.RecordManagerOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbmUtils {
	private static final Logger log = LoggerFactory.getLogger(JdbmUtils.class);

	private static JdbmUtils instance = new JdbmUtils();
	private RecordManager rec;
	private Map<String, PrimaryTreeMap<String, ? extends Serializable>> map;
	private boolean init;

	private JdbmUtils() {
		map = new HashMap<String, PrimaryTreeMap<String, ? extends Serializable>>();
	}

	public static JdbmUtils getInstance() {
		return instance;
	}

	public void init(File cacheDir) throws IOException {
		if (init) {
			return;
		}
		log.debug("Initialize persistence manager.");
		File db = new File(cacheDir, "notalrecall");
		if (!db.exists()) {
			db.createNewFile();
		}

		Properties props = new Properties();
		props.put(RecordManagerOptions.AUTO_COMMIT, "true");
		rec = RecordManagerFactory.createRecordManager(db.getAbsolutePath(),
				props);
		init = true;

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				if (rec != null) {
					try {
						rec.commit();
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}));
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> PrimaryTreeMap<String, T> create(String name) {
		PrimaryTreeMap<String, T> tree = (PrimaryTreeMap<String, T>) map
				.get(name);
		if (tree == null) {
			log.debug("Creating tree map for \"{}\"", name);
			tree = rec.treeMap(name);
			map.put(name, tree);
		}

		return tree;
	}

}
