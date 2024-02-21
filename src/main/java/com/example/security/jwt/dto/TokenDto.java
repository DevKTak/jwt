package com.example.security.jwt.dto;

import lombok.Builder;

public record TokenDto(
	String accessToken,
	String refreshToken) {
	@Builder
	public TokenDto {
	}
}
