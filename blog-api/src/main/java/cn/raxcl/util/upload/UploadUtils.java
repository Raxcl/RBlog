package cn.raxcl.util.upload;

import cn.raxcl.constant.UploadConstants;
import cn.raxcl.exception.BadRequestException;
import cn.raxcl.util.upload.channel.ChannelFactory;
import cn.raxcl.util.upload.channel.FileUploadChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 图片下载再保存工具类
 *
 * @author Raxcl
 * @date 2022/3/15 18:24
 */
@Component
@DependsOn("springContextUtils")
public class UploadUtils {
	private static RestTemplate restTemplate;

	private static FileUploadChannel uploadChannel;

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		UploadUtils.restTemplate = restTemplate;
	}

	@Value("${upload.channel}")
	public void setNotifyChannel(String channelName) {
		UploadUtils.uploadChannel = ChannelFactory.getChannel(channelName);
	}

	@AllArgsConstructor
	@Getter
	public static class ImageResource {
		byte[] data;
		//图片拓展名 jpg png
		String type;
	}

	/**
	 * 通过指定方式存储图片
	 *
	 * @param image 需要保存的图片
	 */
	public static String upload(ImageResource image) throws Exception {
		return uploadChannel.upload(image);
	}

	/**
	 * 从网络获取图片数据
	 *
	 * @param url 图片URL
	 * @return ImageResource
	 */
	public static ImageResource getImageByRequest(String url) {
		ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);
		//todo
		if (UploadConstants.IMAGE.equals(responseEntity.getHeaders().getContentType().getType())) {
			return new ImageResource(responseEntity.getBody(), responseEntity.getHeaders().getContentType().getSubtype());
		}
		throw new BadRequestException("response contentType unlike image");
	}
}
