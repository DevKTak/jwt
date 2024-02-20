package com.example.security.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
	ROLE_USER("USER"),
	ROLE_ADMIN("ADMIN");

	private final String label;
}
