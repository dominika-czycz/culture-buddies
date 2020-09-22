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
    private final AuthorService authorService;

    @GetMapping
    public String prepareAllPage(Model model) throws NotExistingRecordException {
        log.info("Preparing myBooks page...");
        final List<Buddy> buddies = buddyService.getBuddiesOfPrincipal();
        log.debug("Buddy's buddies list with {} positions has been found. ", buddies.size());
        final List<Buddy> inviting = buddyService.getBuddiesInvitingPrincipal();
        List<Author> authors = authorService.findAll();
        model.addAttribute("buddies", buddies);
        model.addAttribute("authors", authors);
        model.addAttribute("inviting", inviting);
        model.addAttribute("newBuddy", new Buddy());
        return "/buddy/myBuddies";
    }

    @PostMapping("/search")
    public String prepareResultsPage(@RequestParam String username,
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

    @PostMapping("/add")
    public String processInviteNewBuddy(@RequestParam Long buddyId) throws NotExistingRecordException {
        log.debug("Preparing to invite buddy with id {}...", buddyId);
        buddyService.inviteBuddy(buddyId);
        return "redirect:/app/myBuddies/";

    }

    @PostMapping("/accept")
    public String processAcceptInvitation(@RequestParam Long buddyId) throws NotExistingRecordException {
        log.debug("Preparing to accept buddy with id {}...", buddyId);
        buddyService.acceptBuddy(buddyId);
        return "redirect:/app/myBuddies/";
    }

    @GetMapping("/delete/{buddyId}")
    public String prepareDeletePage(@PathVariable Long buddyId, Model model) throws NotExistingRecordException {
        log.debug("Preparing to delete buddy with id {}...", buddyId);
        Buddy buddy = buddyService.findById(buddyId);
        model.addAttribute(buddy);
        return "/buddy/delete";
    }

    @PostMapping("/delete")
    public String processDeleteFromBuddies(@RequestParam Long buddyId) throws NotExistingRecordException {
        buddyService.deleteBuddy(buddyId);
        return "redirect:/app/myBuddies/";
    }

    @GetMapping("/block/{buddyId}")
    public String prepareBlockPage(@PathVariable Long buddyId, Model model) throws NotExistingRecordException {
        log.debug("Preparing to block buddy with id {}...", buddyId);
        Buddy buddy = buddyService.findById(buddyId);
        model.addAttribute(buddy);
        return "/buddy/block";
    }

    @PostMapping("/block")
    public String processBlockBuddy(@RequestParam Long buddyId) throws NotExistingRecordException {
        buddyService.block(buddyId);
        return "redirect:/app/myBuddies/";
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
