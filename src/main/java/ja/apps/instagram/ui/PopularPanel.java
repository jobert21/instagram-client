package ja.apps.instagram.ui;

import ja.apps.instagram.AppService;
import ja.apps.instagram.commons.AppUtils;
import ja.apps.instagram.commons.IProgressWatcher;
import ja.apps.instagram.commons.MediaPanel;
import ja.apps.instagram.commons.WrapLayout;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jinstagram.entity.common.ImageData;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

public abstract class PopularPanel extends MediaPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AppService appService;

	/**
	 * Create the panel.
	 */
	public PopularPanel(final AppService appService) {
		this.appService = appService;
		setBackground(Color.WHITE);
		WrapLayout layout = new WrapLayout(WrapLayout.LEFT, 5, 5);
		layout.setAlignOnBaseline(true);
		setLayout(layout);
	}

	@Override
	public void loadData() {
		appService.popularMediaFeed(new IProgressWatcher<MediaFeed>() {

			@Override
			public void failed(String msg) {

			}

			@Override
			public void done(MediaFeed feed) {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						removeAll();
					}

				});
				List<MediaFeedData> datas = feed.getData();
				for (final MediaFeedData data : datas) {
					ImageData imgData = data.getImages().getThumbnail();
					String imgUrl = imgData.getImageUrl();
					try {
						final byte[] img = AppUtils
								.downloadResourceFromHttpAndSave(imgUrl);
						EventQueue.invokeLater(new Runnable() {

							@Override
							public void run() {
								final JLabel imgLabel = new JLabel("");
								imgLabel.addMouseListener(new MouseAdapter() {

									@Override
									public void mouseReleased(MouseEvent arg0) {
										feedClicked(data);
									}

									@Override
									public void mouseEntered(MouseEvent arg0) {
										imgLabel.setCursor(new Cursor(
												Cursor.HAND_CURSOR));
									}

									@Override
									public void mouseExited(MouseEvent arg0) {
										imgLabel.setCursor(new Cursor(
												Cursor.DEFAULT_CURSOR));
									}
								});
								imgLabel.setIcon(new ImageIcon(img));
								add(imgLabel);
							}
						});
					} catch (Exception e) {
					}
				}
			}
		});
	}

	protected abstract void feedClicked(MediaFeedData data);
}
