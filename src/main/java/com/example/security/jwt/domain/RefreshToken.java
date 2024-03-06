package com.example.security.jwt.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	@Column(length = 500)
	private String refreshToken;

	@Builder
	public RefreshToken(String email, String refreshToken) {
		this.email = email;
		this.refreshToken = refreshToken;
	}

	public RefreshToken change(String newRefreshToken) {
		this.refreshToken = newRefreshToken;

		return this;
	}
}
