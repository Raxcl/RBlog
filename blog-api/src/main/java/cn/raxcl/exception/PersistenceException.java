package cn.raxcl.exception;

/**
 * @Description: 持久化异常
 * @author Raxcl
 * @date 2022-01-07 18:35:55
 */

public class PersistenceException extends RuntimeException {
	public PersistenceException() {
	}

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}
}
