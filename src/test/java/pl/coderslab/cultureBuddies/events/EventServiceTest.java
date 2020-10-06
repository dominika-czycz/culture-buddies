package pl.coderslab.cultureBuddies.events;

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
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
class EventServiceTest {
    @Autowired
    private EventService testObject;
    @MockBean
    private EventRepository eventRepositoryMock;
    @MockBean
    private EventTypeRepository eventTypeRepositoryMock;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private AddressRepository addressRepositoryMock;

    private Buddy principal;
    private final List<Event> expectedEvents = new ArrayList<>();
    private final EventType typeMusic = new EventType(1L, "music");
    private final EventType typeCinema = new EventType(2L, "cinema");
    private Address addressToSave;
    private Address savedAddress;
    private Event eventToSave;
    private Event savedEvent;
    private static final int LIMIT = 10;

    private Buddy someBuddy;
    private Event someBuddyEvent;
    private final List<Event> expectedEventsOfSomeBuddy = new ArrayList<>();

    @Spy
    private Event savedEventWithBuddiesSpy;
    @Spy
    private Buddy principalSpy;

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        principal = Buddy.builder().username("BestBuddy").id(1243434L).build();
        when(buddyServiceMock.getPrincipal()).thenReturn(principal);
        addressToSave = Address.builder()
                .city("Wrocław")
                .street("Ruska")
                .number("13B")
                .build();
        eventToSave = Event.builder()
                .buddy(principal)
                .eventType(typeMusic)
                .title("an event")
                .address(addressToSave)
                .build();
        savedAddress = addressToSave.toBuilder().id(324234L).build();
        savedEvent = eventToSave.toBuilder().id(34323L).address(savedAddress).build();
        expectedEvents.add(savedEvent);

        principalSpy.setUsername("SpyName");
        principalSpy.setId(7L);
        savedEventWithBuddiesSpy.setId(3333L);
        savedEventWithBuddiesSpy.setTitle("Spy title");

