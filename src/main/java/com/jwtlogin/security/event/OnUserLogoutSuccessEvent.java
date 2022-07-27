package com.jwtlogin.security.event;

import java.time.Instant;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnUserLogoutSuccessEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 1L;

	private final String token;
	
	private final Date eventTime;
	
	public OnUserLogoutSuccessEvent(String token) {
		super(token);
        this.token = token;
        this.eventTime = Date.from(Instant.now());
    }

}
