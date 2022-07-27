package com.jwtlogin.model;

import java.util.List;

public class AuthResponse {
	
	private String token;
	private List<String> roles;
	private String username;

	public String getToken() {
	    return token;
	}

	public void setToken(String token) {
	    this.token = token;
	}

	public List<String> getRoles() {
	    return roles;
	}

	public void setRoles(List<String> roles) {
	    this.roles = roles;
	}  
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}

}
