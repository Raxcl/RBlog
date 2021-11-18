package cn.raxcl.exception;

/**
 * @Description: 非法请求异常
 * @Author: Raxcl
 * @Date: 2020-07-23
 */

public class BadRequestException extends RuntimeException {
	public BadRequestException() {
	}

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
