package pl.coderslab.cultureBuddies.author;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.setup.SetUpDatabaseService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AuthorServiceImplTest {
    @Autowired
    private AuthorService testObj;
    @MockBean
    private AuthorRepository authorRepositoryMock;
    @MockBean
    private BuddyService buddyServiceMock;
    @Spy
    private Author author;
    @Spy
    private Buddy buddy;

    @BeforeEach
    public void setUp() {
        author.setId(12040L);
    }

    @Test
    public void givenExistingAuthorId_whenCheckingIfAuthorExists_thenTrue()
            throws NotExistingRecordException {
        //given
        when(authorRepositoryMock.findById(author.getId())).thenReturn(Optional.of(author));
        //when
        final boolean actual = testObj.checkIfAuthorExists(author.getId());
        //then
        assertTrue(actual);
    }

    @Test
    public void givenNotExistingAuthorId_whenCheckingIfAuthorExists_thenNotExistingRecordException() {
        //given
        when(authorRepositoryMock.findById(author.getId())).thenReturn(Optional.empty());
        //when,then
        assertThrows(NotExistingRecordException.class,
                () -> testObj.findById(author.getId()));
    }

    @Test
    public void givenNotExistingAuthorFullName_whenSaving_thenAuthorSaved() {
        //given
        String firstName = "Jan";
        String lastName = "Kowalski";
        when(authorRepositoryMock.findFirstByFirstNameAndLastName(firstName, lastName))
                .thenReturn(Optional.empty());
        final Author authorToSave = Author.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        //when
        testObj.saveIfNotExistYet(firstName, lastName);
        //then
        verify(authorRepositoryMock).save(authorToSave);
    }

    @Test
    public void whenLookingForPrincipalAuthors_thenAuthorsListFound() throws NotExistingRecordException {
        buddy.setId(213L);
        when(buddyServiceMock.getPrincipal()).thenReturn(buddy);
        final Author author1 = new Author();
        final List<Author> authors = List.of(this.author, author1);
        when(authorRepositoryMock.findByBuddyIdOrderByLastName(buddy.getId()))
                .thenReturn(authors);
        testObj.getOrderedAuthorsListOfPrincipalUser();
        verify(authorRepositoryMock).findByBuddyIdOrderByLastName(buddy.getId());
    }

}