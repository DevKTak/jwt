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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
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

	// JWT 토큰 생성
	public String createToken(String email, Collection<? extends GrantedAuthority> roles, Long id) {
		Claims claims = Jwts.claims().setSubject(email); // 유저 이메일
		claims.put("roles", roles);
		claims.put("iss", "qkrrudxkr77@gmail.com"); // iss: 이슈어(발급자)
		Date now = new Date();

		// 토큰 생성
		return Jwts.builder()
			.setClaims(claims)
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ: JWT
			.setIssuedAt(now) // 언제 발행이 되었는지(현재 시간)
			.setExpiration(new Date(now.getTime() + tokenValidTime)) // 언제 만료될건지
			.signWith(SignatureAlgorithm.HS512, secretKey) // 서명: 비밀값과 함께 해시값을 HS512 방식으로 암호화
			.claim("id", id) // 클레임 id: 유저 ID
			.compact();
	}

	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("Authorization");
	}

	/** 토큰 기반으로 인증 정보를 가져옴 **/
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));

		// 인증된 사용자라는 것을 정보와 함께 리턴
		return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
	}

	/** 토큰 기반으로 username을 가져옴 **/
	public String getUsername(String token) {
		// 토큰 쪼개서 username 꺼냄
		return Jwts.parser()
			.setSigningKey(secretKey) // 시크릿키로 복호화
			.parseClaimsJws(token)
			.getBody().getSubject(); // 그냥 문법임
	}

	/** JWT 토큰 유효성 검증 **/
	public boolean validToken(String token) {
		try {
			Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token);

			return true;
		} catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
			return false;
		}
	}
}
