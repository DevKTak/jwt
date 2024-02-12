package com.example.security.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // PK

	private String email; // 아이디

	private String password;

	@Enumerated(value = EnumType.STRING)
	private Role role = Role.USER;

	@Builder
	public User(String email, String password, Role role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}
}
