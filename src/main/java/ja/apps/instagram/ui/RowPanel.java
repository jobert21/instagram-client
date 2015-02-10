package ja.apps.instagram.ui;

import ja.apps.instagram.AppService;
import ja.apps.instagram.commons.AppUtils;
import ja.apps.instagram.commons.Constants;
import ja.apps.instagram.commons.IProgressWatcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jinstagram.entity.common.Caption;
import org.jinstagram.entity.common.Comments;
import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Likes;
import org.jinstagram.entity.common.User;
import org.jinstagram.entity.users.feed.MediaFeedData;
import java.awt.GridLayout;

public class RowPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel username;
	private JLabel profilePic;
	private JLabel mainImage;
	private MediaFeedData data;
	private JPanel btmPanel;
	private JLabel caption;
	private JPanel btmBtns;
	private JButton btnLike;
	private AppService appService;
	private JPanel panel;
	private JButton btnComments;
	private JPanel topPanelLeft;
	private JPanel topPanelRight;
	private JLabel timeLbl;

	public RowPanel(MediaFeedData data, final AppService appService) {
		this(data, appService, false);
	}

	/**
	 * Create the panel.
	 */
	public RowPanel(final MediaFeedData data, final AppService appService,
			boolean useStandardResolution) {
		this.data = data;
		this.appService = appService;
		setLayout(new BorderLayout(0, 0));
		setBorder(new EmptyBorder(5, 5, 10, 5));
		setBackground(Color.WHITE);

		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		topPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));

		topPanelLeft = new JPanel();
		topPanelLeft.setBackground(Color.WHITE);
		topPanel.add(topPanelLeft, BorderLayout.WEST);
		topPanelLeft.setLayout(new BoxLayout(topPanelLeft, BoxLayout.X_AXIS));

		profilePic = new JLabel("");
		topPanelLeft.add(profilePic);
		profilePic.setIcon(new ImageIcon(RowPanel.class
				.getResource(Constants.LOADER_GIF)));
		// profilePic.setBorder(new LineBorder(Color.GRAY, 1));

		username = new JLabel("");
		topPanelLeft.add(username);
		username.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		username.setBorder(new EmptyBorder(0, 5, 0, 0));

		topPanelRight = new JPanel();
		topPanelRight.setBackground(Color.WHITE);
		topPanel.add(topPanelRight, BorderLayout.EAST);
		topPanelRight.setLayout(new BoxLayout(topPanelRight, BoxLayout.X_AXIS));

		timeLbl = new JLabel("");
		timeLbl.setForeground(Color.DARK_GRAY);
		timeLbl.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		timeLbl.setHorizontalAlignment(SwingConstants.CENTER);
		topPanelRight.add(timeLbl);

		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(Color.WHITE);
		centerPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));

		mainImage = new JLabel("Loading..");
		if (!useStandardResolution) {
			mainImage.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent arg0) {
					StandardViewDialog largeDialog = new StandardViewDialog(
							data, appService);
					largeDialog.setModal(true);
					largeDialog.setVisible(true);
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					mainImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					mainImage.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
		// mainImage.setBorder(new LineBorder(Color.GRAY, 1));
		mainImage.setIcon(new ImageIcon(RowPanel.class
				.getResource(Constants.LOADER_GIF)));
		mainImage.setHorizontalAlignment(SwingConstants.CENTER);
		centerPanel.add(mainImage, BorderLayout.CENTER);

		btmPanel = new JPanel();
		btmPanel.setBackground(Color.WHITE);
		btmPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(btmPanel, BorderLayout.SOUTH);
		btmPanel.setLayout(new BorderLayout(0, 0));

		caption = new JLabel("");
		caption.setBorder(new EmptyBorder(0, 5, 0, 5));
		btmPanel.add(caption);

		btmBtns = new JPanel();
		btmBtns.setBackground(Color.WHITE);
		btmPanel.add(btmBtns, BorderLayout.NORTH);
		btmBtns.setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		btmBtns.add(panel, BorderLayout.WEST);
		panel.setLayout(new GridLayout(0, 2, 0, 0));

		btnLike = new JButton("0");
		btnLike.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		panel.add(btnLike);
		btnLike.setEnabled(false);
		btnLike.setIcon(new ImageIcon(RowPanel.class
				.getResource("/imgs/03-heart.png")));

		btnComments = new JButton("0");
		btnComments.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		btnComments.setIcon(new ImageIcon(RowPanel.class
				.getResource("/imgs/20-chat.png")));
		btnComments.setEnabled(false);
		panel.add(btnComments);

		appService.submitRunnable(new LoadDataRunnable(useStandardResolution));
	}

	class LoadDataRunnable implements Runnable {
		private boolean useStandardResolution;

		public LoadDataRunnable() {
			this(false);
		}

		public LoadDataRunnable(boolean useStandardResolution) {
			this.useStandardResolution = useStandardResolution;
		}

		public void run() {
			User user = data.getUser();
			String picUrl = user.getProfilePictureUrl();
			Images images = data.getImages();
			String url = useStandardResolution ? images.getStandardResolution()
					.getImageUrl() : images.getLowResolution().getImageUrl();
			Caption capt = data.getCaption();
			Likes likes = data.getLikes();
			final Comments comments = data.getComments();
			try {
				final String _username = user.getUserName();

				final String captionTxt = capt != null ? capt.getText() : "";
				final int likeCount = likes.getCount();
				final int cmtCount = comments.getCount();

				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						username.setIcon(null);
						username.setText(_username);
						timeLbl.setText(AppUtils.usePrettyTime(Long
								.parseLong(data.getCreatedTime())));
						caption.setText(String
								.format("<html><div style=\"width:%dpx;\">%s</div><html>",
										useStandardResolution ? 470 : 230, captionTxt));
						btnComments.setText(Integer.toString(cmtCount));
						btnComments.setEnabled(true);
						btnComments.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								CommentsDialog d = new CommentsDialog(data
										.getId(), comments, appService);
								d.setTitle("@" + _username);
								d.setVisible(true);
							}
						});

						btnLike.setText(Integer.toString(likeCount));
						if (!data.isUserHasLiked()) {
							btnLike.setEnabled(true);
							btnLike.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent arg0) {
									btnLike.setEnabled(false);
									appService.userLike(data,
											new IProgressWatcher<Void>() {

												@Override
												public void failed(String msg) {
													enableBtn(btnLike, true);
													System.out.println(msg);
												}

												@Override
												public void done(Void obj) {
													enableBtn(
															btnLike,
															false,
															Integer.toString(likeCount + 1));
												}
											});
								}
							});
						} else {
							btnLike.setEnabled(false);
						}
					}
				});

				byte[] picBytes = AppUtils
						.downloadResourceFromHttpAndSave(picUrl);
				final Image pathPicUrl = AppUtils.resizeImage(picBytes, 35);
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						profilePic.setIcon(new ImageIcon(pathPicUrl));
					}
				});

				final byte[] pathImgUrl = AppUtils
						.downloadResourceFromHttpAndSave(url);
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						mainImage.setText("");
						mainImage.setIcon(new ImageIcon(pathImgUrl));
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void enableBtn(final JButton comp, final boolean enable) {
		enableBtn(comp, enable, null);
	}

	private void enableBtn(final JButton comp, final boolean enable,
			final String label) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				comp.setEnabled(enable);
				if (label != null) {
					comp.setText(label);
					comp.repaint();
				}
			}
		});
	}
}
