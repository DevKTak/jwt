package com.example.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

public class JwtTokenProvider {

	private String secretKey = "aaaabbbbccccddddd";
	private final long tokenValidTime = 30 * 60 * 1000L; // 30min

	@Autowired
	private UserDetailsService userDetailsService;

	@PostConstruct
	public void init() {
		// secretKey를 바이트로 바꾼뒤 다시 스트링으로 Base64 인코딩
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	public String createToken(String username, Collection<? extends GrantedAuthority> roles) {
		Claims claims = Jwts.claims().setSubject(username); //그냥 문법임
		claims.put("roles", roles);
		Date now = new Date();

		// 토큰 생성
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now) // 언제 발행이 되었는지
			.setExpiration(new Date(now.getTime() + tokenValidTime)) // 언제 만료될건지
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("Authorization");
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));

		// 인증된 사용자라는 것을 정보와 함께 리턴
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUsername(String token) {
		// 토큰 쪼개서 username 꺼냄
		return Jwts.parser().setSigningKey(secretKey)
			.parseClaimsJws(token).getBody().getSubject(); //그냥 문법임
	}
}
