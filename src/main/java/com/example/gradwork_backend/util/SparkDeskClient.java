package com.example.gradwork_backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Component
public class SparkDeskClient {

    @Value("${sparkdesk.ai.app-id}")
    private String appId;

    @Value("${sparkdesk.ai.api-password}")
    private String apiPassword; // 使用 APIPassword 替代 apiKey 和 apiSecret

    @Value("${sparkdesk.ai.url}")
    private String apiUrl;

    public String ask(String question) throws Exception {
        // 检查 API_PASSWORD 是否已替换
        if ("YOUR_APIPASSWORD".equals(apiPassword)) {
            throw new IllegalStateException("请在 application.yml 中配置 sparkdesk.ai.api-password");
        }

        // 使用正确的模型 ID（参考 SparkAITestFinal.java）
        String modelId = "4.0Ultra"; // Spark 4.0 Ultra

        // 构建请求体
        String requestBody = "{" +
                "\"model\": \"" + modelId + "\"," +
                "\"messages\": [" +
                "{" +
                "\"role\": \"user\"," +
                "\"content\": \"" + escapeJson(question) + "\"" +
                "}" +
                "]," +
                "\"temperature\": 0.7," +
                "\"top_k\": 4," +
                "\"max_tokens\": 2048" +
                "}";

        // 准备 URL 和连接
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // 设置请求头 - 使用 Bearer Token 认证
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiPassword);

        // 发送请求体
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 读取响应
        int statusCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (Scanner responseScanner = new Scanner(
                (statusCode >= 200 && statusCode < 300)
                        ? connection.getInputStream()
                        : connection.getErrorStream(),
                StandardCharsets.UTF_8.name())) {
            responseScanner.useDelimiter("\\A");
            if (responseScanner.hasNext()) {
                response.append(responseScanner.next());
            }
        } finally {
            connection.disconnect();
        }

        // 检查状态码
        if (statusCode != 200) {
            throw new RuntimeException("星火 AI API 调用失败，状态码: " + statusCode + ", 响应: " + response.toString());
        }

        // 解析响应（提取 choices[0].message.content）
        // 这里简化处理，实际中建议使用 Jackson 或其他 JSON 库解析
        String responseStr = response.toString();
        int contentStart = responseStr.indexOf("\"content\":\"") + 11;
        int contentEnd = responseStr.indexOf("\"", contentStart);
        if (contentStart == -1 || contentEnd == -1) {
            throw new RuntimeException("无法解析星火 AI 响应: " + responseStr);
        }
        return responseStr.substring(contentStart, contentEnd);
    }

    // 转义 JSON 字符串中的特殊字符（参考 SparkAITestFinal.java）
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
