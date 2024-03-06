package com.example.security.user;

import java.util.NoSuchElementException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.jwt.JwtTokenProvider;
import com.example.security.jwt.domain.RefreshToken;
import com.example.security.jwt.dto.TokenDto;
import com.example.security.jwt.repository.RefreshTokenRepository;
import com.example.security.user.dto.request.JoinUserRequest;
import com.example.security.user.dto.request.LoginUserRequest;
import com.example.security.user.dto.response.JoinUserResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 서비스 계층은 로직 입장에서 메소드명 작명
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthenticationManager authenticationManager;

	@Transactional
	public JoinUserResponse save(JoinUserRequest req) {
		User user = userRepository.save(req.toEntity(passwordEncoder.encode(req.password())));

		return JoinUserResponse.of(user);
	}

	@Transactional
	public void login(LoginUserRequest req, HttpServletResponse response) {

		// 아이디 검사
		User user = userRepository.findByEmail(req.email())
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 계정 정보입니다."));

		// 패스워드 검사
		// if (!passwordEncoder.matches(req.password(), user.getPassword())) {
		// 	throw new IllegalArgumentException("패스워드 오류");
		// }

		try {
			// authenticationManager 통해 id, password 검증
			// authenticate() 메소드 실행 시 UserDetailService.java 에서 만든 loadUserByUsername() 메소드 호출됨
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user, req.password())
			);

			// Access, Refresh 토큰 생성
			TokenDto tokenDto = jwtTokenProvider.createAllToken(authentication.getName(),
				authentication.getAuthorities(), user.getId());

			String newRefreshToken = tokenDto.refreshToken();

			// Refresh Token 있는지 확인
			refreshTokenRepository.findByEmail(user.getEmail())
				.ifPresentOrElse(
					(refreshToken) -> refreshToken.change(newRefreshToken), // 존재한다면 Refresh Token 업데이트
					() -> refreshTokenRepository.save(new RefreshToken(user.getEmail(), newRefreshToken))
					// 존재하지 않다면 Refresh Token 저장
				);

			// response 헤더에 Access Token / Refresh Token 넣음
			setHeader(response, tokenDto);
		} catch (Exception e) {
			log.error("검증 실패");

			throw new NoSuchElementException(e.getMessage());
		}
	}

	private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
		response.addHeader(JwtTokenProvider.ACCESS_TOKEN, tokenDto.accessToken());
		response.addHeader(JwtTokenProvider.REFRESH_TOKEN, tokenDto.refreshToken());
	}

	public User findById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
	}
}
