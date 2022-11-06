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
		switch(channelName.toLowerCase()) {
			case UploadConstants.LOCAL:
				return SpringContextUtils.getBean(LocalChannel.class);
			case UploadConstants.GITHUB:
				return SpringContextUtils.getBean(GithubChannel.class);
			case UploadConstants.TXYUN:
				return SpringContextUtils.getBean(TxYunChannel.class);
			default:
				throw new RuntimeException("Unsupported value in [application.properties]: [upload.channel]");
		}
	}
}
