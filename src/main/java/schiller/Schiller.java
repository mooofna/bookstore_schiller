/*
 * Copyright 2014-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schiller;

import org.salespointframework.EnableSalespoint;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import schiller.inventory.storage.StorageProperties;
import schiller.inventory.storage.StorageService;

@SpringBootApplication
@EnableSalespoint
@EnableConfigurationProperties(StorageProperties.class)
public class Schiller {

	private static final String LOGIN_ROUTE = "/login";

	public static void main(String[] args) {
		SpringApplication.run(Schiller.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
			storageService.copyMockImg();
		};
	}

	@Configuration
	static class SchillerWebConfiguration implements WebMvcConfigurer {

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController(LOGIN_ROUTE).setViewName("login");
			registry.addViewController("/").setViewName("index");
		}
	}

	@Configuration
	static class WebSecurityConfiguration {
		@Bean
		SecurityFilterChain SchillerSecurity(HttpSecurity http) throws Exception {

			return http
				.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
				.csrf(csrf -> csrf.disable())
				.formLogin(login -> login
					.loginPage(LOGIN_ROUTE)
					.loginProcessingUrl(LOGIN_ROUTE)
					.failureUrl(LOGIN_ROUTE + "?error=true")) // Add this line
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/"))
				.build();
		}
	}
}