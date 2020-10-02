package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.coderslab.cultureBuddies.events.Event;
import pl.coderslab.cultureBuddies.events.EventService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

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

    @ModelAttribute("defaultPicture")
    public String defaultPicture() {
        return "/pictures/buddyPictures/defaultPicture.png";
    }
}
