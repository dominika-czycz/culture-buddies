package pl.coderslab.cultureBuddies.events;

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
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyConverter;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.city.CityConverter;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventController.class)
@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventService eventServiceMock;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private CityConverter cityConverter;
    @MockBean
    private BuddyConverter buddyConverter;

    private static final int RECENTLY_LIMIT = 20;
    private final List<EventType> types = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private final List<Event> joined = new ArrayList<>();
    private Buddy buddy;
    private Event unsavedValidEvent;
    private Event savedEvent;

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        buddy = Buddy.builder().id(122L)
                .username("Principal").build();
        final Address address = Address.builder()
                .city("Wrocław")
                .street("Ruska")
                .number("10c").build();
        unsavedValidEvent = Event.builder()
                .title("new title")
                .date(LocalDate.now().plus(Period.ofMonths(1)))
                .stringTime("12:12")
                .address(address)
                .description("description")
                .eventType(new EventType(2L, "cinema"))
                .buddy(buddy)
                .buddies(Set.of(buddy))
                .build();
        savedEvent = unsavedValidEvent.toBuilder()
                .id(233L)
                .time(LocalTime.now())
                .build();
        when(eventServiceMock.findAllEventsTypes()).thenReturn(types);
        when(eventServiceMock.getEventsOfPrincipal()).thenReturn(events);
        when(eventServiceMock.getJoinedEvents()).thenReturn(joined);
        when(buddyServiceMock.getPrincipal()).thenReturn(buddy);
        when(eventServiceMock.findEventById(savedEvent.getId())).thenReturn(savedEvent);
        when(eventServiceMock.findEventByIdWithBuddies(savedEvent.getId())).thenReturn(savedEvent);
    }

    @Test
    public void whenAppMyEventsUrl_thenEventsView() throws Exception {
        mockMvc.perform(get("/app/myEvents/"))
                .andExpect(status().isOk())
                .andExpect(view().name("/events/events"))
                .andExpect(model().attribute("types", types))
                .andExpect(model().attribute("events", events))
                .andExpect(model().attribute("joined", joined))
                .andExpect(model().attribute("buddy", buddy));
        verify(eventServiceMock).findAllEventsTypes();
        verify(eventServiceMock).getEventsOfPrincipal();
        verify(eventServiceMock).getJoinedEvents();
        verify(buddyServiceMock).getPrincipal();
    }

    @Test
    public void whenGetAdd_thenAddView() throws Exception {
        mockMvc.perform(get("/app/myEvents/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("/events/add"))
                .andExpect(model().attribute("event", new Event()))
                .andExpect(model().attribute("types", types))
                .andExpect(model().attribute("buddy", buddy));
        verify(eventServiceMock).findAllEventsTypes();
        verify(buddyServiceMock).getPrincipal();
    }

    @Test
    public void givenValidEvent_whenPostAdd_thenEventSave() throws Exception {
        mockMvc.perform(post("/app/myEvents/add").with(csrf())
                .flashAttr("event", unsavedValidEvent))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myEvents/"));
        verify(eventServiceMock).findAllEventsTypes();
        verify(buddyServiceMock).getPrincipal();
        verify(eventServiceMock).save(unsavedValidEvent);
    }

    @Test
    public void givenEventId_whenGetEdit_thenEditView() throws Exception {
        mockMvc.perform(get("/app/myEvents/edit/" + savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("event", savedEvent))
                .andExpect(view().name("/events/edit"));
        verify(eventServiceMock).findEventById(savedEvent.getId());
    }

    @Test
    public void givenEventId_whenPostEdit_thenUpdateEvent() throws Exception {
        mockMvc.perform(post("/app/myEvents/edit/").with(csrf())
                .flashAttr("event", savedEvent))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myEvents/"));
        verify(eventServiceMock).updateEvent(savedEvent);
    }

    @Test
    public void givenEventId_whenGetDelete_thenDeleteView() throws Exception {
        mockMvc.perform(get("/app/myEvents/delete/" + savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("event", savedEvent))
                .andExpect(view().name("/events/delete"));
        verify(eventServiceMock).findEventByIdWithBuddies(savedEvent.getId());
    }

    @Test
    public void givenEventId_whenPostDelete_thenDeleteEvent() throws Exception {
        mockMvc.perform(post("/app/myEvents/delete/").with(csrf())
                .param("eventId", savedEvent.getId().toString()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myEvents/"));
        verify(eventServiceMock).remove(savedEvent.getId());
    }

    @Test
    public void givenEventId_whenGetCancel_thenCancelView() throws Exception {
        mockMvc.perform(get("/app/myEvents/cancel/" + savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("event", savedEvent))
                .andExpect(view().name("/events/cancel"));
        verify(eventServiceMock).findEventById(savedEvent.getId());
    }

    @Test
    public void givenEventId_whenPostCancel_thenLeaveEvent() throws Exception {
        mockMvc.perform(post("/app/myEvents/cancel/").with(csrf())
                .param("eventId", savedEvent.getId().toString()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myEvents/"));
        verify(eventServiceMock).leave(savedEvent.getId());
    }

    @Test
    public void givenEventId_whenPostJoin_thenJoinEvent() throws Exception {
        mockMvc.perform(post("/app/myEvents/join/").with(csrf())
                .param("eventId", savedEvent.getId().toString()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myEvents/"));
        verify(eventServiceMock).joinEvent(savedEvent.getId());
    }

    @Test
    public void givenEventId_whenGetInfo_thenInfoView() throws Exception {
        mockMvc.perform(get("/app/myEvents/info/" + savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("event", savedEvent))
                .andExpect(view().name("/events/info"));
        verify(eventServiceMock).findEventByIdWithBuddies(savedEvent.getId());
    }

    @Test
    public void givenUsernameTitleEventTypeIdAndCity_whenPostSearch_thenEventBeingSearched() throws Exception {
        //given
        Long typeId = 9L;
        String title = savedEvent.getTitle();
        String username = buddy.getUsername();
        String city = savedEvent.getAddress().getCity();
        List<Event> eventsToJoin = List.of(savedEvent);
        when(eventServiceMock.findByUsernameTitleTypeIdOrCity(username, title, typeId, city))
                .thenReturn(eventsToJoin);
        //when, then
        mockMvc.perform(post("/app/myEvents/search/").with(csrf())
                .param("username", username)
                .param("city", city)
                .param("title", title)
                .param("typeId", typeId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/events/join"))
                .andExpect(model().attribute("eventsToJoin", eventsToJoin));
        verify(eventServiceMock)
                .findByUsernameTitleTypeIdOrCity(username, title, typeId, city);
    }

    @Test
    public void whenGetRecentlyAdded_thenRecentlyAddedView() throws Exception {
        List<Event> recentEvents = List.of(savedEvent);
        when(eventServiceMock.findRecentlyAddedByBuddies(RECENTLY_LIMIT))
                .thenReturn(recentEvents);
        mockMvc.perform(get("/app/myEvents/recentlyAdded"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("recentEvents", recentEvents))
                .andExpect(view().name("/events/recentlyAdded"));
        verify(eventServiceMock).findRecentlyAddedByBuddies(RECENTLY_LIMIT);
    }

    @Test
    public void givenBuddyId_whenGetRecentOfBuddy_thenBuddyEventsView() throws Exception {
        List<Event> buddyEvents = List.of(savedEvent);
        when(buddyServiceMock.findById(buddy.getId())).thenReturn(buddy);
        when(eventServiceMock.findRecentOfBuddy(buddy.getId(), RECENTLY_LIMIT))
                .thenReturn(buddyEvents);
        mockMvc.perform(get("/app/myEvents/recentOfBuddy/"+buddy.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buddyEvents", buddyEvents))
                .andExpect(view().name("/events/buddyEvents"));
        verify(eventServiceMock).findRecentOfBuddy(buddy.getId(), RECENTLY_LIMIT);
    }
    @Test
    public void givenBuddyId_whenGetBuddy_thenBuddyEventsView() throws Exception {
        List<Event> buddyEvents = List.of(savedEvent);
        when(buddyServiceMock.findById(buddy.getId())).thenReturn(buddy);
        when(eventServiceMock.findAllOfBuddy(buddy))
                .thenReturn(buddyEvents);
        mockMvc.perform(get("/app/myEvents/buddy/"+buddy.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buddyEvents", buddyEvents))
                .andExpect(view().name("/events/buddyEvents"));
        verify(eventServiceMock).findAllOfBuddy(buddy);
    }


}