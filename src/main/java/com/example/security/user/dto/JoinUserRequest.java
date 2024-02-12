package com.example.security.user.dto;

import com.example.security.user.Role;
import com.example.security.user.User;

public record JoinUserRequest(
	String email,
	String password,
	Role role
) {
	public User toEntity() {
		return User.builder()
			.email(email)
			.password(password)
			.role(role)
			.build();
	}
}
