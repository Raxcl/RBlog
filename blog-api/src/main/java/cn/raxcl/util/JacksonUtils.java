package cn.raxcl.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: Jackson Object Mapper
 * @author Raxcl
 * @date 2022-01-07 19:50:00
 */
public class JacksonUtils {
	private JacksonUtils(){}

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static String writeValueAsString(Object value) {
		try {
			return OBJECT_MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static <T> T readValue(String content, Class<T> valueType) {
		try {
			return OBJECT_MAPPER.readValue(content, valueType);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T readValue(InputStream src, Class<T> valueType) {
		try {
			return OBJECT_MAPPER.readValue(src, valueType);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
		return OBJECT_MAPPER.convertValue(fromValue, toValueType);
	}
}
