package com.lookingforgroup.db.account;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.lookingforgroup.model.accountandprofile.Account;
import com.lookingforgroup.model.accountandprofile.WebAccount;

@Repository
public interface AccountDao {
	public Account addAccount(WebAccount webAccount, List<String> roles);
	public Account getAccountById(int id);
	public Account getAccountByEmail(String email);
	public Account getAccountByVerificationCode(String verificationCode);
	public List<Account> getAllAccounts();
	public boolean verify(String verificationCode);
	public boolean emailExists(String email);
}