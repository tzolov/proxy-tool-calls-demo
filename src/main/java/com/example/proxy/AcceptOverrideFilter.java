package com.example.proxy;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AcceptOverrideFilter extends OncePerRequestFilter {

	@Override
	@SuppressWarnings("null")
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		filterChain.doFilter(new HeaderMapRequestWrapper((HttpServletRequest) request), response);
	}

	static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

		private static final String ACCEPT = "Accept";

        public HeaderMapRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getHeader(String name) {
			if (ACCEPT.equalsIgnoreCase(name)) {
				return MediaType.TEXT_EVENT_STREAM_VALUE;
			}
			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			List<String> names = Collections.list(super.getHeaderNames());
			names.add(ACCEPT);
			return Collections.enumeration(names);
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			List<String> values = Collections.list(super.getHeaders(name));
			if (ACCEPT.equalsIgnoreCase(name)) {
				values.add(MediaType.TEXT_EVENT_STREAM_VALUE);
			}
			return Collections.enumeration(values);
		}
	}

}