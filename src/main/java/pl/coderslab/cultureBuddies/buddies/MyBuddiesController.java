package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorService;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/app/myBuddies/")
@RequiredArgsConstructor
public class MyBuddiesController {
    private final BuddyService buddyService;
    private final BuddyRelationService buddyRelationService;
    private final AuthorService authorService;

    @GetMapping
    public String prepareAllPage(Model model) throws NotExistingRecordException {
        log.info("Preparing myBooks page...");
        final List<Buddy> buddies = buddyService.getBuddiesOfPrincipal();
        log.debug("Buddies' list with {} positions has been found. ", buddies.size());
        List<Author> authors = authorService.findAll();
        model.addAttribute("buddies", buddies);
        model.addAttribute("authors", authors);
        return "/buddy/myBuddies";
    }

    @PostMapping("/search")
    public String prepareFirstResultsPage(@RequestParam String username,
                                          @ModelAttribute(name = "authorsIds") ArrayList<Integer> authors,
                                          Model model) throws EmptyKeysException, NotExistingRecordException {
        log.debug("keywords: username {}, authors  {}", username, authors);
        log.info("Looking for buddies...");
        List<Buddy> buddies = buddyService.findByUsernameAndAuthors(username, authors);
        log.debug("{} results found", buddies.size());
        model.addAttribute("buddies", buddies);
        model.addAttribute("newBuddy", new Buddy());
        return "/buddy/add";
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
