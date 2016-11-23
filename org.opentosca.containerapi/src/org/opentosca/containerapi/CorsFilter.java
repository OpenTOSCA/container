package org.opentosca.containerapi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		if (req.getHeader("Origin") != null) {
			res.addHeader("Access-Control-Allow-Origin", "*");
			res.addHeader("Access-Control-Expose-Headers", "Origin, Content-Type, X-Cache-Date, Location");
		}
		
		if ("OPTIONS".equals(req.getMethod())) {
			res.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
			res.addHeader("Access-Control-Allow-Headers", "Origin, Content-Type, X-Cache-Date, Location");
			res.addHeader("Access-Control-Max-Age", "-1");
		}
		chain.doFilter(req, res);
	}
	
	@Override
	public void destroy() {
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}