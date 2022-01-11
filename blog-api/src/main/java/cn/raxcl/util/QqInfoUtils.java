package cn.raxcl.util;

import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 获取QQ昵称头像信息
 * @author Raxcl
 * @date 2022-01-07 19:50:43
 */
public class QqInfoUtils {
	private QqInfoUtils(){}
	private static final RestTemplate REST_TEMPLATE = new RestTemplate();
	private static final String QQ_NICKNAME_URL = "https://r.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?uins={1}";
	private static final String QQ_AVATAR_URL = "https://q.qlogo.cn/g?b=qq&nk=%s&s=100";

	/**
	 * 获取QQ昵称
	 *
	 * @param qq qq
	 * @throws UnsupportedEncodingException 编码异常
	 */
	public static String getQqNickname(String qq) throws UnsupportedEncodingException {
		String res = REST_TEMPLATE.getForObject(QQ_NICKNAME_URL, String.class, qq);
		byte[] bytes = Objects.requireNonNull(res).getBytes(StandardCharsets.ISO_8859_1);
		String nickname = new String(bytes, "gb18030").split(",")[6].replace("\"", "");
		if ("".equals(nickname)) {
			return "nickname";
		}
		return nickname;
	}

	private static ImageUtils.ImageResource getImageResourceByQq(String qq) {
		return ImageUtils.getImageByRequest(String.format(QQ_AVATAR_URL, qq));
	}

	/**
	 * 将QQ头像上传至GitHub仓库，并返回CDN链接
	 *
	 * @param qq qq
	 * @return 指向该图片的jsDelivr CDN链接
	 */
	public static String getQqAvatarUrlByGithubUpload(String qq, String githubToken, String githubUsername, String githubRepos, String githubReposPath) {
		return ImageUtils.push2Github(getImageResourceByQq(qq), githubToken, githubUsername, githubRepos, githubReposPath);
	}

	public static boolean isQqNumber(String nickname) {
		return nickname.matches("^[1-9][0-9]{4,10}$");
	}
}