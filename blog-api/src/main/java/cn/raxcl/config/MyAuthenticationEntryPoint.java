package cn.raxcl.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import cn.raxcl.util.common.Result;
import cn.raxcl.util.JacksonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 未登录 拒绝访问
 * @author Raxcl
 * @date 2022-01-07 17:47:28
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException {
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		Result result = Result.exception(403, "请登录");
		out.write(JacksonUtils.writeValueAsString(result));
		out.flush();
		out.close();
	}
}
