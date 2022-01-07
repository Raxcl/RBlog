package cn.raxcl.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

/**
 * @Description: JWT工具类
 * @author Raxcl
 * @date 2022-01-04 11:08:34
 */
@Component
public class JwtUtils {
	private JwtUtils(){
	}

	/**
	 * 判断token是否存在
	 *
	 * @param token token
	 */
	public static boolean judgeTokenIsExist(String token) {
		return token != null && !"".equals(token) && !"null".equals(token);
	}

	/**
	 * 生成token
	 *
	 * @param subject subject
	 */
	public static String generateToken(String subject,Long expireTime, String secretKey) {
		return Jwts.builder()
				.setSubject(subject)
				.setExpiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}

	/**
	 * 生成带角色权限的token
	 *
	 * @param subject subject
	 * @param authorities authorities
	 */
	public static String generateToken(String subject, Collection<? extends GrantedAuthority> authorities,long expireTime,String secretKey ) {
		StringBuilder sb = new StringBuilder();
		for (GrantedAuthority authority : authorities) {
			sb.append(authority.getAuthority()).append(",");
		}
		return Jwts.builder()
				.setSubject(subject)
				.claim("authorities", sb)
				.setExpiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}

	/**
	 * 生成自定义过期时间token
	 *
	 * @param subject  subject
	 * @param expireTime expireTime
	 */
	public static String generateToken(String subject, long expireTime, String secretKey) {
		return Jwts.builder()
				.setSubject(subject)
				.setExpiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();

	}


	/**
	 * 获取tokenBody同时校验token是否有效（无效则会抛出异常）
	 *
	 * @param token token
	 */
	public static Claims getTokenBody(String token, String secretKey) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.replace("Bearer", "")).getBody();
	}
}
