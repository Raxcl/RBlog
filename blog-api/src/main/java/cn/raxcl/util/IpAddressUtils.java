package cn.raxcl.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * ip记录
 * @author Raxcl
 * @date 2022-01-11 11:10:26
 */
@Slf4j
@Component
public class IpAddressUtils {
	private static final String UNKNOWN = "unknown";
	private static final String LOCAL_IP = "127.0.0.1";
	private static final String INNER_HOST = "0:0:0:0:0:0:0:1";
	/**
	 * 在Nginx等代理之后获取用户真实IP地址
	 *
	 * @param request request
	 * @return String
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = fixIp(request);
		}
		return StringUtils.substringBefore(ip, ",");
	}

	private static String fixIp(HttpServletRequest request) {
		String tempIp = request.getRemoteAddr();
		if (LOCAL_IP.equals(tempIp) || INNER_HOST.equals(tempIp)) {
			//根据网卡取本机配置的IP
			InetAddress inet = null;
			try {
				inet = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				log.error("getIpAddress exception:", e);
			}
			tempIp = Objects.requireNonNull(inet).getHostAddress();
		}
		return tempIp;
	}

	private static Searcher searcher;
	private static Method method;

	/**
	 * 在服务启动时加载 ip2region.db 到内存中
	 * 解决打包jar后找不到 ip2region.db 的问题
	 */
	@PostConstruct
	private void initIp2regionResource() throws IOException, NoSuchMethodException {
		// 1、从 dbPath 加载整个 xdb 到内存。
		InputStream inputStream = new ClassPathResource("/ipdb/ip2region.xdb").getInputStream();
		//将 ip2region.db 转为 ByteArray
		byte[] cBuff = FileCopyUtils.copyToByteArray(inputStream);
		// 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
		searcher = new Searcher(null, null, cBuff);
		//二进制方式初始化 DBSearcher，需要使用基于内存的查找算法 memorySearch
		method = searcher.getClass().getMethod("search", String.class);
	}

	/**
	 * 根据ip从 ip2region.db 中获取地理位置
	 *
	 * @param ip ip
	 * @return String
	 */
	public static String getCityInfo(String ip) {
		// 3、查询
		try {
			String region = (String) method.invoke(searcher, ip);
			if (!StringUtils.isEmpty(region)) {
				region = region.replace("|0", "");
				region = region.replace("0|", "");
			}
			return region;
		} catch (Exception e) {
			System.out.printf("{err: %s, ioCount: %d}\n", e, searcher.getIOCount());
			log.error("getCityInfo exception:", e);
		}
		return "";
	}

//	public static void main(String[] args) throws Exception {
//		IpAddressUtils ipAddressUtils = new IpAddressUtils();
//		ipAddressUtils.initIp2regionResource();
//		String cityInfo = IpAddressUtils.getCityInfo("14.215.177.39");
//		System.out.println(cityInfo);
//	}
}