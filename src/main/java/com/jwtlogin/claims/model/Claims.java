package com.jwtlogin.claims.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name ="testc")
public class Claims {
	
	@Column(name = "empId", nullable = false)
	private Integer id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private ClaimType claimType;
	
	@Column(name = "amt", nullable = false)
	private Float amount;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer claimId;
	
	@Column(name = "comm")
	private String comment;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "Status", nullable = false)
	
	private ClaimStatus claimStatus;
	
	@Enumerated(EnumType.STRING)
	@Column(name ="wf", nullable = false)
	
	private ClaimFlow claimFlow;
	
	@Column(name = "ack")
	private String ackString;
	
	@Column(name = "filename")
	private String filename;
	
	@Column(name = "createdate")
	@CreationTimestamp
	private Date createDate;
	
	@Column(name = "updatedate")
	@UpdateTimestamp
	private Date updatedDate;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ClaimType getClaim() {
		return claimType;
	}

	public void setClaim(ClaimType claimType) {
		this.claimType = claimType;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Integer getClaimId() {
		return claimId;
	}

	public void setClaimId(Integer claimId) {
		this.claimId = claimId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ClaimStatus getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(ClaimStatus claimStatus) {
		this.claimStatus = claimStatus;
	}

	public ClaimFlow getClaimFlow() {
		return claimFlow;
	}

	public void setClaimFlow(ClaimFlow claimFlow) {
		this.claimFlow = claimFlow;
	}

	public String getAckString() {
		return ackString;
	}

	public void setAckString(String ackString) {
		this.ackString = ackString;
	}

	public ClaimType getClaimType() {
		return claimType;
	}

	public void setClaimType(ClaimType claimType) {
		this.claimType = claimType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
