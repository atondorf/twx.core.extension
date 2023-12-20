package twx.core.db.model;

public class DbModelException extends RuntimeException {
    private static final long serialVersionUID = -1L;

    public DbModelException() {
        super();
    }

    public DbModelException(String msg) {
        super(msg);
    }

    public DbModelException(Throwable baseEx) {
        super(baseEx);
    }

    public DbModelException(String msg, Throwable baseEx) {
        super(msg, baseEx);
    }

}
