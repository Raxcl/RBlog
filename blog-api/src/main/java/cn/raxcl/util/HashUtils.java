package cn.raxcl.util;

import org.apache.commons.codec.digest.MurmurHash3;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Hash工具类
 * @author Raxcl
 * @date 2022-01-11 10:52:18
 */
public class HashUtils {

	private HashUtils(){}

	private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();


	public static long getMurmurHash32(String str) {
		int i = MurmurHash3.hash32(str);
		return i < 0 ? Integer.MAX_VALUE - (long) i : i;
	}

	public static boolean matchBc(CharSequence rawPassword, String encodedPassword) {
		return B_CRYPT_PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
	}

	/**
	 * 用于生成密码
	 * @param rawPassword 明文密码
	 * @return String
	 */
	public static String getBc(CharSequence rawPassword) {
		return B_CRYPT_PASSWORD_ENCODER.encode(rawPassword);
	}

}
