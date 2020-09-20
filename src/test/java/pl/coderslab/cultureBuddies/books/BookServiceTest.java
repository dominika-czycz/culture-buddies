package pl.coderslab.cultureBuddies.books;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorService;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookService;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.googleapis.restModel.ImageLinks;
import pl.coderslab.cultureBuddies.googleapis.restModel.IndustryIdentifier;
import pl.coderslab.cultureBuddies.googleapis.restModel.VolumeInfo;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BookServiceTest {
    @Autowired
    private BookService testObj;
    @MockBean
    private BookRepository bookRepositoryMock;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private BuddyBookService buddyBookServiceMock;
    @MockBean
    private AuthorService authorServiceMock;

    @Spy
    private Buddy buddy;
    @Spy
    private Author author;
    @Spy
    private BuddyBook buddyBook;
    private Book book;

    @BeforeEach
    public void setUp() throws NotExistingRecordException {
        buddy.setUsername("testBuddy");
        author.setId(10L);

        String isbn = "8374955422";
        final IndustryIdentifier industryIdentifier = new IndustryIdentifier();
        industryIdentifier.setIdentifier(isbn);
        final IndustryIdentifier[] industryIdentifiers = {industryIdentifier};
        final VolumeInfo volumeInfo = VolumeInfo.builder()
                .imageLinks(new ImageLinks())
                .industryIdentifiers(industryIdentifiers)
                .authors(new String[]{"Jan Kowalski", "Piotr Nowak"})
                .build();
        book = Book.builder()
                .title("title")
                .identifier(isbn)
                .authorsFullName(Collections.singletonList("Jan Kowalski"))
                .thumbnailLink(volumeInfo.getImageLinks().getThumbnail()).build();
        when(authorServiceMock.checkIfAuthorExists(author.getId())).thenReturn(true);
        when(authorServiceMock.findById(author.getId())).thenReturn(author);
        when(buddyServiceMock.findByUsername(buddy.getUsername())).thenReturn(buddy);
    }

    @Test
    public void givenBuddyAndAuthor_whenSearchingBuddyBookListByAuthor_thenBooksListBeingSearched() throws NotExistingRecordException {
        //when
        testObj.findBooksByUsernameAndAuthorId(buddy.getUsername(), author.getId());
        //then
        verify(bookRepositoryMock).findByAuthorIdAndBookId(author.getId(), buddy.getId());
    }

    @Test
    public void givenAuthorIdAndBuddyUsername_whenSearchingBooksRate_thenBooksListBeingSearched() throws NotExistingRecordException {
        //given
        when(buddyBookServiceMock.getRatingWhereAuthorIdAndBuddy(author.getId(), buddy))
                .thenReturn(Collections.singletonList(buddyBook));
        //when
        testObj.findBooksRateWhereAuthorIdAndBuddyUsername(author.getId(), buddy.getUsername());
        //then
        verify(buddyBookServiceMock).getRatingWhereAuthorIdAndBuddy(author.getId(), buddy);
    }

    @Test
    public void givenInvalidBookFromGoogle_whenBookBeingSaved_thenConstraintViolationExceptionThrown() {
        //given
        when(bookRepositoryMock.save(book)).thenThrow(ConstraintViolationException.class);
        //when,then
        assertThrows(InvalidDataFromExternalRestApiException.class, () -> testObj.addBookToBuddy(book));
    }

    @Test
    public void givenBookNotRelatedWithBuddy_whenBookAddedToBuddy_thenNewRelationCreated() throws InvalidDataFromExternalRestApiException, NotExistingRecordException, RelationshipAlreadyCreatedException {
        //given
        when(bookRepositoryMock.findFirstByIdentifier(book.getIdentifier())).thenReturn(Optional.ofNullable(book));
        when(buddyServiceMock.addBookToPrincipalBuddy(book)).thenReturn(buddyBook);
        //when
        final BuddyBook createdBuddyBook = testObj.addBookToBuddy(book);
        //then
        verify(buddyServiceMock).addBookToPrincipalBuddy(book);
        assertThat(createdBuddyBook, is(buddyBook));
    }
}