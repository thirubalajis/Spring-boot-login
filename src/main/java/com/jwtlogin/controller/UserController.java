package com.jwtlogin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwtlogin.claims.model.ClaimRequestApproval;
import com.jwtlogin.claims.model.Claims;
import com.jwtlogin.claims.model.ClaimsRequest;
import com.jwtlogin.claims.service.ClaimService;
import com.jwtlogin.security.JwtTokenFilter;
import com.jwtlogin.security.JwtTokenUtil;
import com.jwtlogin.security.event.OnUserLogoutSuccessEvent;

import net.bytebuddy.asm.Advice.Return;


@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	JwtTokenFilter jwtTokenFilter;
	
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	ClaimService claimService;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@GetMapping("/createclaims")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> createClaims(HttpServletRequest request,
										  @Valid @RequestBody ClaimsRequest claimsRequest ) {
		
		String userNameString = getUserNameFromToken(request);
		
		if (userNameString != null) {
			
			return new ResponseEntity<Claims>(claimService.createClaims(
					claimsRequest, userNameString),	HttpStatus.OK);
		}
		
		return new ResponseEntity<String>("Token not valid", HttpStatus.UNAUTHORIZED);
	}
	
	@GetMapping("/getSubmittedClaims")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> submittedClaims(HttpServletRequest request) {
		
		String userNameString = getUserNameFromToken(request);
		
		if (userNameString != null) {
				
			List<Claims> claimsList = claimService.getSubmittedClaims(userNameString);
				
			if (claimsList.size() == 0)
				return new ResponseEntity<String>("No claims found", HttpStatus.OK);
			else
				return new ResponseEntity<List<Claims>>(claimsList, HttpStatus.OK);
		}
		
		return new ResponseEntity<String>("Not a valid username", HttpStatus.OK);
	}
	
	@GetMapping("/getApprovalQueueOfClaims")
	@PreAuthorize("hasRole('ROLE_MAN_L1') or "
				+ "hasRole('ROLE_MAN_L2') or "
				+ "hasRole('ROLE_MAN_L3') or "
				+ "hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> approvalQueueOfClaims(HttpServletRequest request) {
	
		String userNameString = getUserNameFromToken(request);
		
		if (userNameString != null) {
	
			List<Claims> claimsList = claimService.getApprovalQueueOfClaims(userNameString);
				
			if (claimsList.size() == 0)
				return new ResponseEntity<String>("No claims in Queue", HttpStatus.OK);
			
			else
				return new ResponseEntity<List<Claims>>(claimsList, HttpStatus.OK);
		}
		
		//String returnString = "Username "+ userNameString + " is not valid to access";
		
		return new ResponseEntity<String>("No valid username ", HttpStatus.OK);
	}
	
	@PostMapping("/updateClaimStatus")
	@PreAuthorize("hasRole('ROLE_MAN_L1') or"
				+ " hasRole('ROLE_MAN_L2') or"
				+ " hasRole('ROLE_MAN_L3') or"
				+ " hasRole('ROLE_ADMIN')")

	public ResponseEntity<?> UpdateClaimStatus(HttpServletRequest request, 
			@Valid @RequestBody ClaimRequestApproval claimRequestApproval ) {
		
		String userNameString = getUserNameFromToken(request);
		
		if (userNameString != null) {
			
			try {
				Claims claimsUpdate;
				claimsUpdate = claimService.updateClaimStatus(claimRequestApproval, userNameString);
				
				return new ResponseEntity<Claims>(claimsUpdate, HttpStatus.OK);
			} catch (IllegalAccessException e) {
				
				return new ResponseEntity<String> (e.getMessage(), HttpStatus.CONFLICT);
			}
			
			
		}
				
		return new ResponseEntity<String>("Username is not valid", HttpStatus.NOT_ACCEPTABLE);	
	}			
	
	@PostMapping("/logoutUser")
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        
		try {
			
			String token = jwtTokenFilter.getTokenFromRequest(request);
			
			//jwtTokenUtil.validateJwtToken(token);
			
			//System.out.println("Signing key "+ jwtTokenUtil.getSigningKey());
			
			OnUserLogoutSuccessEvent logoutSuccessEvent = new OnUserLogoutSuccessEvent(token);
	        
			applicationEventPublisher.publishEvent(logoutSuccessEvent);
			
		}catch (Exception e) {
			
			//return new ResponseEntity<HttpServletRequest>(request, HttpStatus.CONFLICT);
			throw new RuntimeException("No authentication token available " + e.getMessage());
		}
		
        return new ResponseEntity<String>("User has successfully logged out from the system!", HttpStatus.OK);
    }
	
	
	private String getUserNameFromToken(HttpServletRequest request) {
	
		try {
			String token = jwtTokenFilter.getTokenFromRequest(request);
			
			if (token != null && jwtTokenUtil.validateJwtToken(token)) {
				
				String userNameString = jwtTokenUtil.getUserNameFromJwtToken(token);
									
				return userNameString;
			}
		}catch (Exception e) {
			
			throw new RuntimeException("Cannot set user authentication" + e.getMessage());
		}
		
		return null;
	}
	
	@GetMapping("/allusers")
	public String displayUsers() {
		return "Display All Users";
	}
	
	@GetMapping("/displayadmin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String displayToAdmin() {
		return "Display only to admin";
	}
	
	//@Transactional(readOnly = true)
	//public List<Claims> getAllClaims(List<Integer> ids) {
	//	List<Claims> claimResponse = (List<Claims>) claimRepository.findAllById(ids);
	//	return claimResponse;
	//}

}
