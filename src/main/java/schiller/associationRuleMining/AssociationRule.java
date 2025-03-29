package schiller.associationRuleMining;

import lombok.Getter;
import org.salespointframework.catalog.Product.ProductIdentifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class AssociationRule {

	private final Set<ProductIdentifier> antecedent = new HashSet<>();
	private final Set<ProductIdentifier> consequent = new HashSet<>();
	@Getter
	private double confidence;

	public AssociationRule(Set<ProductIdentifier> antecedent,
						   Set<ProductIdentifier> consequent,
						   double confidence) {
		Objects.requireNonNull(antecedent, "The rule antecedent is null.");
		Objects.requireNonNull(consequent, "The rule consequent is null.");
		this.antecedent.addAll(antecedent);
		this.consequent.addAll(consequent);
		this.confidence = confidence;
	}

	public AssociationRule(Set<ProductIdentifier> antecedent, Set<ProductIdentifier> consequent) {
		this(antecedent, consequent, Double.NaN);
	}

	public Set<ProductIdentifier> getAntecedent() {
		return Collections.unmodifiableSet(antecedent);
	}

	public Set<ProductIdentifier> getConsequent() {
		return Collections.unmodifiableSet(consequent);
	}

	@Override
	public String toString() {
        return Arrays.toString(antecedent.toArray()) +
			" -> " +
			Arrays.toString(consequent.toArray()) +
			": " +
			confidence;
	}

	@Override
	public int hashCode() {
		return antecedent.hashCode() ^ consequent.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		AssociationRule other = (AssociationRule) obj;

		return antecedent.equals(other.antecedent) &&
			consequent.equals(other.consequent);
	}
}
