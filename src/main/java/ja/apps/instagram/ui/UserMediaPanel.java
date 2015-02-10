package ja.apps.instagram.ui;

import ja.apps.instagram.AppService;
import ja.apps.instagram.commons.IProgressWatcher;
import ja.apps.instagram.commons.MediaPanel;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.BoxLayout;

import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

public class UserMediaPanel extends MediaPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AppService appService;

	public UserMediaPanel(AppService appService) {
		this.appService = appService;
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	@Override
	public void loadData() {
		appService.userMediaFeed(new IProgressWatcher<MediaFeed>() {

			@Override
			public void failed(String msg) {

			}

			@Override
			public void done(final MediaFeed obj) {
				final List<MediaFeedData> list = obj.getData();
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						removeAll();
					}
				});

				for (final MediaFeedData data : list) {
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {
							RowPanel row = new RowPanel(data, appService);
							add(row);
						}
					});

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}

			}
		});
	}

}
