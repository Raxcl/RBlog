package cn.raxcl.util;

import org.apache.commons.codec.digest.MurmurHash3;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

/**
 * @Description: Hash工具类
 * @author Raxcl
 * @date 2020-11-17
 */
public class HashUtils {

	private HashUtils(){}

	private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

	public static String getMd5(CharSequence str) {
		return DigestUtils.md5DigestAsHex(str.toString().getBytes());
	}

	public static long getMurmurHash32(String str) {
		int i = MurmurHash3.hash32(str);
		return i < 0 ? Integer.MAX_VALUE - (long) i : i;
	}

	public static String getBc(CharSequence rawPassword) {
		return B_CRYPT_PASSWORD_ENCODER.encode(rawPassword);
	}

	public static boolean matchBc(CharSequence rawPassword, String encodedPassword) {
		return B_CRYPT_PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
	}

}
