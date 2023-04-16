package com.itheima.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

//请求过滤器
@WebFilter(filterName = "WorkToolRequestFilter",urlPatterns = "/*")
@Order(2)
@Slf4j
public class WorkToolRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("进入WorkToolRequestFilter");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(request instanceof BodyReaderHttpServletRequestWrapper){
            log.info("doFilter ->>> getBody:  "+((BodyReaderHttpServletRequestWrapper) request).getBodyStr());
        }
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
