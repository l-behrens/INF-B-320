package internetkaufhaus.model;

import java.util.Iterator;
import java.util.Optional;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserManager {
	
	Role adminRole;
	UserAccountManager manager;
	ConcreteUserAccountRepository repo;
	
	
	@Autowired
	public UserManager(UserAccountManager manager, ConcreteUserAccountRepository repo)
	{
		this.manager = manager;
		this.repo = repo;
		this.adminRole = Role.of("ROLE_ADMIN");
	}
	public boolean deleteUser(Long id)
	{
		int remaining = 0;
		Iterator<ConcreteUserAccount> iter;
		ConcreteUserAccount acc =repo.findOne(id);
		if(acc==null)
		{
			return false;
		}
		if(!acc.getRole().equals(adminRole))
		{
			repo.delete(acc);
			return true;
		}
		else
		{
			iter = repo.findByRole(adminRole).iterator();
			while(iter.hasNext())
			{
				iter.next();
				remaining++;
			}
			if(remaining > 1)
			{
				repo.delete(acc);
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	public boolean changeUser(Long id, String role, String password)
	{
		ConcreteUserAccount acc = repo.findOne(id);
		if(acc==null)
		{
			return false;
		}
		UserAccount usacc = acc.getUserAccount();
		usacc.remove(Role.of("ROLE_ADMIN"));
		usacc.remove(Role.of("ROLE_EMPLOYEE"));
		usacc.remove(Role.of("ROLE_CUSTOMER"));
		usacc.add(Role.of(role));
		manager.save(usacc);
		acc.setUserAccount(usacc);
		acc.setRole(Role.of(role));
		manager.changePassword(usacc, password);
		return true;
	}
	public boolean createUser(String username, String rolename, String password)
	{
//		UserAccount acc = manager.create(username, password, Role.of(rolename));
		if(manager.findByUsername(username).isPresent())
		{
			return false;
		}	
		ConcreteUserAccount cacc = new ConcreteUserAccount(username, password, Role.of(rolename), 
									manager);
		repo.save(cacc);
		manager.save(cacc.getUserAccount());
		return true;
	}
	



}
