/**
 * @title: JsonUtils
 * @package: com.weiju.application.utils
 * @project: weiju-application
 * @description:
 * @author: cyl
 * @company eVideo
 * @date: 2017/12/27 11:05
 * @version V1.0
 */
package com.starnet.ipcmonitorcloud.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @className: JsonUtils
 * @description:
 * @author: cyl
 * @date: 2017/12/27 11:05
 * @mark:
 */
public class JsonUtils {

    public static <T> String toJson(T t) throws JsonProcessingException {
        if (t == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(t);
    }

    public static <T> T fromJson(String content, Class<T> clz) throws IOException {
        if (content == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T t = mapper.readValue(content, clz);
        return t;
    }
}
