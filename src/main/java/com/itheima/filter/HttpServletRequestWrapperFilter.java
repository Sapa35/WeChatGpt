package com.itheima.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "httpServletRequestWrapperFilter", urlPatterns = {"/*"})
@Order(1)
@Slf4j
public class HttpServletRequestWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        log.info("进入HttpServletRequestWrapperFilter");
        ServletRequest requestWrapper = null;

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            log.info("doFilter ->>> 拦截到uri:  "+((HttpServletRequest) request).getRequestURI());
            //遇到post方法才对request进行包装
            String methodType = httpRequest.getMethod();
            if ("POST".equals(methodType)) {
                log.info("doFilter ->>> getMethod:  "+((HttpServletRequest) request).getMethod());
                requestWrapper = new BodyReaderHttpServletRequestWrapper(
                        (HttpServletRequest) request);
            }
        }

        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

}
