package ja.apps.instagram.ui;

import ja.apps.instagram.AppService;
import ja.apps.instagram.commons.Constants;
import ja.apps.instagram.commons.FeedType;
import ja.apps.instagram.commons.IAuthorizationListener;
import ja.apps.instagram.commons.IServiceListener;
import ja.apps.instagram.commons.MediaPanel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.jinstagram.entity.users.feed.MediaFeedData;
import javax.swing.SwingConstants;

public class MainUi extends JFrame implements IServiceListener,
		IAuthorizationListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private AppService appService;
	private JPanel scrollContent;
	private JScrollPane scrollPane;
	private JLabel loadingLbl;
	private JButton btnRefresh;
	private JPanel topEast;
	private JPanel panel;
	private JButton homeBtn;
	private FeedType feedType;
	private Map<FeedType, MediaPanel> feedsMap;
	private JButton populoarBtn;
	private JButton acctBtn;

	/**
	 * Create the frame.
	 */
	public MainUi() {
		String osName = System.getProperty("os.name");
		if (!osName.contains("Mac")) {
			setIconImage(Toolkit.getDefaultToolkit().getImage(
					MainUi.class.getResource("/imgs/Retro-Instagram-64.png")));
		}
		appService = new AppService();
		appService.setListener(this);
		feedType = FeedType.USER;
		feedsMap = new HashMap<FeedType, MediaPanel>();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 355, 650);
		setResizable(false);
		setTitle(Constants.APP_NAME);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel topPanel = new JPanel();
		contentPane.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));

		topEast = new JPanel();
		topPanel.add(topEast, BorderLayout.EAST);
		topEast.setLayout(new GridLayout(0, 2, 0, 0));

		loadingLbl = new JLabel("");
		loadingLbl.setHorizontalAlignment(SwingConstants.CENTER);
		topEast.add(loadingLbl);
		loadingLbl.setVisible(false);
		loadingLbl.setIcon(new ImageIcon(MainUi.class
				.getResource(Constants.LOADER_GIF)));

		btnRefresh = new JButton("");
		topEast.add(btnRefresh);
		btnRefresh.setIcon(new ImageIcon(MainUi.class
				.getResource("/imgs/01-refresh.png")));
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				loadContents(true);
				if (feedType == FeedType.USER) {
					homeBtn.setSelected(true);
					populoarBtn.setSelected(false);
				} else {
					homeBtn.setSelected(false);
					populoarBtn.setSelected(true);
				}
			}
		});
		panel = new JPanel();
		topPanel.add(panel, BorderLayout.WEST);

		homeBtn = new JButton("");
		homeBtn.setToolTipText("Home");
		homeBtn.setIcon(new ImageIcon(MainUi.class
				.getResource("/imgs/53-house.png")));
		homeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!appService.isUserAuthenticated()) {
					int ret = JOptionPane.showConfirmDialog(null,
							Constants.AUTH_REQ_MESSAGE,
							Constants.AUTH_REQ_TITLE,
							JOptionPane.OK_CANCEL_OPTION);
					if (ret == JOptionPane.OK_OPTION) {
						appService.requestAuthorization(MainUi.this);
					}
				} else {
					feedType = FeedType.USER;
					loadContents();
				}
				homeBtn.setSelected(true);
				populoarBtn.setSelected(false);
			}
		});
		panel.setLayout(new GridLayout(0, 3, 0, 0));

		panel.add(homeBtn);

		populoarBtn = new JButton("");
		populoarBtn.setToolTipText("Popular");
		populoarBtn.setIcon(new ImageIcon(MainUi.class
				.getResource("/imgs/02-star.png")));
		populoarBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				feedType = FeedType.POPULAR;
				loadContents();
				populoarBtn.setSelected(true);
				homeBtn.setSelected(false);
			}
		});
		panel.add(populoarBtn);

		acctBtn = new JButton("");
		acctBtn.setIcon(new ImageIcon(MainUi.class
				.getResource("/imgs/24-person.png")));
		acctBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String msg = Constants.CHANGE_ACCOUNT_MESSAGE;
				String title = Constants.CHANGE_ACCOUNT_TITLE;
				if (!appService.isUserAuthenticated()) {
					msg = Constants.AUTH_REQ_MESSAGE;
					title = Constants.AUTH_REQ_TITLE;
				}

				int ret = JOptionPane.showConfirmDialog(null, msg, title,
						JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.OK_OPTION) {
					appService.requestAuthorization(MainUi.this);
				}
			}
		});
		panel.add(acctBtn);

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		scrollContent = new JPanel();
		scrollPane.setViewportView(scrollContent);
		scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

		appService.login();
		loadContents();
	}

	private void loadContents() {
		loadContents(false);
	}

	private void loadContents(boolean forceRefresh) {

		MediaPanel mediaPanel = feedsMap.get(feedType);
		boolean refreshed = false;
		if (mediaPanel == null) {
//			if (!appService.isUserAuthenticated()) {
//				feedType = FeedType.POPULAR;
//			}
			if (feedType == FeedType.POPULAR || !appService.isUserAuthenticated()) {
				mediaPanel = new PopularPanel(appService) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected void feedClicked(MediaFeedData data) {
						RowPanel row = new RowPanel(data, appService);
						scrollContent.removeAll();
						scrollPane.validate();
						scrollContent.add(row);
						scrollContent.repaint();
						scrollPane.validate();
					}

				};
				feedType = FeedType.POPULAR;
			} else if (feedType == FeedType.USER
					&& appService.isUserAuthenticated()) {
				mediaPanel = new UserMediaPanel(appService);
			}
			feedsMap.put(feedType, mediaPanel);
			mediaPanel.loadData();
			refreshed = true;
		}
		scrollPane.validate();
		scrollContent.removeAll();
		scrollContent.add(mediaPanel);
		scrollContent.repaint();
		scrollPane.validate();
		if (forceRefresh && !refreshed) {
			mediaPanel.loadData();
		}
	}

	private void disableComponents(final boolean disable) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				loadingLbl.setVisible(!disable);
				btnRefresh.setEnabled(disable);
				homeBtn.setEnabled(disable);
				populoarBtn.setEnabled(disable);
				acctBtn.setEnabled(disable);
			}
		});
	}

	@Override
	public void working(final ServiceStatus status) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				disableComponents(status != ServiceStatus.BUSY);
			}
		});
	}

	@Override
	public void authorized() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				feedType = FeedType.USER;
				loadContents();
			}
		});
	}
}
