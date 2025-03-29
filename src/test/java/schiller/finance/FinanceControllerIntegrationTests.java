package schiller.finance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import schiller.AbstractIntegrationTests;
import schiller.finance.time.Quarter;

import javax.money.MonetaryAmount;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc
@SpringBootTest
public class FinanceControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testFinanceViewAndModelAttributes() throws Exception {
		MvcResult result = mockMvc.perform(get("/finance"))
			.andExpect(status().isOk())
			.andExpect(view().name("finance"))
			.andReturn();

		ModelMap model = result.getModelAndView().getModelMap();

		assertThat(model).containsKey("quarters");
		assertThat(model).containsKey("intervalForm");
		assertThat(model).containsKey("totalTurnover");
		assertThat(model).containsKey("totalIntervalTurnover");
		assertThat(model).containsKey("turnoverPerQuarter");
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testFinanceViewUpdate() throws Exception {
		IntervalForm intervalForm = new IntervalForm(
			2020,
			2024,
			"FIRST",
			"FOURTH"
		);

		MvcResult result = mockMvc.perform(post("/finance")
				.flashAttr("intervalForm", intervalForm))
			.andExpect(status().isOk())
			.andExpect(view().name("finance"))
			.andReturn();

		ModelMap model = result.getModelAndView().getModelMap();

		assertThat(model).containsKey("turnoverPerQuarter");
		var turnoverPerQuarter = (Map) model.getAttribute("turnoverPerQuarter");
        assert turnoverPerQuarter != null;
        assertEquals(20, turnoverPerQuarter.size());
	}
}
