package com.example.security.user.dto;

import com.example.security.user.Role;
import com.example.security.user.User;

import lombok.Builder;

public record JoinUserResponse(
	Long id,
	String email,
	Role role
) {
	@Builder
	public JoinUserResponse {
	}

	public static JoinUserResponse of(User user) {
		return JoinUserResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.role(user.getRole())
			.build();
	}
}
