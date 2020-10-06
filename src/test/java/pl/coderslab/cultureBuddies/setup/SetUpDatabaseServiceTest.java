package pl.coderslab.cultureBuddies.setup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.author.AuthorRepository;
import pl.coderslab.cultureBuddies.books.BookService;
import pl.coderslab.cultureBuddies.buddies.BuddyRelationRepository;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.buddies.RelationStatusRepository;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookRepository;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.events.EventRepository;
import pl.coderslab.cultureBuddies.events.EventService;
import pl.coderslab.cultureBuddies.events.EventTypeRepository;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalServiceException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class SetUpDatabaseServiceTest {
    @Autowired
    private SetUpDatabaseService testObject;
    @MockBean
    private BuddyService buddyService;
    @MockBean
    private BuddyRelationRepository buddyRelationRepository;
    @MockBean
    private BookService bookService;
    @MockBean
    private EventService eventService;
    @MockBean
    private BuddyBookRepository buddyBookRepository;
    @MockBean
    private AuthorRepository authorRepository;
    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private CityRepository cityRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private EventTypeRepository eventTypeRepository;
    @MockBean
    private RelationStatusRepository relationStatusRepository;

    @Test
    void whenStartApplication_thenSetExampleUsersData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        verify(bookService).setExampleBooks();
        verify(buddyService).setExampleBuddies();
        verify(buddyService).setBuddiesRelations();
        verify(bookService).setExampleBookRatings();
        verify(eventService).setExampleEvents();
    }

    @Test
    void whenRestartApplication_thenCleanDatabaseAndSetExampleUsersData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        //when
        testObject.restoreDatabase();
        //then
        verify(buddyBookRepository).deleteAll();
        verify(buddyRelationRepository).deleteAll();
        verify(bookService).deleteAll();
        verify(buddyService).deleteAll();
        verify(authorRepository).deleteAll();
        verify(eventRepository).deleteAll();
        verify(bookService).setExampleBooks();
        verify(buddyService).setExampleBuddies();
        verify(buddyService).setBuddiesRelations();
        verify(bookService).setExampleBookRatings();
        verify(eventService).setExampleEvents();
    }

}


