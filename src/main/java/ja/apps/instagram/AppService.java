package ja.apps.instagram;

import ja.apps.instagram.commons.AppUtils;
import ja.apps.instagram.commons.Constants;
import ja.apps.instagram.commons.CustomThreadPoolFactory;
import ja.apps.instagram.commons.IAuthorizationListener;
import ja.apps.instagram.commons.IProgressWatcher;
import ja.apps.instagram.commons.IResponseListener;
import ja.apps.instagram.commons.IServiceListener;
import ja.apps.instagram.commons.IServiceListener.ServiceStatus;
import ja.apps.instagram.http.HttpUtils;
import ja.apps.instagram.model.UserCredential;
import ja.apps.instagram.persist.UserCredentialDao;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.comments.MediaCommentResponse;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.model.Scope;

public class AppService implements IResponseListener {
	private ExecutorService service;
	private InstagramService instaService;
	private Instagram instagram;
	private IServiceListener listener;
	private IAuthorizationListener authListener;
	private static final Token EMPTY_TOKEN = null;

	public AppService() {
		service = Executors.newCachedThreadPool(new CustomThreadPoolFactory());
		HttpUtils.addResponseListener(this);
	}

	public void setListener(IServiceListener l) {
		this.listener = l;
	}

	public void login() {
		if (instagram != null) {
			return;
		}
		UserCredential creds = UserCredentialDao.getInstance().getCredential();
		Token token = null;
		if (creds != null) {
			token = new Token(creds.getToken(), Constants.CLIENT_SECRET);
			instagram = new Instagram(token);
		} else {
			instagram = new Instagram(Constants.CLIENT_ID);
		}
	}

	public void requestAuthorization(IAuthorizationListener authListener) {
		this.authListener = authListener;
		if (instaService == null) {
			instaService = new InstagramAuthService()
					.apiKey(Constants.CLIENT_ID)
					.apiSecret(Constants.CLIENT_SECRET)
					.callback(HttpUtils.buildUrl())
					.scope(Scope.LIKES.toString() + " "
							+ Scope.COMMENTS.toString() + " "
							+ Scope.RELATIONSHIPS.toString()).build();
		}
		String authUrl = instaService.getAuthorizationUrl(null);
		AppUtils.openBrowser(authUrl);
	}

	public boolean isUserAuthenticated() {
		return UserCredentialDao.getInstance().getCredential() != null;
	}

	public void popularMediaFeed(final IProgressWatcher<MediaFeed> progress) {
		if (instagram == null) {
			progress.failed("Invalid credentials.");
			return;
		}
		service.submit(new Runnable() {

			@Override
			public void run() {
				try {
					listener.working(ServiceStatus.BUSY);
					MediaFeed feed = instagram.getPopularMedia();
					progress.done(feed);
				} catch (Exception e) {
					progress.failed(e.getMessage());
				} finally {
					listener.working(ServiceStatus.IDLE);
				}
			}
		});
	}

	public void userMediaFeed(final IProgressWatcher<MediaFeed> progress) {
		if (instagram == null) {
			progress.failed("Invalid credentials.");
			return;
		}
		service.submit(new Runnable() {

			@Override
			public void run() {
				try {
					listener.working(ServiceStatus.BUSY);
					MediaFeed feed = instagram.getUserFeeds();
					progress.done(feed);
				} catch (Exception e) {
					progress.failed(e.getMessage());
				} finally {
					listener.working(ServiceStatus.IDLE);
				}
			}
		});
	}

	public void mediaComment(final long mId, final String text,
			final IProgressWatcher<MediaCommentResponse> progress) {
		if (instagram == null) {
			progress.failed("Invalid credentials.");
			return;
		}
		service.submit(new Runnable() {

			@Override
			public void run() {
				try {
					listener.working(ServiceStatus.BUSY);
					MediaCommentResponse resp = instagram.setMediaComments(Long.toString(mId),
							text);
					progress.done(resp);
				} catch (Exception e) {
					progress.failed(e.getMessage());
				} finally {
					listener.working(ServiceStatus.IDLE);
				}
			}
		});
	}

	public void userLike(final MediaFeedData data,
			final IProgressWatcher<Void> progress) {
		if (instagram == null) {
			progress.failed("Invalid credentials.");
			return;
		}
		service.submit(new Runnable() {

			@Override
			public void run() {
				try {
					listener.working(ServiceStatus.BUSY);
					long mediaId = AppUtils.mediaDataId(data.getId());
					instagram.setUserLike(Long.toString(mediaId));
					progress.done(null);
				} catch (Exception e) {
					progress.failed(e.getMessage());
				} finally {
					listener.working(ServiceStatus.IDLE);
				}
			}
		});
	}

	public void serviceBusy(boolean busy) {
		listener.working(busy ? ServiceStatus.BUSY : ServiceStatus.IDLE);
	}

	public void submitRunnable(Runnable r) {
		service.submit(r);
	}

	@Override
	public void response(Map<String, String> params) {
		String code = params.get("code");
		if (instaService != null && code != null) {
			Verifier verifier = new Verifier(code);
			Token accessToken = instaService.getAccessToken(EMPTY_TOKEN,
					verifier);
			instagram = new Instagram(accessToken);
			UserCredentialDao.getInstance().saveCredential(
					new UserCredential(accessToken.getToken()));
			if (authListener != null) {
				authListener.authorized();
			}
		}
	}
}
