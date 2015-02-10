package ja.apps.instagram.ui;

import ja.apps.instagram.AppService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jinstagram.entity.users.feed.MediaFeedData;

public class StandardViewDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public StandardViewDialog(MediaFeedData data, final AppService appService) {
		setBounds(100, 100, 630, 760);
		setResizable(false);
		setTitle("@" + data.getUser().getUserName());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						StandardViewDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.GRAY));
		contentPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		RowPanel rowPanel = new RowPanel(data, appService, true);
		rowPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
		panel.add(rowPanel, BorderLayout.CENTER);
	}

}
