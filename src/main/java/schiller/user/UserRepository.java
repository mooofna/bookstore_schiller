package schiller.user;

import schiller.user.User.UserIdentifier;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.salespointframework.useraccount.Role;

public interface UserRepository extends CrudRepository<User, UserIdentifier> {

	@Override
	Streamable<User> findAll();
	
	Streamable<Customer> findUsersByUserAccount_Roles(Role role);

	User findUserByUserAccountUsernameContaining(String name);

	User findUserByUserAccount_Email(String email);

}