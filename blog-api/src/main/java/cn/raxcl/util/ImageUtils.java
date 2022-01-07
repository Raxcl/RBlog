package cn.raxcl.util;

import cn.raxcl.constant.CommonConstant;
import cn.raxcl.exception.NotFoundException;
import cn.raxcl.exception.PersistenceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import cn.raxcl.exception.BadRequestException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/**
 * @Description: 图片下载保存工具类
 * @author Raxcl
 * @date 2022-01-05 14:46:10
 */
@Component
public class ImageUtils {
	private ImageUtils(){}

	private static final RestTemplate REST_TEMPLATE = new RestTemplate();

	@AllArgsConstructor
	@Getter
	static class ImageResource {
		byte[] data;
		//图片拓展名 jpg png
		String type;
	}

	public static ImageResource getImageByRequest(String url) {
		ResponseEntity<byte[]> responseEntity = REST_TEMPLATE.getForEntity(url, byte[].class);
		MediaType contentType = responseEntity.getHeaders().getContentType();
		if(contentType == null){
			throw new NotFoundException("getImageByRequest is null---ImageUtils.class");
		}
		if (CommonConstant.IMAGE.equals(contentType.getType())) {
			return new ImageResource(responseEntity.getBody(), contentType.getSubtype());
		}
		throw new BadRequestException("response contentType unlike image");
	}

	public static String saveImage(ImageResource image, String serverUploadPath, String serverUrl) throws IOException {
		File folder = new File(serverUploadPath);
		if (!folder.exists()) {
			//TODO
			folder.mkdirs();
		}
		String fileName = UUID.randomUUID() + "." + image.getType();

		try (FileOutputStream fileOutputStream = new FileOutputStream(serverUploadPath + fileName)) {
			fileOutputStream.write(image.getData());
		} catch (IOException e) {
			throw new PersistenceException("saveImage is error---ImageUtils.class");
		}
		return serverUrl + "/image/" + fileName;
	}

	public static String push2Github(ImageResource image, String githubToken, String githubUsername, String githubRepos, String githubReposPath) {
		String fileName = UUID.randomUUID() + "." + image.getType();
		String url = String.format(CommonConstant.GITHUB_UPLOAD_API, githubUsername, githubRepos, githubReposPath, fileName);
		String imgBase64 = Base64.getEncoder().encodeToString(image.getData());

		HttpHeaders headers = new HttpHeaders();
		headers.put("Authorization", Collections.singletonList("token " + githubToken));

		HashMap<String, String> body = new HashMap<>();
		body.put("message", "Add files via RBlog");
		body.put("content", imgBase64);
		//TODO
		HttpEntity httpEntity = new HttpEntity(body, headers);
		REST_TEMPLATE.put(url, httpEntity);

		return String.format(CommonConstant.CDN_URL4_GITHUB, githubUsername, githubRepos, githubReposPath, fileName);
	}
}
