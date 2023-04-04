package twx.core.db;

public interface ExecuteHandler<T> {
  T execute() throws Exception;
}
