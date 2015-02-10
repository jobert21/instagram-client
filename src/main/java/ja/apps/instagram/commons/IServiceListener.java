package ja.apps.instagram.commons;

public interface IServiceListener {
	void working(ServiceStatus status);
	
	public enum ServiceStatus {
		IDLE, BUSY;
	}
}
