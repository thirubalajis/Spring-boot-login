package com.jwtlogin.claims.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jwtlogin.claims.model.ClaimFlow;
import com.jwtlogin.claims.model.Claims;

@Repository
public interface ClaimRepository extends JpaRepository<Claims, Integer> {
	
	public List<Claims> findByid(Integer id);
	
	public List<Claims> findByClaimFlow(ClaimFlow claimFlow);
	
	public List<Claims> findByClaimId(Integer claimId);
	
	public List<Claims> findByClaimFlowNot(ClaimFlow claimFlow);

}
