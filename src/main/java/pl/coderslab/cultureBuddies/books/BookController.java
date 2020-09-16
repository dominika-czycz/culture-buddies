package pl.coderslab.cultureBuddies.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorService;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyBook;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.RestBooksService;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/app/myBooks")
@Slf4j
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final RestBooksService restBooksService;
    private final AuthorService authorService;

    @GetMapping
    public String prepareAllPage(Model model) throws NotExistingRecordException {
        log.info("Preparing myBooks page...");
        final List<Author> authors = authorService.getOrderedAuthorsListOfPrincipalUser();
        model.addAttribute("authors", authors);
        return "/books/myBooks";
    }

    @GetMapping("/{authorId}")
    public String prepareByAuthorPage(Model model, @PathVariable Long authorId) throws NotExistingRecordException {
        log.info("Preparing /app/myBooks/{authorId} page...");
        final List<Book> books = bookService.findBooksByAuthorAndPrincipalUsername(authorId);
        final List<BuddyBook> booksRating = bookService.findBooksRateOfPrincipalByAuthorId(authorId);
        model.addAttribute("books", books);
        model.addAttribute("booksRatingList", booksRating);
        return "/books/author";
    }

    @GetMapping("/add")
    public String prepareAddPage(@RequestParam String title, Model model, RedirectAttributes attributes) throws NotExistingRecordException {
        log.info("Getting books list from google...");
        final List<BookFromGoogle> books = restBooksService.getGoogleBooksListByTitle(title);
        log.debug("{} results found", books.size());
        model.addAttribute("gBooks", books);
        model.addAttribute(new BookFromGoogle());
        attributes.addAttribute("title", title);
        return "/books/add";
    }

    @PostMapping("/add")
    public String processAddPage(@RequestParam String isbn, @RequestParam String title,  RedirectAttributes model) throws NotExistingRecordException, InvalidDataFromExternalRestApiException {
        log.info("Preparing to add book to buddy...");
        log.debug("Looking for book with isbn {}", isbn);
        final BookFromGoogle resultFromGoogle = restBooksService.getGoogleBookByIsbnOrTitle(isbn, title);
        log.debug("Book from google to add {}", resultFromGoogle);
        final boolean isAdded = bookService.addBookToBuddy(resultFromGoogle);
        if (!isAdded) {
            model.addAttribute("info", "The selected book is already in your collection");
        }
        return "redirect:/app/myBooks";
    }
}

