package org.joychou.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class DiyLogUtils {

    private static final Logger logger = LoggerFactory.getLogger(DiyLogUtils.class);

    public static boolean skipReq(HttpServletRequest request) throws IOException {
        String url = request.getRequestURI();
        String contentType = request.getHeader("Content-Type");

        if (url.endsWith(".css")
                || url.endsWith(".png")
                || url.endsWith(".jpg")
                || url.endsWith(".jpeg")
                || url.endsWith(".svg")
                || url.endsWith(".bmp")
                || url.endsWith(".ico")
                || url.endsWith(".gif")
                || url.endsWith(".mp3")
                || url.endsWith(".mp4")
                || url.endsWith(".flv")
                || url.endsWith(".aac")
                || url.endsWith(".pdf")
                || url.endsWith(".doc")
                || url.endsWith(".docx")
                || url.endsWith(".pptx")
                || url.endsWith(".ppt")
                || url.endsWith(".woff")
                || url.endsWith(".woff2")
        ) { // 过滤静态资源
            return true;
        }
        if (contentType != null) {
            if (contentType.matches("image/*")
                    || contentType.matches("audio/*")
                    || contentType.matches("video/*")
                    || contentType.matches(".*octet-stream*")
                    || contentType.matches("application/ogg")
                    || contentType.matches("application/pdf")
                    || contentType.matches("application/msword")
                    || contentType.matches("application/x-ppt")
                    || contentType.matches("video/avi")
                    || contentType.matches("application/x-ico")
                    || contentType.matches(".*zip")
            ) { // 过滤contentType
                return true;
            }
        }

        return false;
    }

    /**
     * 打印请求信息
     *
     * @param request HttpServletRequest 对象
     * @throws IOException
     */
    public static void logRequest(ContentCachingRequestWrapper request) throws IOException {
        // 在请求完成后记录请求、响应日志
        String uri = request.getRequestURI();
        String url = request.getRequestURL().toString();
        String query = request.getQueryString();

        logger.warn("=== 请求 start ===");
        logger.warn("请求来源IP: {}", getClientIP(request));
        logger.warn("请求URL: {}{}", url, (query != null ? "?" + query : ""));
        logger.warn("请求方法: {}", request.getMethod());
        logger.warn("请求URI: {}", uri);

        // 打印 Headers
        Enumeration<String> headerNames = request.getHeaderNames();
        logger.warn("请求header : ");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.warn(" {}: {}", headerName, request.getHeader(headerName));
        }

        // // 请求体
        // byte[] requestContent = request.getContentAsByteArray();
        // logger.info("Request => {} {} {}", method, uri, new String(requestContent));

        // 打印请求体（注意：只能读取一次）
        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
            // 请求体
            byte[] requestContent = request.getContentAsByteArray();
            logger.info("请求body : \n{}", requestContent);
        } else {
            // 打印参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            logger.warn("请求参数 query : ");
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                logger.warn(" {}={}", entry.getKey(), String.join(", ", entry.getValue()));
            }
        }

        logger.warn("=== 请求 end ===");
        logger.warn("");
    }

    /**
     * 打印响应信息
     *
     * @param response HttpServletResponse 对象
     * @throws IOException
     */
    public static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        logger.warn("=== 响应信息 start ===");

        // 响应状态
        int status = response.getStatus();
        logger.warn("响应状态码 : " + status);

        // 打印响应头
        Map<String, String> headers = new HashMap<>();
        for (String headerName : response.getHeaderNames()) {
            headers.put(headerName, String.join(", ", response.getHeaders(headerName)));
        }
        logger.warn("响应header : ");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            logger.warn(" " + entry.getKey() + ": " + entry.getValue());
        }

        // 响应体
        byte[] responseContent = response.getContentAsByteArray();
        logger.info("响应body : ");
        logger.info("\n{}", new String(responseContent));
        // logger.info("Response <= {} {}", status, new String(responseContent));

        logger.warn("=== 响应信息 end ===");
        logger.warn("");
    }


    public static String getClientIP(HttpServletRequest request) {
        // 优先从 X-Forwarded-For 头中获取 IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 如果 X-Forwarded-For 不存在，则尝试 Proxy-Client-IP（适用于某些代理）
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 如果 Proxy-Client-IP 也不存在，则尝试 WL-Proxy-Client-IP（WebLogic 代理）
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 如果以上头都不存在，则尝试获取原始 IP
            ip = request.getRemoteAddr();
        }

        // // X-Forwarded-For 可能包含多个 IP，以逗号分隔，取第一个
        // if (ip != null && ip.contains(",")) {
        //     ip = ip.split(",")[0].trim();
        // }

        return ip;
    }


    public static Map<String, String> getReqHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        return headerMap;
    }


    public static Map<String, String> getRespHeaders(HttpServletResponse response) {
        Map<String, String> headerMap = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            String headerValue = response.getHeader(headerName);
            headerMap.put(headerName, headerValue);
        }
        return headerMap;
    }


}
