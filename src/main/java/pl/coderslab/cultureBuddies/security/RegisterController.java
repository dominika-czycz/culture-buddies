package pl.coderslab.cultureBuddies.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.email.EmailService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
@RequestMapping("register")
@RequiredArgsConstructor
public class RegisterController {
    private final BuddyService buddyService;
    private final EmailService emailService;
    private final CityRepository cityRepository;

    @GetMapping
    public String prepareRegisterPage(Model model) {
        log.info("Preparing to register");
        model.addAttribute(new Buddy());
        return "register";
    }

    @PostMapping
    public String processRegister(@RequestParam(name = "profilePicture", required = false) MultipartFile profilePicture,
                                  @RequestParam(name = "repeatedPassword") String repeatedPassword,
                                  @Valid Buddy buddy,
                                  BindingResult result,
                                  Model model) throws IOException, MessagingException {
        log.debug("Entity to save {}", buddy);
        if (!isValid(buddy, result)) return "register";
        if (!buddyService.isProperFileSize(profilePicture)) {
            model.addAttribute("pictureMessage", "Profile picture's size must be lower than 1MB!");
            return "register";
        }
        if (!arePasswordsTheSame(repeatedPassword, buddy, model)) return "register";
        final boolean isSavedUniqueBuddy = buddyService.save(profilePicture, buddy);
        if (isSavedUniqueBuddy) {
            emailService.sendHTMLEmail(buddy.getName(), buddy.getEmail());
            return "redirect:/";
        }
        log.debug("Entity {} is not unique. Return to register view.", buddy);
        model.addAttribute("errorMessage", "Username is not unique.");
        return "register";
    }

    private boolean isValid(Buddy buddy, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Entity {} fails validation. Return to register view.", buddy);
            return false;
        }
        return true;
    }

    private boolean arePasswordsTheSame(String repeatedPassword, Buddy buddy, Model model) {
        if (!Objects.equals(repeatedPassword, buddy.getPassword())) {
            log.warn("Passwords 1: {}, 2: {} are not the same", buddy.getPassword(), repeatedPassword);
            model.addAttribute("passwordMessage", "Repeated password is not the same!");
            return false;
        }
        return true;
    }

    @ModelAttribute("cities")
    public List<City> cities() {
        return cityRepository.findAll();
    }
}



























