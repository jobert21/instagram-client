package ja.apps.instagram.ui;

import ja.apps.instagram.AppService;
import ja.apps.instagram.commons.AppUtils;
import ja.apps.instagram.commons.Constants;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.common.FromTagData;

public class CommentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblUser;
	private JLabel profilePicLbl;
	private JLabel lblComment;
	private JLabel timeLbl;
	private CommentData data;

	// private AppService appService;

	/**
	 * Create the panel.
	 */
	public CommentPanel(CommentData data, final AppService appService) {
		this.data = data;
		// this.appService = appService;

		setLayout(new BorderLayout(0, 0));

		profilePicLbl = new JLabel("");
		profilePicLbl.setVerticalAlignment(SwingConstants.TOP);
		profilePicLbl.setIcon(new ImageIcon(CommentPanel.class
				.getResource(Constants.LOADER_GIF)));
		// profilePicLbl.setBorder(new LineBorder(Color.GRAY, 1));
		profilePicLbl.setBorder(new EmptyBorder(5, 0, 0, 0));
		add(profilePicLbl, BorderLayout.WEST);

		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));

		lblUser = new JLabel("");
		lblUser.setVerticalAlignment(SwingConstants.TOP);
		lblUser.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		centerPanel.add(lblUser, BorderLayout.NORTH);

		lblComment = new JLabel("");
		lblComment.setVerticalAlignment(SwingConstants.TOP);
		centerPanel.add(lblComment, BorderLayout.CENTER);
		centerPanel.setBorder(new EmptyBorder(0, 5, 0, 5));

		timeLbl = new JLabel("");
		timeLbl.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		timeLbl.setBorder(new EmptyBorder(3, 0, 0, 0));
		timeLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		timeLbl.setVerticalAlignment(SwingConstants.TOP);
		add(timeLbl, BorderLayout.EAST);

		appService.submitRunnable(new LoadDataRunnable());
	}

	class LoadDataRunnable implements Runnable {
		private Image profilePic;

		@Override
		public void run() {
			FromTagData from = data.getCommentFrom();

			try {
				final String comment = data.getText();
				byte[] picBytes = AppUtils.downloadResourceFromHttpAndSave(from
						.getProfilePicture());
				try {
					profilePic = picBytes != null ? AppUtils.resizeImage(
							picBytes, 35) : null;

				} catch (NullPointerException e) {
				}
				final String username = from.getUsername();
				final String time = data.getCreatedTime();
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						profilePicLbl.setIcon(new ImageIcon(profilePic));
						lblUser.setText(username);
						lblComment.setText(String
								.format("<html><div style=\"width:%dpx;\">%s</div><html>",
										200, comment));
						timeLbl.setText(AppUtils.usePrettyTime(Long
								.parseLong(time)));
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
