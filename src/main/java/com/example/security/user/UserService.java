package com.example.security.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.user.dto.JoinUserRequest;
import com.example.security.user.dto.JoinUserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 서비스 계층은 로직 입장에서 메소드명 작명
public class UserService {

	private final UserRepository userRepository;

	@Transactional
	public JoinUserResponse save(JoinUserRequest req) {
		User user = userRepository.save(req.toEntity());

		return JoinUserResponse.of(user);
	}
}
