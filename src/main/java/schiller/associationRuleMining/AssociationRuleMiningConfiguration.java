package schiller.associationRuleMining;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class AssociationRuleMiningConfiguration {
	@Value("${salespoint.association.minimum-support}")
	private double minimumSupport;

	@Value("${salespoint.association.minimum-confidence}")
	private double minimumConfidence;
}