package com.example.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
@ConfigurationProperties("jwt")
public class JwtProperties {

	private String issuer;
	private String secretKey;
}
