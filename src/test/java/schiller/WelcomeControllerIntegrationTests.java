package schiller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc
@SpringBootTest
public class WelcomeControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testHomePage() throws Exception {
		MvcResult result = mockMvc.perform(get("/index"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andReturn();

	}

	@Test
	void testLoginPage() throws Exception {
		MvcResult result = mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("login"))
			.andReturn();

	}
}
