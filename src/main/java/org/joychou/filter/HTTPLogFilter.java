package org.joychou.filter;

import org.joychou.util.DiyLogUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "logFilter", urlPatterns = "/*")
public class HTTPLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (DiyLogUtils.skipReq(request)) {
            // 继续执行请求链
            chain.doFilter(request, response);
        } else {
            // Wrapper 封装 Request 和 Response
            ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

            chain.doFilter(cachingRequest, cachingResponse);

            DiyLogUtils.logRequest(cachingRequest);
            DiyLogUtils.logResponse(cachingResponse);

            // 把缓存的响应数据，响应给客户端
            cachingResponse.copyBodyToResponse();
        }

    }

    @Override
    public void destroy() {

    }

}
