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
import pl.coderslab.cultureBuddies.author.AuthorRepository;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyBook;
import pl.coderslab.cultureBuddies.buddies.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BookServiceTest {
    @Autowired
    private BookService testObj;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private AuthorRepository authorRepositoryMock;
    @MockBean
    private BookRepository bookRepositoryMock;
    @MockBean
    private BuddyBookRepository buddyBookRepositoryMock;
    @Spy
    private Buddy buddy;
    @Spy
    private Author author;
    @Spy
    private BuddyBook buddyBook;

    @BeforeEach
    public void setUp() {
        buddy.setUsername("testBuddy");
        author.setId(10L);
    }

    @Test
    public void givenBuddyAndAuthor_whenSearchingBuddyBookListByAuthor_thenBooksListBeingSearched() throws NotExistingRecordException {
        //given
        when(authorRepositoryMock.findById(author.getId())).thenReturn(Optional.of(author));
        when(buddyServiceMock.findByUsername(buddy.getUsername())).thenReturn(buddy);
        //when
        testObj.findBooksByAuthorAndUsername(buddy.getUsername(), author.getId());
        //then
        verify(bookRepositoryMock).findByAuthorIdAndBookId(author.getId(), buddy.getId());
    }

    @Test
    public void givenAuthorIdAndBuddyUsername_whenSearchingBooksRate_thenBooksListBeingSearched() throws NotExistingRecordException {
        //given
        when(authorRepositoryMock.findById(author.getId())).thenReturn(Optional.of(author));
        when(buddyBookRepositoryMock.findBookRatingWhereAuthorIdAndBuddyId(author.getId(), buddy.getId())).thenReturn(Collections.singletonList(buddyBook));
        when(buddyServiceMock.findByUsername(buddy.getUsername())).thenReturn(buddy);
        //when
        testObj.findBooksRateWhereAuthorIdAndBuddyUsername(author.getId(), buddy.getUsername());
        //then
        verify(buddyBookRepositoryMock).findBookRatingWhereAuthorIdAndBuddyId(author.getId(), buddy.getId());
    }
}