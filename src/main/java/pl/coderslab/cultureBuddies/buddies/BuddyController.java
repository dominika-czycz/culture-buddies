package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.coderslab.cultureBuddies.email.EmailService;
import pl.coderslab.cultureBuddies.exceptions.NonExistingNameException;

import javax.mail.MessagingException;

@Controller
@Slf4j
@RequestMapping("app")
@RequiredArgsConstructor
public class BuddyController {
    private final BuddyService buddyService;
    private final EmailService emailService;


    @GetMapping("{username}")
    public String prepareProfile(@PathVariable String username, Model model) throws NonExistingNameException, MessagingException {
        final Buddy buddy = buddyService.findByUsername(username);
        model.addAttribute(buddy);
        emailService.sendHTMLEmail(buddy.getName(), buddy.getEmail());
        return "buddy/profile";
    }

    @GetMapping("profile")
    public String redirectProfile(RedirectAttributes redirectAttributes) {
        String username = getUsername();
        redirectAttributes.addAttribute("username", username);
        return "redirect:/app/{username}";
    }

    private String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    @ModelAttribute("profilePictureDir")
    public String profilePictureDir() {
        return "/pictures/buddyPictures/";
    }

    @ModelAttribute("defaultPicture")
    public String defaultPicture() {
        return "defaultPicture.png";
    }
}
