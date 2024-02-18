package com.example.security.user.dto.request;

public record LoginUserRequest(
	String username,
	String password
) {
}
