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
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalServiceException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.googleapis.RestBooksService;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
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
    public List<Author> getBooksAuthorsOfBuddy(Long buddyId) {
        return authorService.getOrderedAuthorsListOfBuddy(buddyId);
    }

    @Override
    public List<BuddyBook> findBooksRateOfBuddyByAuthorId(Long buddyId, Long authorId) throws NotExistingRecordException {
        @NotBlank final String username = buddyService.findById(buddyId).getUsername();
        return findBooksRateWhereAuthorIdAndBuddyUsername(authorId, username);
    }

    @Override
    public void saveAll(List<Book> books) throws InvalidDataFromExternalServiceException {
        for (Book book : books) {
            saveBook(book);
        }
    }

    @Override
    public Book findByTitle(String bookTitle) throws NotExistingRecordException {
        return bookRepository.findFirstByTitle(bookTitle)
                .orElseThrow(new NotExistingRecordException("Book with title " + bookTitle +
                        "does not exist in database!"));

    }

    @Override
    public void deleteAll() {
        bookRepository.deleteAll();
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
    public BuddyBook addBookToBuddy(Book book) throws InvalidDataFromExternalServiceException, NotExistingRecordException, RelationshipAlreadyCreatedException {
        final Optional<Book> bookFromDb = bookRepository.findFirstByIdentifier(book.getIdentifier());
        if (bookFromDb.isPresent()) {
            return buddyService.addBookToPrincipalBuddy(bookFromDb.get());
        } else {
            final Book newlySavedBook = saveBook(book);
            return buddyService.addBookToPrincipalBuddy(newlySavedBook);
        }
    }

    @Override
    @Transactional
    public void setExampleBookRatings() throws NotExistingRecordException {
        saveExampleRating("annaKowal", "lolita",
                "Pushing the boundaries of what acceptable literature can actually be, Lolita is very much a piece of art. For many years I kept hearing about this book, the content sounding disturbing and perhaps even slightly fascinating. ",
                10);
        saveExampleRating("Mazur", "lolita",
                "Sick, twisted and beautiful. Love this.",
                9);
        saveExampleRating("annaKowal", "Ada or Ardor",
                "“Maybe the only thing that hints at a sense of Time is rhythm; not the recurrent beats of the rhythm but the gap between two such beats, the gray gap between black beats: the Tender Interval.” ",
                10);
        saveExampleRating("koala", "Beloved",
                "Just love it ",
                10);
        saveExampleRating("annaKowal", "Beloved",
                "The brutal truth, brilliantly written. A mother hanging from a tree, the vile debasement of a nursing mother, scars so deep from whipping that they make a design of a tree on a woman’s back.",
                9);
        saveExampleRating("Mazur", "Big Breasts and Wide Hips",
                "I loved the homage to the female- mother, nurturer, life giver, sacrificial lamb. Yan is brilliant in his use of allegories in the tale of the Shangguan family (specifically Mother and Jintong) from a China (Motherland) violated by war, suffering famine.",
                10);
        saveExampleRating("koala", "The Road",
                "The Road is a truly disturbing book; it is absorbing, mystifying and completely harrowing. Simply because it shows us how man could act given the right circumstances; it’s a terrifying concept because it could also be a true one.",
                10);
        saveExampleRating("Mazur", "The Piano Teacher",
                "AMAZING THINGS: I can literally feel new wrinkles spreading across the surface of my brain when I read this guy. He's so wicked smart that there's no chance he's completely sane. His adjectives and descriptions are 100% PERFECT, and yet entirely nonsensic",
                10);
        saveExampleRating("koala", "The Piano Teacher",
                "good", 7);
        saveExampleRating("annaKowal", "The Piano Teacher",
                "Love it",
                10);
        saveExampleRating("adamski", "The Piano Teacher",
                "This is one of my favorite books. I can't even describe how amazed I was when I finished this book. Jelinek moves the reader from character to character, rarely telling us who we inhabit, yet unlike so many other books that abuse this device, it works.",
                10);
        saveExampleRating("adamski", "Mistrz i Małgorzata",
                "Stories, stories, all is stories: political stories, religious stories, scientific stories, even stories about stories. We live inside these stories. Like this one in The Master and Margarita. The story that we can more or less agree upon we call reality.", 9);
    }

    @Override
    @Transactional
    public void setExampleBooks() throws InvalidDataFromExternalServiceException {
        final Book lolita = getBook("Lolita", "9780141391601", "http://books.google.com/books/content?id=S0lVyYcw8tsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Vladimir Nabokov");
        final Book adaOrArdor = getBook("Ada or Ardor", "9780141911304", "http://books.google.com/books/content?id=SBQgSToNcUsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Vladimir Nabokov");
        final Book beloved = getBook("Beloved", "030738862X", "http://books.google.com/books/content?id=sfmp6gjZGP8C&printsec=frontcover&img=1&zoom=1&source=gbs_api", "Toni Morrison");
        final Book theRepublicOfWine = getBook("The Republic of Wine", "9781743771884", "http://books.google.com/books/content?id=F8m_DAAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "Mo Yan");
        final Book bigBreastsAndWideHips = getBook("Big Breasts and Wide Hips", "1559706724", "http://books.google.com/books/content?id=fe5wAkv1snoC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Mo Yan");
        final Book theRoad = getBook("The Road", "9781529014587", "http://books.google.com/books/content?id=VcZtDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Cormac McCarthy");
        final Book thePianoTeacher = getBook("The Piano Teacher", "9781847653062", "http://books.google.com/books/content?id=d_Ady-4CHRIC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Elfriede Jelinek");
        final Book masterAndMargaret = getBook("Mistrz i Małgorzata", "8373161503", null, "Michaił Bułkahow");
        final List<Book> books = List.of(lolita, adaOrArdor, beloved, theRepublicOfWine, bigBreastsAndWideHips,
                theRoad, thePianoTeacher, masterAndMargaret);
        saveAll(books);
    }

    private Book getBook(String lolita2, String s, String s2, String s3) {
        return Book.builder()
                .title(lolita2)
                .identifier(s)
                .thumbnailLink(s2)
                .authorsFullName(Collections.singletonList(s3))
                .build();
    }

    private void saveExampleRating(String username, String bookTitle, String comment, int rate) throws NotExistingRecordException {
        final Buddy buddy = buddyService.findByUsername(username);
        Book book = findByTitle(bookTitle);
        final BuddyBook buddyBook = new BuddyBook();
        buddyBook.setComment(comment);
        buddyBook.setRate(rate);
        buddyBook.setBook(book);
        buddyBook.setBuddy(buddy);
        buddyBookService.save(buddyBook);
    }

    private Book saveBook(Book book) throws InvalidDataFromExternalServiceException {
        final List<String> authorsFullName = checkAuthors(book);
        addAuthorsToBook(book, authorsFullName);
        log.debug("Saving entity {}...", book);
        try {
            final Book savedBook = bookRepository.save(book);
            log.debug("Entity {} has been saved", book);
            return savedBook;
        } catch (ConstraintViolationException | org.hibernate.exception.ConstraintViolationException ex) {
            log.warn("Book fails validation: {}", book);
            log.warn("{}", ex.getMessage());
            throw new InvalidDataFromExternalServiceException("Data obtained from external service are invalid!");
        }
    }

    private List<String> checkAuthors(Book book) throws InvalidDataFromExternalServiceException {
        final List<String> authorsFullName = book.getAuthorsFullName();
        if (authorsFullName == null || authorsFullName.isEmpty()) {
            throw new InvalidDataFromExternalServiceException("Data obtained from external service are invalid!");
        }
        return authorsFullName;
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

    private List<BuddyBook> findBooksRateWhereAuthorIdAndBuddyUsername(Long authorId, String username) throws NotExistingRecordException {
        if (authorService.checkIfAuthorExists(authorId)) {
            final Buddy buddy = buddyService.findByUsername(username);
            return buddyBookService.getRatingsWhereAuthorIdAndBuddy(authorId, buddy);
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
