package schiller.user;

import jakarta.persistence.*;
import org.jmolecules.ddd.types.Identifier;
import org.salespointframework.core.AbstractAggregateRoot;
import org.salespointframework.useraccount.UserAccount;
import schiller.user.User.UserIdentifier;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents Users of the website, and is the parent class for {@link Customer}
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AbstractAggregateRoot<UserIdentifier> {

	// EmbeddedId means that id is the identifier for the entity of User
	private @EmbeddedId UserIdentifier id = new UserIdentifier();


	// UserAccount(SalesPoint) can hold more attributes, like password, role etc.
	// Every User gets one UserAccount -> we save the UserAccount here to access it later
	@OneToOne //
	private UserAccount userAccount;

	@SuppressWarnings("unused")
	public User() {}

	public User(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Persistable#getId()
	 */
	@Override
	public UserIdentifier getId() {
		return id;
	}


	public UserAccount getUserAccount() {
		return userAccount;
	}

	// Embeddable: attributes of the embeddable class become part of the entity's table in the database
	@Embeddable
	public static final class UserIdentifier implements Identifier, Serializable {

		// UUID is used to generate unique identifiers
		private static final long serialVersionUID = 7740660930809051850L;
		private final @SuppressWarnings("unused") UUID identifier;

		/**
		 * Creates a new unique identifier for {@link User}s.
		 */
		UserIdentifier() {
			this(UUID.randomUUID());
		}

		/**
		 * Only needed for property editor, shouldn't be used otherwise.
		 *
		 * @param identifier The string representation of the identifier.
		 */
		UserIdentifier(UUID identifier) {
			this.identifier = identifier;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;

			result = prime * result + (identifier == null ? 0 : identifier.hashCode());

			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {

			if (obj == this) {
				return true;
			}

			if (!(obj instanceof UserIdentifier that)) {
				return false;
			}

			return this.identifier.equals(that.identifier);
		}
	}
}