package com.jwtlogin.claims.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jwtlogin.claims.model.ClaimFlow;
import com.jwtlogin.claims.model.ClaimRequestApproval;
import com.jwtlogin.claims.model.Claims;
import com.jwtlogin.claims.model.ClaimsRequest;

@Component
public interface ClaimService {
	
	public Claims createClaims(ClaimsRequest claimsRequest, String userName);
	
	public List<Claims> getSubmittedClaims(String userName);
	
	public List<Claims> getApprovalQueueOfClaims(String userName);
	
	public Claims updateClaimStatus(ClaimRequestApproval claimRequestApproval, String approver) throws IllegalAccessException;

	public List<Claims> findByid(Integer id);
	
	public List<Claims> findByClaimFlow(ClaimFlow claimFlow);
	
	public List<Claims> findByClaimId(Integer claimId);
	
	public List<Claims> findByClaimFlowNot(ClaimFlow claimFlow);

}
