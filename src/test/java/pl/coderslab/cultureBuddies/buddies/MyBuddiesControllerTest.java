package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorService;
import pl.coderslab.cultureBuddies.books.BookService;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.city.CityConverter;
import pl.coderslab.cultureBuddies.events.Address;
import pl.coderslab.cultureBuddies.events.Event;
import pl.coderslab.cultureBuddies.events.EventService;
import pl.coderslab.cultureBuddies.events.EventType;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MyBuddiesController.class)
@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MyBuddiesControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private AuthorService authorServiceMock;
    @MockBean
    private BookService bookServiceMock;
    @MockBean
    private EventService eventService;
    @MockBean
    private CityConverter cityConverter;
    @MockBean
    private BuddyConverter buddyConverter;
    private final List<Buddy> buddies = new ArrayList<>();
    final String username = "someBuddy";
    private final Long buddyId = 10L;
    private static final int RECENT_LIMIT = 20;
    private Buddy buddy;
    private final List<Event> events = new ArrayList<>();
    private final List<Author> authors = new ArrayList<>();

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        buddy = Buddy.builder()
                .id(buddyId)
                .username(username).build();
        when(buddyServiceMock.findById(buddy.getId())).thenReturn(buddy);
        final Event event = Event.builder()
                .title("title")
                .date(LocalDate.now())
                .time(LocalTime.now())
                .eventType(new EventType(1L, "music"))
                .address(new Address(1L, "Wrocław", "Javowa", "10", "b"))
                .build();
        events.add(event);
    }

    @Test
    public void whenAppMyBuddiesUrl_thenMyBuddiesView() throws Exception {
        List<Buddy> inviting = new ArrayList<>();
        when(buddyServiceMock.getBuddiesOfPrincipal())
                .thenReturn(buddies);
        when(buddyServiceMock.getBuddiesInvitingPrincipal())
                .thenReturn(inviting);
        when((authorServiceMock.findAll()))
                .thenReturn(authors);
        mockMvc.perform(get("/app/myBuddies/"))
                .andExpect(status().isOk())
                .andExpect(view().name("/buddy/myBuddies"))
                .andExpect(model().attribute("buddies", buddies))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("inviting", inviting));
    }

    @Test
    public void givenUsernameAndAuthorsIds_whenAppSearchUrl_thenResultsFoundAndAddView() throws Exception {
        ArrayList<Long> authorsIds = new ArrayList<>();
        when(buddyServiceMock.findByUsernameAndAuthors(username, authorsIds))
                .thenReturn(buddies);
        mockMvc.perform(post("/app/myBuddies/search").with(csrf())
                .param("username", username)
                .flashAttr("authorsIds", authorsIds))
                .andExpect(status().isOk())
                .andExpect(view().name("/buddy/add"))
                .andExpect(model().attribute("buddies", buddies))
                .andExpect(model().attribute("newBuddy", new Buddy()));
    }

    @Test
    public void givenNoSearchKey_whenPostToSerachUlr_thenEmptyKeyExceptionAndErrorPageView() throws Exception {
        when(buddyServiceMock.findByUsernameAndAuthors(null, null))
                .thenThrow(EmptyKeysException.class);
        mockMvc.perform(post("/app/myBuddies/search").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/errors/error"));
    }

    @Test
    public void givenBuddyId_whenPostAdd_ThenInviteBuddy() throws Exception {
        mockMvc.perform(post("/app/myBuddies/add").with(csrf())
                .param("buddyId", buddyId.toString()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myBuddies/"));
        verify(buddyServiceMock).inviteBuddy(buddyId);
    }

    @Test
    public void givenBuddyId_whenPostAccept_thenAcceptBuddy() throws Exception {
        mockMvc.perform(post("/app/myBuddies/accept").with(csrf())
                .param("buddyId", buddyId.toString()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myBuddies/"));
        verify(buddyServiceMock).acceptBuddy(buddyId);
    }


    @Test
    public void givenBuddyId_whenGetDelete_thenDeleteView() throws Exception {
        mockMvc.perform(get("/app/myBuddies/delete/" + buddyId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buddy", buddy))
                .andExpect(view().name("/buddy/delete"));
    }

    @Test
    public void givenBuddyId_whenPostDelete_thenDeleteBuddy() throws Exception {
        mockMvc.perform(post("/app/myBuddies/delete").with(csrf())
                .param("buddyId", buddyId.toString()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myBuddies/"));
        verify(buddyServiceMock).deleteBuddy(buddyId);
    }

    @Test
    public void givenBuddyId_whenGetBlock_thenBlockView() throws Exception {
        mockMvc.perform(get("/app/myBuddies/block/" + buddyId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buddy", buddy))
                .andExpect(view().name("/buddy/block"));
    }

    @Test
    public void givenBuddyId_whenPostBlock_thenBlockBuddy() throws Exception {
        mockMvc.perform(post("/app/myBuddies/block").with(csrf())
                .param("buddyId", buddyId.toString()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myBuddies/"));
        verify(buddyServiceMock).block(buddyId);
    }

    @Test
    public void givenBuddyId_whenGetInfo_thenInfoView() throws Exception {
        when(eventService.findRecentOfBuddy(buddy.getId(), RECENT_LIMIT)).thenReturn(events);
        mockMvc.perform(get("/app/myBuddies/info/" + buddyId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buddy", buddy))
                .andExpect(model().attribute("recentEvents", events))
                .andExpect(view().name("/buddy/info"));
        verify(buddyServiceMock).findById(buddyId);
        verify(eventService).findRecentOfBuddy(buddyId, RECENT_LIMIT);
    }

    @Test
    public void givenBuddyId_whenGetBooks_thenBuddyBooksView() throws Exception {
        when(bookServiceMock.getBooksAuthorsOfBuddy(buddyId)).thenReturn(authors);
        mockMvc.perform(get("/app/myBuddies/books/" + buddyId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buddy", buddy))
                .andExpect(model().attribute("authors", authors))
                .andExpect(view().name("/buddy/buddyBooks"));
        verify(buddyServiceMock).findById(buddyId);
        verify(bookServiceMock).getBooksAuthorsOfBuddy(buddyId);
    }

    @Test
    public void givenBuddyId_whenGetAuthors_thenBuddyAuthorsView() throws Exception {
        final Author author = Author.builder().id(12L).build();
        List<BuddyBook> buddyBooks = new ArrayList<>();
        when(bookServiceMock.findBooksRateOfBuddyByAuthorId(buddy.getId(), author.getId()))
                .thenReturn(buddyBooks);
        when(bookServiceMock.getAuthorById(author.getId())).thenReturn(author);
        mockMvc.perform(get("/app/myBuddies/authors/" + author.getId())
                .param("buddyId", String.valueOf(buddyId)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buddy", buddy))
                .andExpect(model().attribute("author", author))
                .andExpect(view().name("/buddy/buddyAuthor"));
        verify(buddyServiceMock).findById(buddyId);
        verify(bookServiceMock).findBooksRateOfBuddyByAuthorId(buddy.getId(), author.getId());
        verify(bookServiceMock).getAuthorById(author.getId());
    }

}