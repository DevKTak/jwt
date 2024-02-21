package com.example.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.security.jwt.dto.TokenDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

	private final UserDetailsService userDetailsService;

	private final JwtProperties jwtProperties;

	private String secretKey;

	// private static final long ACCESS_TOKEN_VALID_TIME = 30 * 60 * 1000L; // 30min
	private static final long ACCESS_TOKEN_VALID_TIME = Duration.ofMinutes(30).toMillis(); // 30min
	// private static final long REFRESH_TOKEN_VALID_TIME = 3 * 24 * 60 * 60 * 1000L; // 3 day
	private static final long REFRESH_TOKEN_VALID_TIME = Duration.ofDays(3).toMillis(); // 3 day
	public static final String ACCESS_TOKEN = "Access_Token";
	public static final String REFRESH_TOKEN = "Refresh_Token";

	@PostConstruct
	public void init() {
		// secretKey를 바이트로 바꾼뒤 다시 스트링으로 Base64 인코딩
		secretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
	}

	public TokenDto createAllToken(String email, Collection<? extends GrantedAuthority> roles, Long id) {
		return TokenDto.builder()
			.accessToken(createToken(email, roles, id, ACCESS_TOKEN_VALID_TIME))
			.refreshToken(createToken(email, roles, id, REFRESH_TOKEN_VALID_TIME))
			.build();
	}

	// JWT 토큰 생성
	private String createToken(String email, Collection<? extends GrantedAuthority> roles, Long id,
		long tokenValidTime) {
		Claims claims = Jwts.claims().setSubject(email); // 유저 이메일
		claims.put("roles", roles);
		claims.put("iss", jwtProperties.getIssuer()); // iss: 이슈어(발급자)
		Date now = new Date();

		// 토큰 생성
		return Jwts.builder()
			.setClaims(claims)
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ: JWT
			.setIssuedAt(now) // 언제 발행이 되었는지(현재 시간)
			.setExpiration(new Date(now.getTime() + tokenValidTime)) // 언제 만료될건지
			.signWith(SignatureAlgorithm.HS256, secretKey) // 서명: 비밀값과 함께 해시값을 HS256 방식으로 암호화(보안을 더 강화하려면 HS512)
			.claim("id", id) // 클레임 id: 유저 ID
			.compact();
	}

	// Header에서 토큰 꺼내기
	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("Authorization");
	}

	/** 토큰 기반으로 인증 정보를 가져옴 **/
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));

		// 인증된 사용자라는 것을 정보와 함께 리턴
		return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
			userDetails.getAuthorities());
	}

	/** 토큰 기반으로 username을 가져옴 **/
	public String getUsername(String token) {
		return Jwts.parser()
			.setSigningKey(secretKey) // 시크릿키로 복호화
			.parseClaimsJws(token)
			.getBody().getSubject(); // 그냥 문법임
	}

	/** Access 토큰 유효성 검증 **/
	public boolean validToken(String token) {
		try {
			Jws<Claims> claimsJws = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token);
			return true;
			// return !claimsJws.getBody().getExpiration().before(new Date());
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			log.error("Invalid JWT signature, 유효하지 않는 JWT 서명");
			throw new NoSuchElementException(e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token, 만료된 JWT Token");
			throw new IllegalArgumentException(e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰");
			throw new NoSuchElementException(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims is empty, 잘못된 JWT 토큰");
			throw new NoSuchElementException(e.getMessage());
		} catch (JwtException e) {
			log.error(e.getMessage());
			throw new NoSuchElementException(e.getMessage());
		} catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
			log.error(e.getMessage());
			throw new NoSuchElementException(e.getMessage());
		}
	}
}
