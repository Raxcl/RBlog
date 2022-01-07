package cn.raxcl.util.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 封装响应结果
 * @author Raxcl
 * @date 2020-07-19
 */

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Result {
	private Integer code;
	private String msg;
	private Object data;

	private Result(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
		this.data = null;
	}

	private Result(Integer code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public static Result success(String msg, Object data) {
		return new Result(200, msg, data);
	}

	public static Result success(String msg) {
		return new Result(200, msg);
	}

	public static Result error(String msg) {
		return new Result(500, msg);
	}

	public static Result error() {
		return new Result(500, "异常错误");
	}

	public static Result exception(Integer code, String msg, Object data) {
		return new Result(code, msg, data);
	}

	public static Result exception(Integer code, String msg) {
		return new Result(code, msg);
	}
}
