package cn.raxcl.exception;

/**
 * @Description: 非法请求异常
 * @author Raxcl
 * @date 2022-01-07 18:34:51
 */

public class BadRequestException extends RuntimeException {

	public BadRequestException(String message) {
		super(message);
	}




}
