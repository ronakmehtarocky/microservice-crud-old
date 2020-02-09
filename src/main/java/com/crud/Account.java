package com.crud;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "account")
public class Account {
	@Id
	private Long accountNumber;
	private Double accountBalance;
	
	 @ManyToOne(cascade = CascadeType.ALL)
	    @JoinColumn(name = "user_id")
	    private User user;

	 
	
	public Account(Long accountNumber, Double accountBalance) {
		super();
		this.accountNumber = accountNumber;
		this.accountBalance = accountBalance;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public Double getAccountBalance() {
		return accountBalance;
	}
	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
	}
}
