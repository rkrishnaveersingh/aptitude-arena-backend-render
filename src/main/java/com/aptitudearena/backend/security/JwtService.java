package com.aptitudearena.backend.security;

import com.aptitudearena.backend.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

	private static final Duration TOKEN_LIFETIME = Duration.ofHours(24);

	private final SecretKey signingKey;

	public JwtService(@Value("${jwt.secret}") String secret) {
		this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(AppUser user) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(user.getEmail())
				.claim("userId", user.getId().toString())
				.claim("role", user.getRole())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(TOKEN_LIFETIME)))
				.signWith(signingKey, Jwts.SIG.HS256)
				.compact();
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	public String extractUserId(String token) {
		return parseClaims(token).get("userId", String.class);
	}

	public String extractRole(String token) {
		return parseClaims(token).get("role", String.class);
	}

	public boolean isTokenValid(String token, AppUser user) {
		if (user == null) {
			return false;
		}

		try {
			Claims claims = parseClaims(token);
			return user.getEmail().equals(claims.getSubject())
					&& user.getId().toString().equals(claims.get("userId", String.class))
					&& user.getRole().equals(claims.get("role", String.class));
		} catch (JwtException | IllegalArgumentException exception) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
