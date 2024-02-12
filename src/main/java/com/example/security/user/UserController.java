package com.example.security.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.user.dto.JoinUserRequest;
import com.example.security.user.dto.JoinUserResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// 컨트롤러 계층은 사용자와 가깝기 때문에 사용자 언어로 메소드명 작명
public class UserController {

	private final UserService userService;

	@PostMapping("/join")
	public JoinUserResponse join(@RequestBody JoinUserRequest req) {
		return userService.save(req);
	}
}
