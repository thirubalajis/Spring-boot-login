package com.jwtlogin.claims.service;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jwtlogin.claims.model.ClaimFlow;
import com.jwtlogin.claims.model.ClaimRequestApproval;
import com.jwtlogin.claims.model.ClaimStatus;
import com.jwtlogin.claims.model.ClaimType;
import com.jwtlogin.claims.model.Claims;
import com.jwtlogin.claims.model.ClaimsRequest;
import com.jwtlogin.claims.repository.ClaimRepository;
import com.jwtlogin.dao.UserRepository;
import com.jwtlogin.model.Role;
import com.jwtlogin.model.Roles;
import com.jwtlogin.model.User;

@Service
public class ClaimServiceImpl implements ClaimService{
	
	@Autowired
	ClaimRepository claimRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Transactional
	public List<Claims> findByid(Integer id) {
		List<Claims> response = (List<Claims>) claimRepository.findByid(id);
		return response;
	}
	
	@Transactional
	public List<Claims> findByClaimFlow(ClaimFlow claimFlow){
		List<Claims> response = (List<Claims>) claimRepository.findByClaimFlow(claimFlow);
		
		return response;
	}
	
	@Transactional
	public List<Claims> findByClaimId(Integer claimId) {
		
		List<Claims> response = (List<Claims>) claimRepository.findByClaimId(claimId);
		
		return response;
	}
	
	@Transactional
	public List<Claims> findByClaimFlowNot(ClaimFlow claimFlow) {
		
		List<Claims> response = (List<Claims>) claimRepository.findByClaimFlowNot(claimFlow);
		
		return response;
	}

	
	@Override
	public Claims createClaims(ClaimsRequest claimsRequest, String userName) {
		
		Claims claims = new Claims();
		
		claims.setAmount(claimsRequest.getAmount());
		
		claims.setClaimStatus(ClaimStatus.CLAIM_CREATED);
		
		claims.setClaimFlow(ClaimFlow.CLAIM_TO_START);
		
		if (claimsRequest.getComment() != null) {
			claims.setComment(claimsRequest.getComment());
		}
		
		switch(claimsRequest.getClaimType()) {
		
		case "CONVEYANCE_CLAIM":
			claims.setClaim(ClaimType.CONVEYANCE_CLAIM);
			break;
			
		case "TELEPHONE_CLAIM":
			claims.setClaim(ClaimType.TELEPHONE_CLAIM);
			break;
			
		case "FUEL_CLAIM":
			claims.setClaim(ClaimType.FUEL_CLAIM);
			break;
			
		case "INTERNET_CLAIM":
			claims.setClaim(ClaimType.INTERNET_CLAIM);
			break;
		
		case "OTHERS":
			claims.setClaim(ClaimType.OTHERS);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid type of claim");
		}
		
		if (claims.getClaim() == ClaimType.OTHERS && claims.getComment() == null) {
			throw new MissingFormatArgumentException("Comment required for Other Type Claims");
		}
		
		if (claims.getAmount() == null) {
			throw new MissingFormatArgumentException("Enter all the particulars, Amount is missing");
		}
		
		// Get Id from the Token Username
		
		User user = findByUserName(userName);
		
		claims.setId(user.getId());
		
		claimRepository.save(claims);

		return claims;
	}
	
	@Override
	public List<Claims> getSubmittedClaims(String userName) {
		
		User user = findByUserName(userName);
		List<Claims> claimsList = claimRepository.findByid(user.getId());
		
		System.out.println(userName + " " + user.getId() + " " + claimsList.size());
		
		if (claimsList.size() > 0) {
			
			for(Claims c :claimsList)
				PrintClaims(c);
		
		}
		return claimsList;
	}

	@Override
	public List<Claims> getApprovalQueueOfClaims(String userName) {
		
		User user = findByUserName(userName);
		// Get the User Role (usually this is a HASH SET along with User role, ignore User role
		Set<Role> roles = user.getRoles();
		
		List<Claims> claims = new ArrayList<>();
		//Get the list of claims pending with specific User role
		for(Role r: roles) {
			//Return the list, List can be empty if nothing is present
			if (r.getRoleName() == (Roles.ROLE_MAN_L1))
				return claimRepository.findByClaimFlow(ClaimFlow.CLAIM_TO_START);
			
			else if (r.getRoleName() == (Roles.ROLE_MAN_L2))
				return claimRepository.findByClaimFlow(ClaimFlow.CLAIM_APPROVED_L1);
			
			else if (r.getRoleName() == (Roles.ROLE_MAN_L3))
				return claimRepository.findByClaimFlow(ClaimFlow.CLAIM_APPROVED_L2);

			else if (r.getRoleName() == (Roles.ROLE_ADMIN))
				return claimRepository.findByClaimFlow(ClaimFlow.CLAIM_UNKNOWN);
			
			//else Traverse to see valid role
		}
		
		return claims;
	}

