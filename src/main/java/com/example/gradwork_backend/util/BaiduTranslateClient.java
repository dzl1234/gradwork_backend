package com.example.gradwork_backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
@Component
public class BaiduTranslateClient {

    @Value("${baidu.translate.app-id}")
    private String appId;

    @Value("${baidu.translate.secret-key}")
    private String secretKey;

    public String translateToEnglish(String text) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = md5(appId + text + salt + secretKey);

        // 构建请求参数
        String url = "http://api.fanyi.baidu.com/api/trans/vip/translate" +
                "?q=" + URLEncoder.encode(text, StandardCharsets.UTF_8.name()) +
                "&from=zh&to=en" +
                "&appid=" + appId +
                "&salt=" + salt +
                "&sign=" + sign;

        // 发送 HTTP 请求
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // 读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        // 解析 JSON 响应
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.readValue(response.toString(), Map.class);
        List<Map<String, String>> transResult = (List<Map<String, String>>) result.get("trans_result");
        if (transResult != null && !transResult.isEmpty()) {
            return transResult.get(0).get("dst");
        }
        throw new RuntimeException("Translation failed: " + response.toString());
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 error", e);
        }
    }
}
