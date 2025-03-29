package schiller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

	@GetMapping("/index")
	public String homePage() {
		return "index";
	}

	@GetMapping("/login")
	public String loginPage(){
		return "login";
	}
}
