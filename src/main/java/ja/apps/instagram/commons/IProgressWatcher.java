package ja.apps.instagram.commons;

public interface IProgressWatcher<T> {
	void done(T obj);

	void failed(String msg);
}
