package com.jwtlogin.security.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.jwtlogin.security.cache.LoggedOutJwtTokenCache;

@Component
public class OnUserLogoutSuccessEventListener implements ApplicationListener<OnUserLogoutSuccessEvent> {
	
	private final LoggedOutJwtTokenCache tokenCache;
	
	 @Autowired
	    public OnUserLogoutSuccessEventListener(LoggedOutJwtTokenCache tokenCache) {
	        this.tokenCache = tokenCache;
	    }

	    public void onApplicationEvent(OnUserLogoutSuccessEvent event) {
	        if (null != event) {
	            tokenCache.markLogoutEventForToken(event);
	        }
	    }

}
