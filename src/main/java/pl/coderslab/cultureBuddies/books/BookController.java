package pl.coderslab.cultureBuddies.books;

import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookService;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/app/myBooks")
@Slf4j
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BuddyBookService buddyBookService;

    @GetMapping
    public String prepareAllPage(Model model) throws NotExistingRecordException {
        log.info("Preparing myBooks page...");
        final List<Author> authors = bookService.getBooksAuthorsOfPrincipal();
        log.debug("Authors' list with {} positions has been found. ", authors.size());
        model.addAttribute("authors", authors);
        return "/books/myBooks";
    }

    @GetMapping("/{authorId}")
    public String prepareByAuthorPage(Model model, @PathVariable Long authorId) throws NotExistingRecordException {
        log.info("Preparing /app/myBooks/{authorId} page...");
        final List<BuddyBook> booksRating = bookService.findBooksRateOfPrincipalByAuthorId(authorId);
        final Author author = bookService.getAuthorById(authorId);
        model.addAttribute(author);
        model.addAttribute("booksRatingList", booksRating);
        return "/books/author";
    }

    @PostMapping("/search")
    public String prepareFirstResultsPage(@RequestParam String title,
                                          @RequestParam String author,
                                          Model model) throws NotExistingRecordException, BadHttpRequest {
        log.info("Getting books list from google...");
        final int maxPage = bookService.getMaxResultsPage(title, author);
        final int startPage = 0;
        return prepareResultsPage(title, author, model, maxPage, startPage);
    }

    @GetMapping("/search/{pageNo}")
    public String prepareNextResultsPage(@PathVariable Integer pageNo,
                                         @RequestParam String title,
                                         @RequestParam String author,
                                         @RequestParam Integer maxPage,
                                         Model model) throws NotExistingRecordException, BadHttpRequest {
        log.info("Getting books list from google...");
        return prepareResultsPage(title, author, model, maxPage, pageNo);
    }

    private String prepareResultsPage(String title, String author, Model model, int maxPage, int pageNo) throws NotExistingRecordException, BadHttpRequest {
        if (pageNo > maxPage) {
            throw new NotExistingRecordException("No more results to your search!");
        }
        final List<BookFromGoogle> books = bookService.getBooksFromExternalApi(title, author, pageNo);
        log.debug("{} results found", books.size());
        model.addAttribute("gBooks", books);
        model.addAttribute(new Book());
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        return "/books/add";
    }

    @PostMapping("/add")
    public String processAddPage(Book book, Model model)
            throws NotExistingRecordException, InvalidDataFromExternalRestApiException, RelationshipAlreadyCreatedException {
        log.debug("Preparing to add relation between  {} and principal...", book);
        return addBookToBuddy(model, book);
    }

    @PostMapping("/addFromBuddy")
    public String processAddBookFromBuddy(@RequestParam Long bookId, Model model)
            throws NotExistingRecordException, InvalidDataFromExternalRestApiException, RelationshipAlreadyCreatedException {
        log.debug("Preparing to add relation between  book with id {} and principal...", bookId);
        final Book book = bookService.findById(bookId);
        return addBookToBuddy(model, book);
    }

    private String addBookToBuddy(Model model, Book book) throws InvalidDataFromExternalRestApiException, NotExistingRecordException, RelationshipAlreadyCreatedException {
        final BuddyBook buddyBook = bookService.addBookToBuddy(book);
        log.debug("BuddyBook {}", buddyBook);
        model.addAttribute("buddyBookWithId", buddyBook);
        model.addAttribute(new BuddyBook());
        model.addAttribute(book);
        return "/books/rate";
    }

    @PostMapping("/rate")
    public String processAddPage(@Valid BuddyBook buddyBook, BindingResult result, Model model) throws NotExistingRecordException {
        log.debug("Preparing to update entity {}.", buddyBook);
        if (result.hasErrors()) {
            log.warn("Entity {} has failed validation! Return to rate view", buddyBook);
            return "/books/rate";
        }
        buddyBookService.updateBuddyBook(buddyBook);
        return "redirect:/app/myBooks";
    }

    @GetMapping("/delete/{bookId}")
    public String prepareDeletePage(@PathVariable Long bookId, @RequestParam Long authorId,
                                    Model model) throws NotExistingRecordException {
        log.debug("Preparing delete page of relation between principal and book with  id:  {}...", bookId);
        final BuddyBook buddyBook = buddyBookService.findRelationWithPrincipalByBookId(bookId);
        log.debug("Found entities relation:  {}.", buddyBook);
        final Book book = bookService.findById(bookId);
        model.addAttribute("authorId", authorId);
        model.addAttribute(book);
        model.addAttribute(buddyBook);
        return "/books/delete";
    }

    @PostMapping("/delete")
    public String processDeletePage(@RequestParam Long bookId) throws NotExistingRecordException {
        log.debug("Preparing to delete relation of principal and book with id:  {}.", bookId);
        buddyBookService.remove(bookId);
        return "redirect:/app/myBooks";
    }

    @GetMapping("/edit/{bookId}")
    public String prepareEditPage(@PathVariable Long bookId, @RequestParam Long authorId, Model model) throws NotExistingRecordException {
        log.debug("Preparing edit page of relation between between principal and book with id {}", bookId);
        final BuddyBook buddyBook = buddyBookService.findRelationWithPrincipalByBookId(bookId);
        log.debug("Found entities relation:  {}.", buddyBook);
        final Book book = bookService.findByIdWithAuthors(bookId);
        model.addAttribute("authorId", authorId);
        model.addAttribute(book);
        model.addAttribute(buddyBook);
        return "/books/edit";
    }

    @PostMapping("/edit")
    public String processEditPage(@Valid BuddyBook buddyBook, BindingResult result,
                                  @RequestParam Long authorId,
                                  RedirectAttributes attributes) throws NotExistingRecordException {
        log.debug("Preparing to edit entity {}", buddyBook);
        if (result.hasErrors()) {
            log.warn("Entity {} has failed validation! Return to edit view", buddyBook);
            return "/books/edit";
        }
        buddyBookService.updateBuddyBook(buddyBook);
        attributes.addAttribute("authorId", authorId);
        return "redirect:/app/myBooks/{authorId}";
    }

    @GetMapping("/info/{bookId}")
    public String prepareInfoPage(@PathVariable Long bookId, Model model) throws NotExistingRecordException {
        final Book book = bookService.findByIdWithAuthors(bookId);
        final List<BuddyBook> ratings = buddyBookService.findAllPrincipalBuddiesBookRatings(bookId);
        if (ratings.size() > 0) {
            final double averageRating = buddyBookService.countRating(ratings);
            model.addAttribute("averageRating", averageRating);
        }
        model.addAttribute(book);
        model.addAttribute("ratings", ratings);
        return "/books/info";
    }
}

