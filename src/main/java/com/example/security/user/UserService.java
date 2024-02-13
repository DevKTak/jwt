package com.example.security.user;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.user.dto.JoinUserRequest;
import com.example.security.user.dto.JoinUserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 서비스 계층은 로직 입장에서 메소드명 작명
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public JoinUserResponse save(JoinUserRequest req) {
		User user = userRepository.save(req.toEntity(passwordEncoder.encode(req.password())));

		return JoinUserResponse.of(user);
	}

	// WebSecurityConfig.java -> UserService,java로 이사
	// 인증하는 시점(로그인)에 loadUserByUsername 메소드 호출됨
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// DB에 너 있는거 확인했으니까, 들여보내줄게

		// UserDetails는 User를 추상화한 것
		return userRepository.findByEmail(username).orElseThrow(); // userRepository에 회원가입(저장) 되어있는 객체를 꺼내주면 됩니다.
	}
}
