package com.jwtlogin.claims.model;

public class ClaimRequestApproval {
	
	private Integer claimId;
	
	private String statuString;
	
	private String ackString;
	
	private String commentString;

	public Integer getClaimId() {
		return claimId;
	}

	public void setClaimId(Integer claimId) {
		this.claimId = claimId;
	}

	public String getStatuString() {
		return statuString;
	}

	public void setStatuString(String statuString) {
		this.statuString = statuString;
	}

	public String getAckString() {
		return ackString;
	}

	public void setAckString(String ackString) {
		this.ackString = ackString;
	}

	public String getCommentString() {
		return commentString;
	}

	public void setCommentString(String commentString) {
		this.commentString = commentString;
	}

}
