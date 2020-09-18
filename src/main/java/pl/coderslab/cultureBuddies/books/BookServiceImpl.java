package pl.coderslab.cultureBuddies.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorRepository;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BuddyService buddyService;
    private final BuddyBookRepository buddyBookRepository;

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
            final Optional<Author> authorFromDB = authorRepository.findFirstByFirstNameAndLastName(firstName, lastName);
            final Author existingAuthor = authorFromDB.orElseGet(() -> authorRepository.save(Author.builder().firstName(firstName).lastName(lastName).build()));
            book.addAuthor(existingAuthor);
        }
    }

    @Override
    public List<Book> findBooksByAuthorAndUsername(String username, Long authorId) throws NotExistingRecordException {
        final Buddy buddy = buddyService.findByUsername(username);
        checkIfAuthorExists(authorId);
        return bookRepository.findByAuthorIdAndBookId(authorId, buddy.getId());
    }

    @Override
    public List<Book> findBooksByAuthorAndPrincipalUsername(Long authorId) throws NotExistingRecordException {
        final String username = buddyService.getPrincipalUsername();
        return findBooksByAuthorAndUsername(username, authorId);
    }

    @Override
    public List<BuddyBook> findBooksRateWhereAuthorIdAndBuddyUsername(Long authorId, String username) throws NotExistingRecordException {
        checkIfAuthorExists(authorId);
        final Buddy buddy = buddyService.findByUsername(username);
        return buddyBookRepository.findRatingWhereAuthorIdAndBuddyId(authorId, buddy.getId());
    }

    @Override
    public List<BuddyBook> findBooksRateOfPrincipalByAuthorId(Long authorId) throws NotExistingRecordException {
        final String principalUsername = buddyService.getPrincipalUsername();
        return findBooksRateWhereAuthorIdAndBuddyUsername(authorId, principalUsername);
    }

    private void checkIfAuthorExists(Long authorId) throws NotExistingRecordException {
        final Optional<Author> author = authorRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new NotExistingRecordException("Author with id " + authorId + " does not exist");
        }
    }
}
