package com.lookingforgroup.db.account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.lookingforgroup.model.accountandprofile.Account;
import com.lookingforgroup.model.accountandprofile.WebAccount;

import net.bytebuddy.utility.RandomString;

@Repository
public class AccountDaoImpl implements AccountDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DataSourceTransactionManager transactionManager;
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	// ------------------------------------------------
	// CREATE OPS
	// ------------------------------------------------
	@Override
	public Account addAccount(WebAccount webAccount, List<String> roles) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		int id = -1;
		
		try {
			//Account creation
			final String accountSQL = "insert into lfg_account (email, password, verification_code) "
					   + " values (?, ?, ?)";
			webAccount.setVerificationCode(RandomString.make(64));
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(accountSQL, new String[] { "id" });
				ps.setString(1, webAccount.getEmail());
				ps.setString(2, webAccount.getPassword());
				ps.setString(3, webAccount.getVerificationCode());
				return ps;
			}, keyHolder);
			
			id = keyHolder.getKey().intValue();
			
			logger.info("Created account with Email: " + webAccount.getEmail() + " and Id: " + id);
			logger.info("Verification Code: " + webAccount.getVerificationCode());
			
			//Profile creation
			final String profileSQL = "insert into lfg_profile (id, username, bio)" +
					 "values (?, ?, ?)";
		
			jdbcTemplate.update(profileSQL, id, webAccount.getUsername(), "Iorem Ipsum...");
		
			logger.info("Created profile for Id: " + id);
			
			//Roles creation
			final String rolesSQL = "insert into lfg_roles (email, role)" + "values (?, ?)";
			for(String role : roles) {
				jdbcTemplate.update(rolesSQL, webAccount.getEmail(), role);
			}
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in creating account record, rolling back.");
			transactionManager.rollback(status);
			throw e;
		}		
		
		return getAccountById(id);
	}

	// ------------------------------------------------
	// READ OPS
	// ------------------------------------------------
	@Override
	public Account getAccountById(int id) {
		String SQL = "select * from lfg_account where id = ?";
		Account account = jdbcTemplate.queryForObject(SQL, new AccountMinusPasswordMapper(), new Object[] { id });
		
		logger.info("Retrieved Account ID = " + account.getId());
		
		return account;
	}
	
	@Override
	public Account getAccountByEmail(String email) {
		String SQL = "select * from lfg_account where email = ?";
		Account account = jdbcTemplate.queryForObject(SQL, new AccountMinusPasswordMapper(), new Object[] { email });
		
		logger.info("Retrieve Account Email = " + account.getEmail());
		return account;
	}
	
	@Override
	public Account getAccountByVerificationCode(String verificationCode) {
		String SQL = "select * from lfg_account where verification_code = ?";
		Account account = jdbcTemplate.queryForObject(SQL, new AccountMinusPasswordMapper(), new Object[] { verificationCode });
		
		logger.info("Retrieved Account ID = " + account.getId());
		
		return account;
	}

	@Override
	public List<Account> getAllAccounts() {
		String SQL = "select * from lfg_account";
		List<Account> accounts = jdbcTemplate.query(SQL, new AccountMinusPasswordMapper());
		return accounts;
	}
	
	// ------------------------------------------------
	// UPDATE OPS
	// ------------------------------------------------
	@Override
	public boolean verify(String verificationCode) {
		Account account = getAccountByVerificationCode(verificationCode);
		
		if(account == null || account.isActive()) {
			return false;
		}
		else {
			String SQL = "UPDATE lfg_account SET verification_code = ?, active = ? WHERE verification_code = ?";
			jdbcTemplate.update(SQL, null, true, verificationCode);
			return true;
		}
	}
	
	// -----------------------------------------------
	// ERROR CHECKING & UTILITY
	// -----------------------------------------------
	
	@Override
	public boolean emailExists(String email) {
		String SQL = "select email from lfg_account where email = ?";
		String result = jdbcTemplate.queryForObject(SQL, String.class, new Object[] { email });
		if(result.equals(email)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// ------------------------------------------------
	// MAPPERS
	// ------------------------------------------------
	
	class AccountMinusPasswordMapper implements RowMapper<Account> {
		@Override
		public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
			Account account = new Account();
			account.setId(rs.getInt("id"));
			account.setCreationtime(rs.getObject("creation_time", OffsetDateTime.class));
			account.setUpdatedtime(rs.getObject("updated_time", OffsetDateTime.class));
			account.setEmail(rs.getString("email"));
			account.setVerificationCode(rs.getString("verification_code"));
			return account;
		}
	}
}
