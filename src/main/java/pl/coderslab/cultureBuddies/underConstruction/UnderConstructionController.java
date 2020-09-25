package pl.coderslab.cultureBuddies.underConstruction;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/underConstruction")
public class UnderConstructionController {
    @GetMapping
    public String prepareErrorPage(Model model) {
        model.addAttribute("message", "The website is under construction.");
        return "/errors/error";
    }
}
