package pl.coderslab.cultureBuddies.books;

import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorService;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookService;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.googleapis.RestBooksService;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BuddyService buddyService;
    private final BuddyBookService buddyBookService;
    private final RestBooksService restBooksService;
    private final AuthorService authorService;

    @Override
    public int getMaxResultsPage(String title, String author) throws NotExistingRecordException {
        return restBooksService.countMaxPage(title, author);
    }

    @Override
    public List<Author> getBooksAuthorsOfPrincipal() throws NotExistingRecordException {
        return authorService.getOrderedAuthorsListOfPrincipalUser();
    }

    @Override
    public List<BookFromGoogle> getBooksFromExternalApi(String title, String author, int pageNo) throws NotExistingRecordException, BadHttpRequest {
        return restBooksService.getGoogleBooksList(title, author, pageNo);
    }

    @Override
    public Author getAuthorById(Long authorId) throws NotExistingRecordException {
        return authorService.findById(authorId);
    }

    @Override
    public BuddyBook addBookToBuddy(Book book) throws InvalidDataFromExternalRestApiException, NotExistingRecordException, RelationshipAlreadyCreatedException {
        final Optional<Book> bookFromDb = bookRepository.findFirstByIdentifier(book.getIdentifier());
        if (bookFromDb.isPresent()) {
            return buddyService.addBookToPrincipalBuddy(bookFromDb.get());
        } else {
            final Book newlySavedBook = saveBook(book);
            return buddyService.addBookToPrincipalBuddy(newlySavedBook);
        }
    }

    private Book saveBook(Book book) throws InvalidDataFromExternalRestApiException {
        final List<String> authorsFullName = book.getAuthorsFullName();
        if (authorsFullName == null || authorsFullName.isEmpty()) {
            throw new InvalidDataFromExternalRestApiException("Data obtained from external service are invalid!");
        }
        addAuthorsToBook(book, authorsFullName);
        log.debug("Saving entity {}...", book);
        try {
            final Book savedBook = bookRepository.save(book);
            log.debug("Entity {} has been saved", book);
            return savedBook;
        } catch (ConstraintViolationException | org.hibernate.exception.ConstraintViolationException ex) {
            log.warn("Book from google fails validation: {}", book);
            log.warn("{}", ex.getMessage());
            throw new InvalidDataFromExternalRestApiException("Data obtained from external service are invalid!");
        }
    }

    private void addAuthorsToBook(Book book, List<String> authors) {
        for (String author : authors) {
            final String[] names = author.split(" ");
            String firstName = names[0];
            String lastName = names[names.length - 1];
            final Author existingAuthor = authorService.saveIfNotExistYet(firstName, lastName);
            book.addAuthor(existingAuthor);
        }
    }

    @Override
    public List<Book> findBooksByUsernameAndAuthorId(String username, Long authorId) throws NotExistingRecordException {
        final Buddy buddy = buddyService.findByUsername(username);
        if (authorService.checkIfAuthorExists(authorId)) {
            return bookRepository.findByAuthorIdAndBookId(authorId, buddy.getId());
        }
        throw new NotExistingRecordException("Author with id " + authorId + " does not exist!");
    }

    @Override
    public List<BuddyBook> findBooksRateWhereAuthorIdAndBuddyUsername(Long authorId, String username) throws NotExistingRecordException {
        if (authorService.checkIfAuthorExists(authorId)) {
            final Buddy buddy = buddyService.findByUsername(username);
            return buddyBookService.getRatingWhereAuthorIdAndBuddy(authorId, buddy);
        }
        throw new NotExistingRecordException("Author with id " + authorId + " does not exist!");
    }

    @Override
    public List<BuddyBook> findBooksRateOfPrincipalByAuthorId(Long authorId) throws NotExistingRecordException {
        final String principalUsername = buddyService.getPrincipalUsername();
        return findBooksRateWhereAuthorIdAndBuddyUsername(authorId, principalUsername);
    }

    @Override
    public Book findById(Long id) throws NotExistingRecordException {
        final Optional<Book> book = bookRepository.findById(id);
        return book.orElseThrow(new NotExistingRecordException("Book with id " + id + " does not exist in database!"));
    }

    @Override
    public Book findByIdWithAuthors(Long bookId) {
        return bookRepository.findByIdWithAuthors(bookId);
    }
}