	@Override
	public Claims updateClaimStatus(ClaimRequestApproval claimRequestApproval, String approver) throws IllegalAccessException {
		
		List<Claims> claims = claimRepository.findByClaimId(claimRequestApproval.getClaimId());
		
		if (claims.size() != 1) {
			
			String errString = ("ClaimID" + claimRequestApproval.getClaimId() +  "is not valid");
			
			throw new IllegalArgumentException(errString);
		}
		Claims claimsUpdate = claims.get(0);
		//Check if the status is relevant to the user role
		User user = findByUserName(approver);
		
		// Check if the user Id and Claim user ID are same, dont allow to update
		if (user.getId() == claimsUpdate.getId())
			throw new IllegalAccessException("Claim generated User and Approver User are same");
		
		// Get the User Role (usually this is a HASH SET along with User role, ignore User role
		Set<Role> roles = user.getRoles();
		
		List<Roles> userRole = getListOfUserRole(roles);
		
		boolean updateStatus = false;
		
		boolean approvedStatus = false;

		// Authority to Update

		if (claimRequestApproval.getStatuString().equals("Approved"))
			approvedStatus = true;
		
		else if (claimRequestApproval.getStatuString().equals( "Denied"))	
			approvedStatus = false;

		else {
			
			String errString = "Invalid Claim Status = " 
					+ claimRequestApproval.getStatuString();
			System.out.println(errString);
			
			throw new IllegalArgumentException(errString);
		}
		
		//Get the list of claims pending with specific User role
		
		if (claimsUpdate.getClaimStatus() == ClaimStatus.CLAIM_CREATED && 
				userRole.contains(Roles.ROLE_MAN_L1)) {
			
			updateStatus = true;
			
			claimsUpdate.setClaimStatus(ClaimStatus.CLAIM_INPROGRESS);
			
			if (approvedStatus) {
			
				claimsUpdate.setClaimFlow(ClaimFlow.CLAIM_APPROVED_L1);
			}
			else {
				claimsUpdate.setClaimFlow(ClaimFlow.CLAIM_DENIED_L1);
				claimsUpdate.setClaimStatus(ClaimStatus.CLAIM_DENIED);
			}
		}
		else if (claimsUpdate.getClaimStatus() == ClaimStatus.CLAIM_INPROGRESS &&
				claimsUpdate.getClaimFlow() == ClaimFlow.CLAIM_APPROVED_L1 &&
				userRole.contains(Roles.ROLE_MAN_L2)) {
			
			updateStatus = true;
			
			if (approvedStatus) {
			
				claimsUpdate.setClaimFlow(ClaimFlow.CLAIM_APPROVED_L2);
			}
			else {
				claimsUpdate.setClaimFlow(ClaimFlow.CLAIM_DENIED_L2);
				claimsUpdate.setClaimStatus(ClaimStatus.CLAIM_DENIED);
			}
		}	
		else if (claimsUpdate.getClaimStatus() == ClaimStatus.CLAIM_INPROGRESS &&
				claimsUpdate.getClaimFlow() == ClaimFlow.CLAIM_APPROVED_L2 &&
				userRole.contains(Roles.ROLE_MAN_L3)) {
			
			updateStatus = true;
			
			if (approvedStatus) {
			
				claimsUpdate.setClaimFlow(ClaimFlow.CLAIM_APPROVED_L3);
			}
			else {
				claimsUpdate.setClaimFlow(ClaimFlow.CLAIM_DENIED_L3);
				claimsUpdate.setClaimStatus(ClaimStatus.CLAIM_DENIED);
			}
		}	
		
		if (updateStatus) {
			if (claimRequestApproval.getAckString() != null &&
					!claimRequestApproval.getAckString().isEmpty())
				claimsUpdate.setAckString(claimRequestApproval.getAckString());
			
			if(claimRequestApproval.getAckString() != null &&
					!claimRequestApproval.getCommentString().isEmpty())
				claimsUpdate.setComment(claimRequestApproval.getCommentString());
			
			PrintClaims(claimsUpdate);
			
			claimRepository.save(claimsUpdate);

		}
		else {
			
			String errString = "Current role and WorkFlow doesnt allow Updation";
			
			System.out.println(errString);
			
			throw new IllegalAccessError(errString);
		}
		return claimsUpdate;
	}
	
	private User findByUserName(String userName) {
		
		User user =	userRepository.findByUserName(userName).
					orElseThrow(() -> new UsernameNotFoundException(
					"User name " + userName + 
					"Not found. This should not have ended up here"));
		
		System.out.println("User ID "+ user.getId() + "User name "+ userName);
		
		return user;
		
	}
	
	private void PrintClaims(Claims c) {
		
		System.out.println("Claim id " + c.getClaimId());
		System.out.println("Ack String " + c.getAckString());
		System.out.println("Comment " + c.getComment());
		System.out.println("File Attached " + c.getFilename());
		System.out.println("Amount " + c.getAmount());
		System.out.println("Claim flow " + c.getClaimFlow());
		System.out.println("Claim Status " + c.getClaimStatus());
		System.out.println("Claim Type " + c.getClaimType());
		//System.out.println("Claim " + c.get);
		return;
	}
	
	private List<Roles> getListOfUserRole(Set<Role> roles) {
		
		List<Roles> roleList = new ArrayList<>();
		
		for (Role r: roles) {
			
			roleList.add(r.getRoleName());
			/*
			if (currentRole == Roles.ROLE_USER) {
				currentRole = tempRole;
			}
			else if (currentRole == Roles.ROLE_MAN_L1 && tempRole != Roles.ROLE_USER) {
				currentRole = tempRole;
			}
			else if (currentRole ==Roles.ROLE_MAN_L2 && (tempRole != Roles.ROLE_USER)
					&& (tempRole != Roles.ROLE_MAN_L1)) {
				currentRole = tempRole;
			}
			else if (currentRole == Roles.ROLE_MAN_L3 && tempRole == Roles.ROLE_ADMIN) {
				currentRole = tempRole;
			}
			//else if (currentRole == Roles.ROLE_ADMIN) {
			 //Dont update 	
			//}
			*/
		}
		return roleList;
	}

}
