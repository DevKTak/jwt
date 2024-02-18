package com.example.security.user.dto.request;

import com.example.security.user.Role;
import com.example.security.user.User;

public record JoinUserRequest(
	String email,
	String password,
	Role role
) {
	public User toEntity(String encodedPassword) {
		return User.builder()
			.email(email)
			.password(encodedPassword)
			.role(role)
			.build();
	}
}
