package com.example.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.jwt.JwtTokenProvider;
import com.example.security.user.dto.request.JoinUserRequest;
import com.example.security.user.dto.request.LoginUserRequest;
import com.example.security.user.dto.response.JoinUserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 서비스 계층은 로직 입장에서 메소드명 작명
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public JoinUserResponse save(JoinUserRequest req) {
		User user = userRepository.save(req.toEntity(passwordEncoder.encode(req.password())));

		return JoinUserResponse.of(user);
	}

	public String login(LoginUserRequest req) {
		User user = userRepository.findByEmail(req.username()).orElseThrow();

		return jwtTokenProvider.createToken(user.getUsername(), user.getAuthorities());
	}
}
