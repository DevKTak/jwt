package com.example.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class UserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	// WebSecurityConfig.java -> UserService.java로 이사
	// 인증하는 시점(로그인)에 loadUserByUsername 메소드 호출됨
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 시큐리티가 인증할 때 DB에 너 있는거 확인했으니까, 들여보내줄게 할때 사용하는 메소드
		// 우리가 사용하는것이 아니라 시큐리티가 사용하는 메소드이므로 그 객체를 시큐리티한테 허가해주라고 주기만 하면됨
		// UserDetails는 User를 추상화한 것
		return userRepository.findByEmail(username)
			.orElseThrow(() -> new IllegalArgumentException(username)); // userRepository에 회원가입(저장) 되어있는 객체를 꺼내주면 됩니다.
	}
}