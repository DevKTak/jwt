package com.example.security.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		String token = jwtTokenProvider.resolveToken((HttpServletRequest)request); // 토큰 꺼냄

		if (token != null) {
			// 토큰 검증 = 토큰을 보고, 인증된 객체 맞아?
			// 필터에서 프로바이더에게 인증된 사용자인지 확인 요청
			Authentication authentication = jwtTokenProvider.getAuthentication(token);

			// 시큐리티는 인증이 된 사용자만 허락해줄꺼니까, 얘 인증 되었어! 라고 필터에서 대신 말해주는 것
			SecurityContextHolder.getContext().setAuthentication(authentication); // 응 맞아
		}
		chain.doFilter(request, response);
	}
}
