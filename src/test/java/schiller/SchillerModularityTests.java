package schiller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.CanvasOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SchillerModularityTests {

	private static final Class<?> APPLICATION_CLASS = Schiller.class;
	private static final String BASE_PACKAGE = APPLICATION_CLASS.getSimpleName().toLowerCase(Locale.ENGLISH);

	ApplicationModules modules = ApplicationModules.of(APPLICATION_CLASS);
	Predicate<ApplicationModule> isSalespointModule = it -> it.getBasePackage().getName().startsWith("org.salespoint");

	@Test
	void writeComponentDiagrams() throws IOException {

		var options = DiagramOptions.defaults() //
			.withColorSelector(this::getColorForModule) //
			.withDefaultDisplayName(this::getModuleDisplayName) //
			.withTargetOnly(isSalespointModule);

		new Documenter(modules).writeDocumentation(options, CanvasOptions.defaults());
	}

	private Optional<String> getColorForModule(ApplicationModule module) {

		var packageName = module.getBasePackage().getName();

		if (packageName.startsWith("org.salespoint")) {
			return Optional.of("#ddddff");
		} else if (packageName.startsWith(BASE_PACKAGE)) {
			return Optional.of("#ddffdd");
		} else {
			return Optional.empty();
		}
	}

	private String getModuleDisplayName(ApplicationModule module) {

		return module.getBasePackage().getName().startsWith(BASE_PACKAGE) //
			? String.format("%s :: %s", APPLICATION_CLASS.getSimpleName(), StringUtils.capitalize(module.getDisplayName())) //
			: module.getDisplayName();
	}
}