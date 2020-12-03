package db;

public class DbIntegrationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DbIntegrationException (String msg) {
		super(msg);
	}
}
