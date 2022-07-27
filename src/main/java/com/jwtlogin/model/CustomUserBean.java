package com.jwtlogin.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CustomUserBean implements UserDetails {

	private static final long serialVersionUID = 2597943885927486689L;
	
	private Integer id; 
	private String userName; 
	private String email;
	  
	@JsonIgnore
	private String password;
	private Collection<? extends GrantedAuthority> authorities;

	CustomUserBean(Integer id, String userName, String email, 
		      String password, Collection<? extends GrantedAuthority> authorities){
		    this.setId(id);
		    this.userName = userName;
		    this.setEmail(email);
		    this.password = password;
		    this.authorities = authorities;
	}
	
	public static CustomUserBean createInstance(User user) {
	    List<GrantedAuthority> authorities = user.getRoles()
	                     .stream()
	                         .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
	                      .collect(Collectors.toList());
	    return new CustomUserBean(user.getId(), user.getUserName(), 
	        user.getEmail(), user.getPassword(), authorities);
	  }
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return authorities;
		
	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	  public boolean equals(Object rhs) {
	    if (rhs instanceof CustomUserBean) {
	      return userName.equals(((CustomUserBean) rhs).userName);
	    }
	    return false;
	  }

	  @Override
	  public int hashCode() {
	    return userName.hashCode();
	  }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
