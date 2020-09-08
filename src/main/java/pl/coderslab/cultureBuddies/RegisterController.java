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
import pl.coderslab.cultureBuddies.buddies.BuddiesRepository;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.PictureService;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@Slf4j
@RequestMapping("register")
@RequiredArgsConstructor
public class RegisterController {
    private final BuddiesRepository buddiesRepository;
    private final PictureService pictureService;

    @GetMapping
    public String register(Model model) {
        model.addAttribute(new Buddy());
        return "register";
    }

    @PostMapping
    public String processRegister(@RequestParam(name = "profilePicture", required = false) MultipartFile profilePicture, @Valid Buddy buddy,
                                  BindingResult result,
                                  RedirectAttributes model) throws IOException, URISyntaxException {
        log.debug("Entity to save {}", buddy);
        if (result.hasErrors()) {
            log.warn("Entity {} fails validation", buddy);
            return "/register";
        }
        if (!(profilePicture == null)) {
            pictureService.savePicture(profilePicture, buddy);
        }
        final Buddy savedBuddy = buddiesRepository.save(buddy);
        log.debug("Entity {} been saved", savedBuddy);
        model.addAttribute("username", buddy.getUsername());
        return "redirect:/app/{username}";
    }


}



























