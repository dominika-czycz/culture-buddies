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
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingNameException;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BookServiceTest {
    @Autowired
    private BookService testObj;
    @MockBean
    private BuddyService buddyService;
    @MockBean
    private AuthorRepository authorRepository;
    @MockBean
    private BookRepository bookRepository;
    @Spy
    private Buddy buddy;
    @Spy
    private Author author;

    @BeforeEach
    void setUp() {
    }


    @Test
    public void givenBuddyAndAuthor_whenSearchingBuddyBookListByAuthor_thenBooksListBeingSearched() throws NotExistingNameException {
        //given
        buddy.setUsername("testBuddy");
        author.setId(10L);
        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(buddyService.findByUsername(buddy.getUsername())).thenReturn(buddy);
        //when
        testObj.findBooksByAuthorAndUsername(buddy.getUsername(), author.getId());
        //then
        verify(bookRepository).findByAuthorsAndBuddies(author, buddy);
    }


}