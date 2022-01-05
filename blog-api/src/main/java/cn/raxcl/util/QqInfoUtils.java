package cn.raxcl.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Description: 获取QQ昵称头像信息
 * @author Raxcl
 * @date 2020-09-10
 */
public class QqInfoUtils {

	/**
	 * 服务访问地址，用于返回图片url
	 */
	@Value("${upload.path}")
	private String serverUploadPath;

	/**
	 * 服务器文件上传路径
	 */
	@Value("${custom.url.api}")
	private String serverUrl;





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
	 * 将QQ头像下载到本地，并返回访问本地图片的URL
	 *
	 * @param qq qq
	 * @return 访问本地图片的URL
	 * @throws IOException
	 */
	public static String getQQAvatarURLByServerUpload(String qq, String serverUploadPath, String serverUrl) throws IOException {
		return ImageUtils.saveImage(getImageResourceByQq(qq), serverUploadPath, serverUrl);
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