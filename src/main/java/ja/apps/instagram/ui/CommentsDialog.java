package ja.apps.instagram.ui;

import ja.apps.instagram.AppService;
import ja.apps.instagram.commons.AppUtils;
import ja.apps.instagram.commons.Constants;
import ja.apps.instagram.commons.IProgressWatcher;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentResponse;
import org.jinstagram.entity.common.Comments;

public class CommentsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JPanel scrollContent;
	private AppService appService;
	private Comments comments;
	private JTextField msgField;
	private JButton btnSend;
	private JButton cancelButton;

	/**
	 * Create the dialog.
	 */
	public CommentsDialog(final String mediaId, Comments comments,
			final AppService appService) {
		this.comments = comments;
		this.appService = appService;

		setBounds(100, 100, 450, 300);
		setResizable(false);
		setModal(true);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				scrollContent = new JPanel();
				scrollPane.setViewportView(scrollContent);
				scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
			}
		}
		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new BorderLayout(0, 0));
		{
			{
				JPanel btnsPanel = new JPanel();
				buttonPane.add(btnsPanel, BorderLayout.EAST);
				btnSend = new JButton("Send");
				btnSend.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						String text = msgField.getText();
						if (text == null || text.length() == 0) {
							return;
						}
						enableSendComponents(false);

						long mId = AppUtils.mediaDataId(mediaId);
						appService.mediaComment(mId, text,
								new IProgressWatcher<MediaCommentResponse>() {

									@Override
									public void failed(String msg) {
										System.out.println(msg);
										enableSendComponents(true);
									}

									@Override
									public void done(
											final MediaCommentResponse obj) {
										enableSendComponents(true);
										EventQueue.invokeLater(new Runnable() {

											@Override
											public void run() {
												CommentData data = obj
														.getCommentData();
												CommentPanel comm = new CommentPanel(
														data, appService);
												scrollContent.add(comm);
												scrollContent.repaint();
											}
										});
									}
								});
					}
				});
				btnsPanel.add(btnSend);
				cancelButton = new JButton("Close");
				btnsPanel.add(cancelButton);
				cancelButton.setActionCommand("Cancel");
				{
					JPanel msgPanel = new JPanel();
					msgPanel.setBorder(new EmptyBorder(3, 3, 5, 0));
					buttonPane.add(msgPanel, BorderLayout.CENTER);
					msgPanel.setLayout(new GridLayout(1, 0, 0, 0));
					{
						msgField = new JTextField(
								"");
						msgPanel.add(msgField);
						msgField.setColumns(10);
					}
				}
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						CommentsDialog.this.dispose();
					}
				});
			}
		}
		enableSendComponents(false);
		loadData();
	}

	private void enableSendComponents(final boolean enable) {
//		EventQueue.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
				// btnSend.setText(enable ? "Send" : "");
				btnSend.setIcon(!enable ? new ImageIcon(CommentsDialog.class
						.getResource(Constants.BALL_LOADER_GIF)) : null);
				btnSend.setEnabled(enable);
				btnSend.setVisible(enable);
//				 cancelButton.setEnabled(enable);
				msgField.setEnabled(enable);
				msgField.setVisible(enable);
//			}
//		});

	}

	public void loadData() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				scrollContent.removeAll();
			}

		});

		List<CommentData> datas = comments.getComments();
		for (final CommentData data : datas) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					CommentPanel commentPanel = new CommentPanel(data,
							appService);
					scrollContent.add(commentPanel);
				}
			});
		}

	}
}
