package com.example.security.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // PK

	private String email; // 아이디

	private String password;

	@Enumerated(value = EnumType.STRING)
	private Role role = Role.ROLE_USER;

	@Builder
	public User(String email, String password, Role role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}

	// getEmail()
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	// getRole()과 같은 역할
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// User가 가지고 있는 권한(Role)들
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role.name()));

		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() { // 만료된 계정 아니야?
		return true; // 응 아니야.
	}

	@Override
	public boolean isAccountNonLocked() { // 락 걸린 계정 아니야?
		return true; // 응 아니야.
	}

	@Override
	public boolean isCredentialsNonExpired() { // ex. 패스워드 30일 지난거아니야?
		return true; // 응 아니야.
	}

	@Override
	public boolean isEnabled() { // 이거 사용할 수 있는거야?
		return true; // 응 맞아~
	}
}
