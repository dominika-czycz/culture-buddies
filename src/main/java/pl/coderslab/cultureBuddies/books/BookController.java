package pl.coderslab.cultureBuddies.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorService;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookService;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.googleapis.RestBooksService;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/app/myBooks")
@Slf4j
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final RestBooksService restBooksService;
    private final AuthorService authorService;
    private final BuddyBookService buddyBookService;

    @GetMapping
    public String prepareAllPage(Model model) throws NotExistingRecordException {
        log.info("Preparing myBooks page...");
        final List<Author> authors = authorService.getOrderedAuthorsListOfPrincipalUser();
        log.debug("Authors' list with {} positions has been found. ", authors.size());
        model.addAttribute("authors", authors);
        return "/books/myBooks";
    }

    @GetMapping("/{authorId}")
    public String prepareByAuthorPage(Model model, @PathVariable Long authorId) throws NotExistingRecordException {
        log.info("Preparing /app/myBooks/{authorId} page...");
        final List<BuddyBook> booksRating = bookService.findBooksRateOfPrincipalByAuthorId(authorId);
        final Author author = authorService.findById(authorId);
        model.addAttribute(author);
        model.addAttribute("booksRatingList", booksRating);
        return "/books/author";
    }

    @GetMapping("/add")
    public String prepareAddPage(@RequestParam String title, Model model) throws NotExistingRecordException {
        log.info("Getting books list from google...");
        final List<BookFromGoogle> books = restBooksService.getGoogleBooksListByTitle(title);
        log.debug("{} results found", books.size());
        model.addAttribute("gBooks", books);
        model.addAttribute(new Book());
        return "/books/add";
    }

    @PostMapping("/add")
    public String processAddPage(Book book, Model model)
            throws NotExistingRecordException, InvalidDataFromExternalRestApiException, RelationshipAlreadyCreatedException {
        log.debug("Preparing to add relation between  {} and principal...", book);
        final BuddyBook buddyBook = bookService.addBookToBuddy(book);
        log.debug("BuddyBook {}", buddyBook);
        model.addAttribute("buddyBookWithId", buddyBook);
        model.addAttribute(new BuddyBook());
        model.addAttribute(book);
        return "/books/rate";
    }

    @PostMapping("/rate")
    public String processAddPage(@Valid BuddyBook buddyBook, BindingResult result) throws NotExistingRecordException {
        log.debug("Preparing to update entity {}.", buddyBook);
        if (result.hasErrors()) {
            log.warn("Entity {} has failed validation! Return to rate view", buddyBook);
            return "/books/rate";
        }
        buddyBookService.updateBuddyBook(buddyBook);
        return "redirect:/app/myBooks";
    }

    @GetMapping("/delete/{bookId}")
    public String prepareDeletePage(@PathVariable Long bookId, Model model) throws NotExistingRecordException {
        log.debug("Preparing to delete principal relation with book of id:  {}...", bookId);
        final BuddyBook buddyBook = buddyBookService.findRelationWithPrincipalByBookId(bookId);
        log.debug("Preparing to delete buddy-book relation with id {}...", buddyBook);
        final Book book = bookService.findByIdWithAuthors(bookId);
        final Long authorId = book.getAuthors().iterator().next().getId();
        model.addAttribute("authorId", authorId);
        model.addAttribute(book);
        model.addAttribute(buddyBook);
        return "/books/delete";
    }

    @PostMapping("/delete")
    public String processDeletePage(@RequestParam Long bookId) throws NotExistingRecordException {
        log.debug("Preparing to delete relation principal and book of id={}.", bookId);
        buddyBookService.remove(bookId);
        return "redirect:/app/myBooks";
    }
}

