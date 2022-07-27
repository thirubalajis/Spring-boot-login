package com.jwtlogin.security.cache;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jwtlogin.security.JwtTokenUtil;
import com.jwtlogin.security.event.OnUserLogoutSuccessEvent;

import net.jodah.expiringmap.ExpiringMap;

@Component
public class LoggedOutJwtTokenCache {
	@Value("${jwttoken.secret}")
	private String skString;
	
	private ExpiringMap<String, OnUserLogoutSuccessEvent> tokenEventMap;

	public LoggedOutJwtTokenCache() {
	    this.tokenEventMap = ExpiringMap.builder()
	    					.maxSize(1000)
	                		.variableExpiration()
	                		.build();
	}

	public void markLogoutEventForToken(OnUserLogoutSuccessEvent event) {
		
		String token = event.getToken();
		
		if (tokenEventMap.containsKey(token)) {
			System.out.println("Log out token for user is already present in the cache");

		} else {
			JwtTokenUtil tokenProvider = new JwtTokenUtil();
			tokenProvider.setSigningKey(skString);
			System.out.println("In Class LoggedOutJwtTokenCache " + tokenProvider.getSigningKey());
			Date tokenExpiryDate = tokenProvider.getTokenExpiryFromJWT(token);
			long ttlForToken = getTTLForToken(tokenExpiryDate);
			System.out.println("Logout token cache set for with a TTL of seconds."
					+ " Token is due expiry at " +  ttlForToken + " " + tokenExpiryDate);
			tokenEventMap.put(token, event, ttlForToken, TimeUnit.SECONDS);
		}
	}

	public OnUserLogoutSuccessEvent getLogoutEventForToken(String token) {
		return tokenEventMap.get(token);
	}

	private long getTTLForToken(Date date) {
		long secondAtExpiry = date.toInstant().getEpochSecond();
		long secondAtLogout = Instant.now().getEpochSecond();
		return Math.max(0, secondAtExpiry - secondAtLogout);
	}
}
