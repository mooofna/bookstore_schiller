package schiller.order;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jmolecules.ddd.types.ValueObject;

import java.io.Serializable;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class DeliveryMethod implements Serializable, ValueObject {

	private static final long serialVersionUID = -6927889732758783955L;

	/**
	 * Description of the <code>PaymentMethod</code> in human-readable form. Is not {@literal null}.
	 */
	private final @NonNull String description;

	@Id
	@GeneratedValue
	private Long id;

	public DeliveryMethod() {
		description = "";
	}

	public abstract boolean readyForCompletion();

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}