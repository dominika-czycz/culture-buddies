package pl.coderslab.cultureBuddies.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingNameException;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/app/myBooks")
@Slf4j
@RequiredArgsConstructor
public class BookController {
    private final BuddyService buddyService;
    private final BookService bookService;

    @GetMapping
    public String prepareMyBooksPage(Model model) throws NotExistingNameException {
        log.info("Preparing myBooks page...");
        final Buddy buddy = buddyService.findAuthenticatedBuddyWithAuthors();
        final Set<Author> authors = buddy.getAuthors();
        model.addAttribute("authors", authors);
        return "/books/myBooks";
    }

    @GetMapping("/{authorId}")
    public String prepareMyBooksAuthorPage(Model model, @PathVariable Long authorId) throws NotExistingNameException {
        log.info("Preparing /app/myBooks/{authorId} page...");
        final List<Book> books = bookService.findBooksByAuthorAndPrincipalUsername(authorId);
        model.addAttribute("books", books);
        return "/books/author";
    }
}