        someBuddy = Buddy.builder()
                .username("someBuddy")
                .id(33673L).build();
        someBuddyEvent = Event.builder()
                .buddy(someBuddy)
                .date(LocalDate.now())
                .eventType(typeCinema)
                .title("Some Buddy event")
                .address(addressToSave)
                .build();
        expectedEventsOfSomeBuddy.add(someBuddyEvent);
    }

    @Test
    void whenGetEventsOfPrincipal_thenEventsOfPrincipalAreGotten() throws NotExistingRecordException {
        //given
        when(eventRepositoryMock.findAllByBuddy(principal)).thenReturn(expectedEvents);
        //when
        final List<Event> actual = testObject.getEventsOfPrincipal();
        //then
        verify(buddyServiceMock).getPrincipal();
        verify(eventRepositoryMock).findAllByBuddy(principal);
        assertThat(actual, is(expectedEvents));
    }

    @Test
    void whenGetJoinedEvents_thenJoinedEventsAreGotten() throws NotExistingRecordException {
        //given
        when(eventRepositoryMock.findAllByBuddiesContains(principal))
                .thenReturn(expectedEvents);
        //when
        final List<Event> actual = testObject.getJoinedEvents();
        //then
        verify(buddyServiceMock).getPrincipal();
        verify(eventRepositoryMock).findAllByBuddiesContains(principal);
        assertThat(actual, is(expectedEvents));
    }

    @Test
    void whenFindAllEventsTypes_thenAllEventsTypesAreFound() {
        //given
        final List<EventType> expectedTypes = List.of(typeMusic, typeCinema);
        when(eventTypeRepositoryMock.findAll()).thenReturn(expectedTypes);
        //when
        final List<EventType> actual = testObject.findAllEventsTypes();
        //then
        verify(eventTypeRepositoryMock).findAll();
        assertThat(actual, is(expectedTypes));
    }

    @Test
    void givenEvent_whenSaveEvent_thenEventAndAddressAreSaved() throws NotExistingRecordException {
        //given
        when(addressRepositoryMock.findFirstByCityAndStreetAndNumberAndFlatNumber(
                addressToSave.getCity(), addressToSave.getStreet(), addressToSave.getNumber(), addressToSave.getFlatNumber()))
                .thenReturn(Optional.empty());
        when(addressRepositoryMock.save(addressToSave)).thenReturn(savedAddress);
        when(eventRepositoryMock.save(eventToSave)).thenReturn(savedEvent);
        //when
        testObject.save(eventToSave);
        //then
        verify(addressRepositoryMock).save(addressToSave);
        verify(eventRepositoryMock).save(eventToSave);
    }

    @Test
    void givenEventWithUpdatedTitle_whenUpdateEvent_thenEventFromDbIsUpdated() throws NotExistingRecordException {
        //given
        final Event eventWithUpdatedData = savedEvent.toBuilder().title("new Title").build();
        when(eventRepositoryMock.findById(eventWithUpdatedData.getId()))
                .thenReturn(Optional.ofNullable(savedEvent));
        final Address eventAddress = savedEvent.getAddress();
        when(addressRepositoryMock.findFirstByCityAndStreetAndNumberAndFlatNumber(
                eventAddress.getCity(), eventAddress.getStreet(), eventAddress.getNumber(), eventAddress.getFlatNumber()))
                .thenReturn(Optional.ofNullable(savedAddress));
        final Event toUpdate = savedEvent.toBuilder().build();
        toUpdate.setTitle(eventWithUpdatedData.getTitle());
        when(eventRepositoryMock.save(toUpdate)).thenReturn(toUpdate);
        //when
        testObject.updateEvent(eventWithUpdatedData);
        //then
        verify(eventRepositoryMock).save(toUpdate);
        verify(addressRepositoryMock, atMost(0)).save(eventAddress);
    }

    @Test
    void givenEventId_whenRemoveEvent_thenEventIsRemoved() throws NotExistingRecordException {
        //given
        final Long eventToDeleteId = savedEvent.getId();
        when(eventRepositoryMock.findById(eventToDeleteId))
                .thenReturn(Optional.ofNullable(savedEvent));
        //when
        testObject.remove(eventToDeleteId);
        //then
        verify(eventRepositoryMock).delete(savedEvent);
    }

    @Test
    void givenEventId_whenLeaveEvent_thenEventIsLeft() throws NotExistingRecordException {
        //given
        savedEventWithBuddiesSpy.addBuddy(principalSpy);
        final Long eventToLeaveId = savedEventWithBuddiesSpy.getId();
        when(eventRepositoryMock.findEventWithBuddies(eventToLeaveId))
                .thenReturn(Optional.of(savedEventWithBuddiesSpy));
        when(buddyServiceMock.getPrincipalWithEvents()).thenReturn(principalSpy);
        //when
        testObject.leave(eventToLeaveId);
        //then
        verify(eventRepositoryMock).save(savedEventWithBuddiesSpy);
        verify(savedEventWithBuddiesSpy).removeBuddy(principalSpy);
    }

    @Test
    void givenUsernameTitleCityAndEventTypeId_whenSearchedForEvents_thenEventAreSearchedByAllGivenData() throws EmptyKeysException, NotExistingRecordException {
        final String username = savedEvent.getBuddy().getUsername();
        final String title = savedEvent.getTitle();
        final Long typeId = savedEvent.getEventType().getId();
        final String city = savedEvent.getAddress().getCity();
        when(eventRepositoryMock.findByUsernameTitleCityAndTypeId(username, title, city, typeId))
                .thenReturn(expectedEvents);
        //when
        final List<Event> actual = testObject.findByUsernameTitleTypeIdOrCity(username, title, typeId, city);
        //then
        verify(eventRepositoryMock).findByUsernameTitleCityAndTypeId(username, title, city, typeId);
        assertThat(actual, is(expectedEvents));
    }

    @Test
    void givenUsernameTitleAndCityButAnyTypeId_whenSearchedForEvents_thenEventAreSearchedByUsernameTitleAndCity() throws EmptyKeysException, NotExistingRecordException {
        final String username = savedEvent.getBuddy().getUsername();
        final String title = savedEvent.getTitle();
        final String city = savedEvent.getAddress().getCity();
        when(eventRepositoryMock.findByUsernameTitleAndCity(username, title, city))
                .thenReturn(expectedEvents);
        //when
        final List<Event> actual = testObject.findByUsernameTitleTypeIdOrCity(username, title, null, city);
        //then
        verify(eventRepositoryMock).findByUsernameTitleAndCity(username, title, city);
        assertThat(actual, is(expectedEvents));
    }

    @Test
    void givenAnyKeys_whenSearchedForEvents_thenEmptyKeysException() {
        final String username = "";
        final String title = "";
        assertThrows(EmptyKeysException.class,
                () -> testObject.findByUsernameTitleTypeIdOrCity(username, title, null, null));
    }

    @Test
    void givenEventId_whenJoinEvent_thenEventIsJoined() throws NotExistingRecordException, RelationshipAlreadyCreatedException {
        //given
        final Long eventToJoinId = savedEventWithBuddiesSpy.getId();
        when(eventRepositoryMock.findEventByBuddies(principalSpy, eventToJoinId))
                .thenReturn(Optional.empty());

        when(buddyServiceMock.getPrincipalWithEvents()).thenReturn(principalSpy);

        when(eventRepositoryMock.findEventWithBuddies(eventToJoinId))
                .thenReturn(Optional.of(savedEventWithBuddiesSpy));
        //when
        testObject.joinEvent(eventToJoinId);
        //then
        verify(eventRepositoryMock).save(savedEventWithBuddiesSpy);
        verify(savedEventWithBuddiesSpy).addBuddy(principalSpy);
    }

    @Test
    void givenAlreadyJoinedEventId_whenJoinEvent_thenAlreadyJoinedExceptionIsThrown()
            throws NotExistingRecordException {
        //given
        final Long eventToJoinId = savedEventWithBuddiesSpy.getId();
        when(eventRepositoryMock.findEventByBuddies(principalSpy, eventToJoinId))
                .thenReturn(Optional.of(savedEventWithBuddiesSpy));
        when(buddyServiceMock.getPrincipalWithEvents()).thenReturn(principalSpy);
        //when, then
        assertThrows(RelationshipAlreadyCreatedException.class,
                () -> testObject.joinEvent(eventToJoinId));
    }

    @Test
    void givenLimitPrincipalBuddy_whenFindRecentlyAddedByBuddies_thenEventsOfBuddiesAreSearched() throws NotExistingRecordException {
        //given
        when(eventRepositoryMock.findRecentlyAddedEventsWithBuddies(principal.getId(), LIMIT)).thenReturn(expectedEvents);
        //when
        final List<Event> actual = testObject.findRecentlyAddedByBuddies(LIMIT);
        //then
        verify(buddyServiceMock).getPrincipal();
        verify(eventRepositoryMock).findRecentlyAddedEventsWithBuddies(principal.getId(), LIMIT);
        assertThat(actual, is(expectedEvents));
    }

    @Test
    void whenFindRecentEvents_thenAllRecentEventsAreFound() {
        //given
        when(eventRepositoryMock.findFirst20ByOrderByDateDesc()).thenReturn(expectedEvents);
        //when
        final List<Event> recentEvents = testObject.findRecentEvents();
        //then
        verify(eventRepositoryMock).findFirst20ByOrderByDateDesc();
        assertThat(recentEvents, is(expectedEvents));
    }

    @Test
    void givenBuddyId_whenFindRecentEventsOfBuddy_thenRecentEventsOfBuddyAreFound() {
        //given
        final Long someBuddyId = someBuddy.getId();
        when(eventRepositoryMock.findRecentWhereBuddyId(someBuddyId, LIMIT)).thenReturn(expectedEventsOfSomeBuddy);
        //when
        final List<Event> recentOfBuddy = testObject.findRecentOfBuddy(someBuddyId, LIMIT);
        //then
        verify(eventRepositoryMock).findRecentWhereBuddyId(someBuddyId, LIMIT);
        assertThat(recentOfBuddy, is(expectedEventsOfSomeBuddy));
    }

    @Test
    void givenBuddy_whenFindAllOfBuddy_thenAllOfBuddyAreFound() {
        //given
        when(eventRepositoryMock.findAllByBuddy(someBuddy))
                .thenReturn(expectedEventsOfSomeBuddy);
        //when
        final List<Event> allRecentOfBuddy = testObject.findAllOfBuddy(someBuddy);
        //then
        verify(eventRepositoryMock).findAllByBuddy(someBuddy);
        assertThat(allRecentOfBuddy, is(expectedEventsOfSomeBuddy));
    }

    @Test
    void givenEventId_whenSearchForEventFetchedWithBuddies_thenEventWithBuddiesAreSearched() throws NotExistingRecordException {
        final Long eventToFindId = someBuddyEvent.getId();
        principal.setEvents(new HashSet<>());
        someBuddy.setEvents(new HashSet<>());
        someBuddyEvent.addBuddy(principal);
        someBuddyEvent.addBuddy(someBuddy);
        when(eventRepositoryMock.findEventWithBuddies(eventToFindId))
                .thenReturn(Optional.ofNullable(someBuddyEvent));
        //when
        final Event withBuddies = testObject.findEventByIdWithBuddies(eventToFindId);
        //then
        verify(eventRepositoryMock).findEventWithBuddies(eventToFindId);
        assertThat(withBuddies, is(someBuddyEvent));
    }

}