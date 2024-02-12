package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 시큐리티야 지금부터 너는 내 설정 따른다.
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home", "join").permitAll() // 이 URL 요청은 무조건 허가
				.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/admin/**").hasAnyRole("ADMIN")
				.anyRequest().authenticated() // 그 외 요청은 무조건 인증을 받아야 한다.
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll())
			// .formLogin().disable() // 너가 만들어주는 폼 로그인 안쓰고싶어
			.logout(LogoutConfigurer::permitAll);

		return http.build();
	}

	@Bean
	// 시큐리가 모든 유저를 알지 못하기 때문에 유저들의 인터페이스 UserDetails를 마련해 둔 것
	// 사이트마다 아이디를 id, email, phoneNumber 로도 하며 패스워드는 pw, pwd 등 통일되지 않았기에 인터페이스를 제공
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
			.username("tak")
			.password("123")
			.roles("ADMIN") // 'ROLE_' 프리픽스는 시큐리티에서 붙여줌
			.build();

		return new InMemoryUserDetailsManager(user);
	}
}
