package cn.raxcl.util;

import cn.raxcl.model.vo.QqResultVO;
import cn.raxcl.model.vo.QqVO;
import cn.raxcl.util.upload.UploadUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

/**
 * 获取QQ昵称头像信息
 * @author Raxcl
 * @date 2022-01-07 19:50:43
 */
public class QqInfoUtils {
	private QqInfoUtils(){}
	private static final RestTemplate REST_TEMPLATE = new RestTemplate();
	// 原接口半失效，需要提供cookie才可使用，暂时替换为备用接口，感谢 苏晓晴 大佬友情提供
	private static final String QQ_NICKNAME_URL = "https://api.toubiec.cn/api/qqinfo_v4.php?qq={1}";
	private static final String QQ_AVATAR_URL = "https://q.qlogo.cn/g?b=qq&nk=%s&s=100";

	/**
	 * 获取QQ昵称
	 *
	 * @param qq qq
     */
	public static String getQqNickname(String qq) {
		QqResultVO qqResultVO = REST_TEMPLATE.getForObject(QQ_NICKNAME_URL, QqResultVO.class, qq);
        if (qqResultVO != null) {
            return new ObjectMapper().convertValue(qqResultVO.getData(), QqVO.class).getName();
        }
		return "nickname";
    }

	/**
	 * 从网络获取QQ头像数据
	 *
	 * @param qq qq
	 * @return ImageResource
	 */
	private static UploadUtils.ImageResource getImageResourceByQq(String qq) {
		return UploadUtils.getImageByRequest(String.format(QQ_AVATAR_URL, qq));
	}

	/**
	 * 获取QQ头像URL
	 *
	 * @param qq qq
	 * @return String
	 */
	public static String getQqAvatarUrl(String qq) throws Exception {
		return UploadUtils.upload(getImageResourceByQq(qq));
	}

	public static boolean isQqNumber(String nickname) {
		return nickname.matches("^[1-9][0-9]{4,10}$");
	}
}