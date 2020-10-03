package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.coderslab.cultureBuddies.events.Event;
import pl.coderslab.cultureBuddies.events.EventService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("app/board/")
@RequiredArgsConstructor
public class ProfileController {
    private final BuddyService buddyService;
    private final EventService eventService;

    @GetMapping("{username}")
    public String prepareProfile(@PathVariable String username, Model model) throws NotExistingRecordException {
        final Buddy buddy = buddyService.findByUsername(username);
        final List<Event> events = eventService.findRecentEvents();
        final String picture = buddyService.getPicture(buddy);
        model.addAttribute(buddy);
        model.addAttribute("events", events);
        model.addAttribute("picture", picture);
        return "buddy/profile";
    }

    @GetMapping("/profile")
    public String redirectProfile(RedirectAttributes redirectAttributes) {
        String username = buddyService.getPrincipalUsername();
        redirectAttributes.addAttribute("username", username);
        return "redirect:/app/board/{username}";
    }

    @GetMapping("/changePassword")
    public String prepareChangePasswordPage() {
        return "/buddy/changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String password,
                                 @RequestParam String repeatedPassword,
                                 Model model) throws NotExistingRecordException {
        if (!password.equals(repeatedPassword)) {
            log.warn("Passwords are not the same: {}, {}", password, repeatedPassword);
            model.addAttribute("passwordMessage", "Passwords must be the same!");
            return "/buddy/changePassword";
        }
        buddyService.updatePassword(password);
        return "redirect:/app/board/profile/";
    }

    @GetMapping("/changePicture")
    public String prepareChangePicturePage() {
        return "/buddy/changePicture";
    }

    @PostMapping("/changePicture")
    public String processChangePicturePage(
            @RequestParam(name = "profilePicture", required = false) MultipartFile profilePicture,
            Model model) throws NotExistingRecordException, IOException {
        if (!buddyService.isProperFileSize(profilePicture)) {
            model.addAttribute("pictureMessage", "Profile picture's size must be lower than 1MB!");
            return "/buddy/changePicture";
        }
        buddyService.updateProfilePicture(profilePicture);
        return "redirect:/app/board/profile/";
    }

    @ModelAttribute("defaultPicture")
    public String defaultPicture() {
        return "/pictures/buddyPictures/defaultPicture.png";
    }
}
