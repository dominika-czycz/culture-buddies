package pl.coderslab.cultureBuddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@Slf4j
@RequestMapping("register")
@RequiredArgsConstructor
public class RegisterController {
    private final BuddyService buddyService;

    @GetMapping
    public String register(Model model) {
        model.addAttribute(new Buddy());
        return "register";
    }

    @PostMapping
    public String processRegister(@RequestParam(name = "profilePicture", required = false) MultipartFile profilePicture,
                                  @Valid Buddy buddy,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) throws IOException {
        log.debug("Entity to save {}", buddy);
        if (result.hasErrors()) {
            log.warn("Entity {} fails validation. Return to register view.", buddy);
            return "register";
        }
        final boolean isSavedUniqueBuddy = buddyService.save(profilePicture, buddy);
        if (isSavedUniqueBuddy) {
            redirectAttributes.addAttribute("username", buddy.getUsername());
            log.debug("Redirect to :/app/{}", buddy.getUsername());
            return "redirect:/app/{username}";
        }
        log.debug("Entity {} is not unique. Return to register view.", buddy);
        model.addAttribute("errorMessage", "Username is not unique.");
        return "register";
    }
}



























