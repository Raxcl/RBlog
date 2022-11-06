package cn.raxcl.util.upload.channel;

import cn.raxcl.config.properties.TxYunProperties;
import cn.raxcl.util.upload.UploadUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 腾讯云存储上传方式
 *
 * @author raxcl
 * @date 2022/11/06 09:59:09
 */
@Lazy
@Component
public class TxYunChannel implements FileUploadChannel {

	private final TxYunProperties txYunProperties;
	private final TransferManager transferManager;

	public TxYunChannel(TxYunProperties txYunProperties) {
		this.txYunProperties = txYunProperties;
		this.transferManager = createTransferManager();
	}

	@Override
	public String upload(UploadUtils.ImageResource image) throws Exception {
		String fileAbsolutePath = txYunProperties.getPath() + "/" + UUID.randomUUID() + "." + image.getType();
		// 获取流长度，分块上传
		long inputStreamLength = image.getData().length;
		InputStream inputStream = new ByteArrayInputStream(image.getData());
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(inputStreamLength);
		PutObjectRequest putObjectRequest = new PutObjectRequest(txYunProperties.getBucketName(), fileAbsolutePath, inputStream, objectMetadata);
		try {
			transferManager.upload(putObjectRequest);
		} catch (CosClientException e) {
			throw new RuntimeException("腾讯云上传失败");
		}
		return txYunProperties.getDomain() + fileAbsolutePath;
	}

	public TransferManager createTransferManager() {
		COSClient cosClient = createCosClient();
		ExecutorService threadPool = new ThreadPoolExecutor(16,
				16,
				0L,
				TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(3),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy());
		TransferManager transferManager = new TransferManager(cosClient, threadPool);
		TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
		transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024L);
		transferManagerConfiguration.setMinimumUploadPartSize(1024 * 1024);
		transferManager.setConfiguration(transferManagerConfiguration);
		return transferManager;
	}

	private COSClient createCosClient() {
		COSCredentials cred = new BasicCOSCredentials(txYunProperties.getSecretId(), txYunProperties.getSecretKey());
		ClientConfig clientConfig = new ClientConfig(new Region(txYunProperties.getRegion()));
		clientConfig.setHttpProtocol(HttpProtocol.https);
		return new COSClient(cred, clientConfig);
	}
}
