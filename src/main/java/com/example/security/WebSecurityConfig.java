package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.security.jwt.JwtAuthenticationFilter;
import com.example.security.jwt.JwtProperties;
import com.example.security.jwt.JwtTokenProvider;
import com.example.security.user.Role;

import lombok.RequiredArgsConstructor;

/**
 * 1) 인증(Authentication): 로그인
 * 		# 쿠키 vs 세션 vs JWT
 * 		(1) 쿠키: 보안문제
 * 		(2) 세션: 금고
 * 		(3) JWT "암호화": 유저 로그인
 * 						  -> 로그인 성공 JWT(나 언제 로그인했고 나 이런사람이고 너가 인증해줬잖아) 생성 & 회신
 * 						  -> 유저는 요청할 때마다 JWT를 같이 줌
 *						  -> JWT를 복호화(decoding)
 * 	2) 인가(Authorization): 권한
 */

@Configuration
@EnableWebSecurity // 시큐리티야 지금부터 너는 내 설정 따른다.
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final CorsConfigurationSource corsConfigurationSource;
	private final UserDetailsService userDetailsService;
	private final JwtProperties jwtProperties;

	@Bean
	public JwtTokenProvider jwtTokenProvider() {
		return new JwtTokenProvider(userDetailsService, jwtProperties);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home", "/join", "/login").permitAll() // 이 URL 요청은 무조건 허가
				// .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
				// .requestMatchers("/admin/**").hasAnyRole("ADMIN")
				.requestMatchers("/admin/**").hasAnyRole(Role.ROLE_ADMIN.getLabel())
				.anyRequest().authenticated() // 그 외 요청은 무조건 인증을 받아야 한다.
			)
			// .formLogin((form) -> form
			// 	.usernameParameter("email")
			// 	.passwordParameter("password"))
			// .loginPage("/login")
			// .loginProcessingUrl("/login"))
			.csrf(AbstractHttpConfigurer::disable) // 백 작업시 필요없고 403 포비든 에러, 프론트가 붙으면 csrf() 켜줘야한다.
			.cors(config -> config.configurationSource(corsConfigurationSource))
			.httpBasic(AbstractHttpConfigurer::disable) // login form redirect 필요 X (rest api)
			.formLogin(AbstractHttpConfigurer::disable) // 너가 만들어주는 폼 로그인 안쓰고싶어
			.rememberMe(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(
				config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // jwt token으로 인증하므로 세션 필요 X
			// .logout(LogoutConfigurer::permitAll) // 모든 사용자에게 로그아웃 권한 허용

			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider()),
				UsernamePasswordAuthenticationFilter.class); // 1번 파라미터 구현체를 2번 파라미터 타입으로 사용

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration authenticationConfiguration
	) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
/*	@Bean
	// 시큐리티가 모든 유저를 알지 못하기 때문에 유저들의 인터페이스 UserDetails를 마련해 둔 것
	// 사이트마다 아이디를 id, email, phoneNumber 로도 하며 패스워드는 pw, pwd 등 통일되지 않았기에 인터페이스를 제공
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
			.username("tak")
			.password("123")
			.roles("ADMIN") // 'ROLE_' 프리픽스는 시큐리티에서 붙여줌
			.build();

		return new InMemoryUserDetailsManager(user);
	}*/
}
