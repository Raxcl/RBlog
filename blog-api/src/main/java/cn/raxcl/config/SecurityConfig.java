package cn.raxcl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import cn.raxcl.service.LoginLogService;
import cn.raxcl.service.impl.UserServiceImpl;

/**
 * Spring Security配置类
 * @author Raxcl
 * @date 2022-01-07 17:57:58
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserServiceImpl userService;
	private final LoginLogService loginLogService;
	private final MyAuthenticationEntryPoint myAuthenticationEntryPoint;

	public SecurityConfig(UserServiceImpl userService, LoginLogService loginLogService, MyAuthenticationEntryPoint myAuthenticationEntryPoint) {
		this.userService = userService;
		this.loginLogService = loginLogService;
		this.myAuthenticationEntryPoint = myAuthenticationEntryPoint;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				//禁用 csrf 防御
				.csrf().disable()
				//开启跨域支持
				.cors().and()
				//基于Token，不创建会话
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests()
				//放行获取网页标题后缀的请求
				.antMatchers("/admin/webTitleSuffix").permitAll()
				//任何 /admin 开头的路径下的请求都需要经过JWT验证
				.antMatchers(HttpMethod.GET, "/admin/**").hasAnyRole("admin", "visitor")
				.antMatchers("/admin/**").hasRole("admin")
				//其它路径全部放行
				.anyRequest().permitAll()
				.and()
				//自定义JWT过滤器
				.addFilterBefore(new JwtLoginFilter("/RBlog/admin/login", authenticationManager(), loginLogService), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
				//未登录时，返回json，在前端执行重定向
				.exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint);
	}
}
