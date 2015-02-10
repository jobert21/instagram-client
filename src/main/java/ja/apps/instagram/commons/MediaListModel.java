package ja.apps.instagram.commons;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

public class MediaListModel extends DefaultListModel<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MediaFeedData> listData;

	public MediaListModel() {
		listData = new ArrayList<MediaFeedData>();
	}

	public void setData(MediaFeed feed) {
		listData.clear();
		listData.addAll(feed.getData());
		setSize(listData.size());
	}

	public void clear() {
		if (listData != null) {
			listData.clear();
		}
		setSize(0);
	}

	@Override
	public Object getElementAt(int arg0) {
		return !listData.isEmpty() ? listData.get(arg0) : null;
	}

	@Override
	public int getSize() {
		return listData.size();
	}
}
