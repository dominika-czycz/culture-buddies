package pl.coderslab.cultureBuddies.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorRepository;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyBook;
import pl.coderslab.cultureBuddies.buddies.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;
import pl.coderslab.cultureBuddies.googleapis.restModel.ImageLinks;
import pl.coderslab.cultureBuddies.googleapis.restModel.IndustryIdentifier;
import pl.coderslab.cultureBuddies.googleapis.restModel.VolumeInfo;

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
    public boolean addBookToBuddy(BookFromGoogle bookFromGoogle) throws InvalidDataFromExternalRestApiException, NotExistingRecordException {
        final Optional<Book> bookFromDb = bookRepository.findFirstByIsbn(bookFromGoogle.getId());
        if (bookFromDb.isPresent()) {
            return buddyService.addBook(bookFromDb.get());
        } else {
            return buddyService.addBook(saveBook(bookFromGoogle));
        }
    }


    @Override
    public Book saveBook(BookFromGoogle bookFromGoogle) throws InvalidDataFromExternalRestApiException {
        final String[] authors = bookFromGoogle.getVolumeInfo().getAuthors();
        if (authors == null || authors.length == 0) {
            throw new InvalidDataFromExternalRestApiException("Data obtained from external service are invalid!");
        }
        final Book book = getBook(bookFromGoogle);
        addAuthorsToBook(authors, book);
        try {
            return bookRepository.save(book);
        } catch (ConstraintViolationException | org.hibernate.exception.ConstraintViolationException ex) {
            log.warn("Book from google fails validation: {}", bookFromGoogle);
            log.warn("{}", ex.getMessage());
            throw new InvalidDataFromExternalRestApiException("Data obtained from external service are invalid!");
        }
    }

    private void addAuthorsToBook(String[] authors, Book book) {
        for (String author : authors) {
            final String[] names = author.split(" ");
            String firstName = names[0];
            String lastName = names[names.length - 1];
            final Optional<Author> authorFromDB = authorRepository.findFirstByFirstNameAndLastName(firstName, lastName);
            final Author existingAuthor = authorFromDB.orElseGet(() -> authorRepository.save(Author.builder().firstName(firstName).lastName(lastName).build()));
            book.addAuthor(existingAuthor);
        }
    }

    private Book getBook(BookFromGoogle bookGoogle) throws InvalidDataFromExternalRestApiException {
        final VolumeInfo volInf = bookGoogle.getVolumeInfo();
        if (volInf == null) {
            throw new InvalidDataFromExternalRestApiException("Invalid data from external api");
        }
        final ImageLinks imgLinks = (volInf.getImageLinks() != null) ? volInf.getImageLinks() : new ImageLinks();
        final IndustryIdentifier[] identifiers = volInf.getIndustryIdentifiers();
        if (identifiers == null || identifiers.length == 0 || identifiers[0] == null) {
            throw new InvalidDataFromExternalRestApiException("Invalid data from external api");
        }
        final String isbn = identifiers[0].getIdentifier();
        return Book.builder().title(volInf.getTitle())
                .isbn(isbn)
                .thumbnailLink(imgLinks.getThumbnail()).build();
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
        return buddyBookRepository.findBookRatingWhereAuthorIdAndBuddyId(authorId, buddy.getId());
    }


    @Override
    public List<BuddyBook> findBooksRateOfPrincipalByAuthorId(Long authorId) throws NotExistingRecordException {
        final String principalUsername = buddyService.getPrincipalUsername();
        return findBooksRateWhereAuthorIdAndBuddyUsername(authorId, principalUsername);
    }

    private boolean checkIfAuthorExists(Long authorId) throws NotExistingRecordException {
        final Optional<Author> author = authorRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new NotExistingRecordException("Author with id " + authorId + " does not exist");
        }
        return true;
    }

}
