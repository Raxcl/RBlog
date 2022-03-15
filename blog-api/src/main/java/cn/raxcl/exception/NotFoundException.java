package cn.raxcl.exception;

/**
 * 404异常
 * @author Raxcl
 * @date 2022-01-07 18:36:01
 */

public class NotFoundException extends RuntimeException {

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}


}
