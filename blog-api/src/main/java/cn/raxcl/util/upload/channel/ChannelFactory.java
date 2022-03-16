package cn.raxcl.util.upload.channel;

import cn.raxcl.constant.UploadConstants;
import cn.raxcl.util.common.SpringContextUtils;

/**
 * 文件上传方式
 *
 * @author raxcl
 * @date 2022-03-15 23:47:06
 */
public class ChannelFactory {
	/**
	 * 创建文件上传方式
	 *
	 * @param channelName 方式名称
	 * @return FileUploadChannel
	 */
	public static FileUploadChannel getChannel(String channelName) {
		if (UploadConstants.LOCAL.equalsIgnoreCase(channelName)) {
			return SpringContextUtils.getBean("localChannel", FileUploadChannel.class);
		} else if (UploadConstants.GITHUB.equalsIgnoreCase(channelName)) {
			return SpringContextUtils.getBean("githubChannel", FileUploadChannel.class);
		}
		throw new RuntimeException("Unsupported value in [application.properties]: [upload.channel]");
	}
}
